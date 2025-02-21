/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include <format>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <map>
#include <print>
#include <pugixml.hpp>
#include <stdint.h>
#include <string>
#include <vector>

class CoverageData {
  public:
    CoverageData(const std::string &fileName, const std::string &md5sum)
        : fileName(fileName), md5sum(md5sum), lineCounts() {}

    const std::string &getFileName() const { return fileName; }
    const std::string &getMd5sum() const { return md5sum; }

    void addLineCount(int line, int count) { lineCounts[line] += count; }
    void addReal(int line, uint64_t duration) { real[line] += duration; }
    void addUser(int line, uint64_t duration) { user[line] += duration; }
    void addSystem(int line, uint64_t duration) { system[line] += duration; }

    bool hasLine(int line) const {
        return lineCounts.find(line) != lineCounts.end();
    }
    int getLineCount(int line) const { return lineCounts.find(line)->second; }
    const uint64_t getRealNanos(int line) const {
        return real.find(line)->second;
    }
    const uint64_t getUserNanos(int line) const {
        return user.find(line)->second;
    }
    const uint64_t getSystemNanos(int line) const {
        return system.find(line)->second;
    }

  private:
    std::string fileName;
    std::string md5sum;
    std::map<int, int> lineCounts;
    std::map<int, uint64_t> real;
    std::map<int, uint64_t> user;
    std::map<int, uint64_t> system;
};

using namespace std::literals;

struct CoverageParser {
  public:
    void parse(std::string xmlFile) {
        pugi::xml_document doc;
        auto result = doc.load_file(xmlFile.c_str());
        if (!result) {
            std::println(stderr, "XML Parse Failed: {}: {}", xmlFile,
                         result.description());
            return;
        }

        for (const auto &process : doc.children("process")) {
            for (const auto &value : process.children("time")) {
                reportTime = value.text().as_string();
            }
            for (const auto &value : process.children("duration")) {
                reportDuration = value.text().as_string();
            }

            for (const auto &domain : process.children("domain")) {
                parseDomain(domain);
            }
        }
    }
    void parseDomain(const pugi::xml_node &domain) {
        for (const auto &terminator : domain.children("terminator")) {
            for (const auto &service : domain.children("service")) {
                parseService(std::format("{}::{}~>{}", name(domain),
                                         name(terminator), name(service)),
                             service);
            }
        }
        for (const auto &object : domain.children("object")) {
            for (const auto &service : domain.children("service")) {
                parseService(std::format("{}::{}.{}", name(domain),
                                         name(object), name(service)),
                             service);
            }
            for (const auto &state : domain.children("state")) {
                parseService(std::format("{}::{}.{}", name(domain),
                                         name(object), name(state)),
                             state);
            }
        }
        for (const auto &service : domain.children("service")) {
            parseService(std::format("{}::{}", name(domain), name(service)),
                         service);
        }
    }

    void parseService(const std::string &name, const pugi::xml_node &service) {
        std::string filename = service.attribute("filename").as_string();
        std::string md5sum = service.attribute("md5sum").as_string();

        auto actionIt = coverage.find(name);
        if (actionIt == coverage.end()) {
            actionIt =
                coverage.emplace(name, CoverageData(filename, md5sum)).first;
        } else {
            if (actionIt->second.getFileName() != filename ||
                actionIt->second.getMd5sum() != md5sum) {
                std::println(stderr,
                             "Inconsistent XML - filenames or md5sums do not "
                             "match for {}",
                             name);
            }
        }
        auto &action = actionIt->second;

        for (const auto &statement : service.children("statement")) {
            auto line = statement.attribute("line").as_int();
            action.addLineCount(line, 0);
            for (auto &v : statement.children("count")) {
                action.addLineCount(line, v.text().as_int(0));
            }
            for (auto &v : statement.children("real")) {
                action.addReal(line, v.text().as_int(0));
            }
            for (auto &v : statement.children("user")) {
                action.addUser(line, v.text().as_int(0));
            }
            for (auto &v : statement.children("system")) {
                action.addSystem(line, v.text().as_int(0));
            }
        }
    }

    typedef std::map<std::string, CoverageData> Coverage;
    const Coverage &getCoverage() const { return coverage; }

    const std::string &getReportTime() const { return reportTime; }
    const std::string &getReportDuration() const { return reportDuration; }

  private:
    std::string name(const pugi::xml_node &node) {
        return node.attribute("name").as_string();
    }

    Coverage coverage{};
    std::string reportTime{};
    std::string reportDuration{};
};

class TextReport {
  public:
    TextReport(const CoverageParser &parser);
    void reportLine(std::ostream &stream, const CoverageData &data, int lineNo,
                    const std::string &lineText);

    void write(std::ostream &stream) const;

  private:
    std::ostringstream report;
};

void TextReport::reportLine(std::ostream &stream, const CoverageData &data,
                            int lineNo, const std::string &lineText) {
    if (data.hasLine(lineNo)) {
        int count = data.getLineCount(lineNo);

        if (count) {
            stream << std::setw(8) << count << " : ";
            int64_t realMicros = data.getRealNanos(lineNo) / 1000;
            int64_t userMicros = data.getUserNanos(lineNo) / 1000;
            int64_t systemMicros = data.getSystemNanos(lineNo) / 1000;
            stream << std::fixed << std::setprecision(3);
            if (realMicros > 0) {
                stream << std::setw(9) << realMicros / 1.0e3 / count << "/"
                       << std::setw(9) << realMicros / 1.0e3 << " : ";
            } else {
                stream << std::setw(9 + 1 + 9) << " " << " : ";
            }
            if (userMicros > 0) {
                stream << std::setw(9) << userMicros / 1.0e3 / count << "/"
                       << std::setw(9) << userMicros / 1.0e3 << " : ";
            } else {
                stream << std::setw(9 + 1 + 9) << " " << " : ";
            }
            if (systemMicros > 0) {
                stream << std::setw(9) << systemMicros / 1.0e3 / count << "/"
                       << std::setw(9) << systemMicros / 1.0e3 << " : ";
            } else {
                stream << std::setw(9 + 1 + 9) << " " << " : ";
            }
        } else {
            stream << std::setw(8) << "########" << " : ";
            stream << std::setw(9 + 1 + 9) << " " << " : ";
            stream << std::setw(9 + 1 + 9) << " " << " : ";
            stream << std::setw(9 + 1 + 9) << " " << " : ";
        }
    } else {
        stream << std::setw(8) << "        " << " : ";
        stream << std::setw(9 + 1 + 9) << " " << " : ";
        stream << std::setw(9 + 1 + 9) << " " << " : ";
        stream << std::setw(9 + 1 + 9) << " " << " : ";
    }

    stream << std::setw(5) << lineNo << " : ";
    stream << lineText << "\n";
}

TextReport::TextReport(const CoverageParser &parser) {
    report << "Generated at " << parser.getReportTime() << " sampled over "
           << std::stoul(parser.getReportDuration()) / 1e9 << " seconds\n";

    for (const auto &[name, action] : parser.getCoverage()) {
        std::ifstream file(action.getFileName().c_str());

        // Poco::MD5Engine md5summer;
        // Poco::DigestInputStream md5Stream(md5summer, file);

        std::string lineText;
        int lineNo = 1;

        int executedLines = 0;
        int significantLines = 0;

        std::ostringstream fileDetail;

        while (std::getline(file, lineText)) {
            reportLine(fileDetail, action, lineNo, lineText);
            if (action.hasLine(lineNo)) {
                if (action.getLineCount(lineNo)) {
                    ++executedLines;
                }
                ++significantLines;
            }
            ++lineNo;
        }
        if (significantLines) {
            double percentage = executedLines * 100.0 / significantLines;
            report << name << " : " << std::setprecision(4) << percentage
                   << "%\n";
            report << std::setw(8) << "Count" << " : ";
            report << std::setw(9 + 1 + 9) << "Ave/Tot Real (ms)" << " : ";
            report << std::setw(9 + 1 + 9) << "Ave/Tot User (ms)" << " : ";
            report << std::setw(9 + 1 + 9) << "Ave/Tot System (ms)"
                   << " : ";
            report << std::setw(5) << "Line" << " :\n";

            // std::string md5sum = md5summer.digestToHex(md5summer.digest());
            // if (action.getMd5sum() != md5sum) {
            //     report << "!!!!!!! File content does not match the one used "
            //               "for coverage analysis !!!!!!\n";
            //     report << "!!!!!!! Coverage md5 = " << action.getMd5sum()
            //            << "!!!!!!\n";
            //     report << "!!!!!!! " << action.getFileName()
            //            << " md5 = " << md5sum << "!!!!!!\n";
            // }
            report << fileDetail.str();
            report << std::string(80, '-') << "\n";
        }
    }
}

void TextReport::write(std::ostream &stream) const {
    stream << report.str() << std::flush;
}

int main(int argc, char **argv) {
    std::vector<std::string> xmlFiles(argv + 1, argv + argc);

    CoverageParser parser;

    for (const auto &filename : xmlFiles) {
        parser.parse(filename);
    }

    TextReport report(parser);
    report.write(std::cout);
}
