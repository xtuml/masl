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

#include "CodeCoverage.hh"
#include "metadata/MetaData.hh"
#include "swa/NameFormatter.hh"
#include "swa/PluginRegistry.hh"
#include "swa/Process.hh"
#include "swa/Stack.hh"
#include "swa/String.hh"
#include "swa/Timestamp.hh"
#include <format>
#include <fstream>
#include <iostream>


namespace {
bool registered = CodeCoverage::CodeCoverage::getInstance().initialise();

}

namespace CodeCoverage {
CodeCoverage::CodeCoverage()
    : active(true), startTime(SWA::Timestamp::now()), samplingTime(),
      processStack(SWA::Stack::getInstance())

{}

CodeCoverage &CodeCoverage::getInstance() {
    static CodeCoverage singleton;
    return singleton;
}

bool CodeCoverage::initialise() {
    SWA::PluginRegistry::getInstance().registerAction(
        getName(), "Print Report", [this]() { printReport(); });
    SWA::PluginRegistry::getInstance().registerPropertySetter(
        getName(), "Save Report to",
        [this](const std::string &name) { saveReport(name); });
    SWA::PluginRegistry::getInstance().registerAction(
        getName(), "Clear Statistics", [this]() { clearStats(); });
    SWA::PluginRegistry::getInstance().registerFlagSetter(
        getName(), "Active", [this](bool active) { setActive(active); });
    SWA::PluginRegistry::getInstance().registerFlagGetter(
        getName(), "Active", [this]() { return isActive(); });

    SWA::Process::getInstance().registerShutdownListener([this]() {
        std::string finalReportFile =
            std::format("{}_code_coverage.{}.xml",
                        SWA::Process::getInstance().getName(), getpid());
        saveReport(finalReportFile);
    });

    registerMonitor();
    connectToMonitor();
    return true;
}

void CodeCoverage::startStatement() {
    if (active) {
        timeStack.push_back(Time(StackFrame(processStack.top())));
    }
}

void CodeCoverage::endStatement() {
    if (active) {
        statistics[timeStack.back().getFrame()] += timeStack.back();
        timeStack.pop_back();
    }
}

CodeCoverage::StackFrame::StackFrame(const SWA::StackFrame &frame)
    : type(frame.getType()), domain(frame.getDomainId()),
      object(frame.getObjectId()), action(frame.getActionId()),
      line(frame.getLine()) {}

CodeCoverage::StackFrame::StackFrame(SWA::StackFrame::ActionType type,
                                     int domain, int object, int action,
                                     int line)
    : type(type), domain(domain), object(object), action(action), line(line) {}

std::ostream &operator<<(std::ostream &stream,
                         const CodeCoverage::StackFrame &rhs) {
    stream << SWA::NameFormatter::formatStackFrame(rhs.type, rhs.domain,
                                                   rhs.object, rhs.action);
    if (rhs.line > 0)
        stream << "-" << rhs.line;
    return stream;
}

void CodeCoverage::addLineXML(pugi::xml_node &parent, StackFrame frame) const {
    auto statement = parent.append_child("statement");
    statement.append_attribute("line") = frame.getLine();

    Statistics::const_iterator statIt = statistics.find(frame);

    if (statIt != statistics.end()) {
        statement.append_child("count").text() = statIt->second.getCount();
        statement.append_child("real").text() =
            statIt->second.getReal().nanos();
        statement.append_child("user").text() =
            statIt->second.getUser().nanos();
        statement.append_child("system").text() =
            statIt->second.getSystem().nanos();
    }
}

void CodeCoverage::addServiceXML(pugi::xml_node &parent,
                                 const SWA::ServiceMetaData &service,
                                 SWA::StackFrame::ActionType type, int domain,
                                 int object) const {
    auto serviceElement = parent.append_child("service");
    serviceElement.append_attribute("name") = service.getName().c_str();
    serviceElement.append_attribute("filename") = service.getFileName().c_str();
    serviceElement.append_attribute("md5sum") = service.getFileHash().c_str();

    for (std::vector<int>::const_iterator it = service.getLines().begin();
         it != service.getLines().end(); ++it) {
        addLineXML(serviceElement,
                   StackFrame(type, domain, object, service.getId(), *it));
    }
}

void CodeCoverage::addStateXML(pugi::xml_node &parent,
                               const SWA::StateMetaData &state, int domain,
                               int object) const {

    auto stateElement = parent.append_child("state");
    stateElement.append_attribute("name") = state.getName().c_str();
    stateElement.append_attribute("filename") = state.getFileName().c_str();
    stateElement.append_attribute("md5sum") = state.getFileHash().c_str();
    for (std::vector<int>::const_iterator it = state.getLines().begin();
         it != state.getLines().end(); ++it) {
        addLineXML(stateElement,
                   StackFrame(SWA::StackFrame::StateAction, domain, object,
                              state.getId(), *it));
    }
}

void CodeCoverage::writeReport(std::ostream &stream) const {
    pugi::xml_document xmlDocument;

    auto processElement = xmlDocument.append_child("process");
    processElement.append_attribute("name") =
        SWA::Process::getInstance().getName().c_str();
    processElement.append_child("time").text() =
        SWA::Timestamp::now()
            .format_iso_ymdhms(SWA::Timestamp::Second, 9, true)
            .c_str();

    uint64_t totalTime = samplingTime.nanos();
    if (active) {
        totalTime += (SWA::Timestamp::now() - startTime).nanos();
    }
    processElement.append_child("duration").text() = totalTime;

    const SWA::ProcessMetaData &process = SWA::ProcessMetaData::getProcess();

    for (const auto &pdomain : SWA::Process::getInstance().getDomains()) {

        const SWA::DomainMetaData &domain = process.getDomain(pdomain.getId());
        auto domainElement = processElement.append_child("domain");
        domainElement.append_attribute("name") = domain.getName().c_str();

        for (const auto &service : domain.getServices()) {
            addServiceXML(domainElement, service,
                          SWA::StackFrame::DomainService, domain.getId(), -1);
        }

        for (const auto &term : domain.getTerminators()) {
            auto termElement = domainElement.append_child("terminator");
            termElement.append_attribute("name") = term.getName().c_str();

            for (const auto &service : term.getServices()) {
                addServiceXML(termElement, service,
                              SWA::StackFrame::TerminatorService,
                              domain.getId(), term.getId());
            }
        }

        for (const auto &object : domain.getObjects()) {

            auto objectElement = domainElement.append_child("object");
            objectElement.append_attribute("name") = object.getName().c_str();

            for (const auto &service : object.getServices()) {
                addServiceXML(objectElement, service,
                              SWA::StackFrame::ObjectService, domain.getId(),
                              object.getId());
            }

            for (const auto &state : object.getStates()) {
                addStateXML(objectElement, state, domain.getId(),
                            object.getId());
            }
        }
    }

    xmlDocument.save(stream, "  ");
}

void CodeCoverage::printReport() const { writeReport(std::cout); }

void CodeCoverage::saveReport(const std::string &filename) const {
    std::ofstream file(filename.c_str());
    if (file) {
        writeReport(file);
        std::cout << "Code Coverage report saved to " + filename << std::endl;
    } else {
        std::cout << "Failed to save Code Coverage report to " + filename
                  << std::endl;
    }
}

void CodeCoverage::setActive(bool flag) {
    if (active && !flag) {
        samplingTime += SWA::Timestamp::now() - startTime;
    } else if (!active && flag) {
        startTime = SWA::Timestamp::now();
    }
    active = flag;
}

CodeCoverage::~CodeCoverage() {}

CodeCoverage::Time::Time(const StackFrame &frame)
    : real(SWA::Duration::real()), user(SWA::Duration::user()),
      system(SWA::Duration::system()), frame(frame) {}

CodeCoverage::Statistic::Statistic() : count(), real(), user(), system() {}

void CodeCoverage::Statistic::operator+=(const Time &startTime) {
    ++count;
    real += SWA::Duration::real() - startTime.getReal();
    user += SWA::Duration::user() - startTime.getUser();
    system += SWA::Duration::system() - startTime.getSystem();
}
} // namespace CodeCoverage
