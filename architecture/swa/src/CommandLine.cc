/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/CommandLine.hh"
#include "boost/operators.hpp"
#include "swa/ProgramError.hh"
#include <format>

namespace SWA {
    NamedOption::NamedOption(
        const std::string &name,
        const std::string &usageText,
        bool required,
        const std::string &paramDescription,
        bool paramRequired,
        bool multipleAllowed
    )
        : name(name),
          aliases(CommandLine::getInstance().getNameAliases(name)),
          defaultValue(CommandLine::getInstance().getDefaultValue(name)),
          required(required && defaultValue.empty()),
          usageText(usageText),
          paramDescription(paramDescription),
          paramRequired(paramRequired && defaultValue.empty()),
          multipleAllowed(multipleAllowed) {
        if (name[0] != '-')
            throw ProgramError("Named Option must begin with '-'");
    }

    std::string NamedOption::getUsage() const {
        std::string usage;
        usage = name;
        for (std::vector<std::string>::const_iterator it = aliases.begin(), end = aliases.end(); it != end; ++it) {
            usage += ", " + *it;
        }
        if (!paramDescription.empty()) {
            std::string param = "<" + paramDescription + ">";
            if (!paramRequired) {
                param = "[" + param + "]";
            }
            usage = usage + " " + param;
        }

        if (!required)
            usage = "[" + usage + "]";

        if (multipleAllowed)
            usage += "...";

        return usage;
    }

    std::string NamedOption::getUsageDetail() const {
        std::string usage(name);
        if (!paramDescription.empty()) {
            usage = usage + " <" + paramDescription + ">";
        }

        usage = usage + " \t: " + usageText;

        if (!defaultValue.empty()) {
            usage += "(default = \"" + defaultValue + "\" )";
        }

        return usage;
    }

    TrailingOption::TrailingOption(const std::string &name, const std::string &usageText, bool required, bool multiple)
        : name(name), usageText(usageText), required(required), multiple(multiple) {}

    std::string TrailingOption::getUsage() const {
        std::string usage;
        usage = "<" + name + ">";

        if (multiple)
            usage += "...";

        if (!required)
            usage = "[" + usage + "]";

        return usage;
    }

    std::string TrailingOption::getUsageDetail() const {
        return "<" + name + "> \t: " + usageText;
    }

    CommandLine::CommandLine() {}

    CommandLine::~CommandLine() {}

    CommandLine &CommandLine::getInstance() {
        static CommandLine instance;
        return instance;
    }

    void CommandLine::registerOption(const NamedOption &option) {
        // Insert option and check that the insert succeeded
        if (!namedOptions.insert(NamedOptions::value_type(option.getName(), option)).second) {
            throw ProgramError("Option " + option.getName() + " already exists.");
        }
    }

    void CommandLine::registerOption(const TrailingOption &option) {
        // Having any options after one that allows multiple values would make it
        // impossible to determine which is which
        if (!trailingOptions.empty() && trailingOptions.back().isMultiple()) {
            throw ProgramError(
                "Only final trailing option can have multiple values. " + option.getUsage() + " supplied after " +
                trailingOptions.back().getUsage() + "."
            );
        }

        // Having an required option after an optional one would make it impossible
        // to determine which is which
        if (!trailingOptions.empty() && !trailingOptions.back().isRequired() && option.isRequired()) {
            throw ProgramError(
                "Cannot have required trailing option after an optional one. " + option.getUsage() +
                " supplied after " + trailingOptions.back().getUsage() + "."
            );
        }

        // Check this option isn't already present
        for (TrailingOptions::const_iterator it = trailingOptions.begin(), end = trailingOptions.end(); it != end;
             ++it) {
            if (it->getName() == option.getName()) {
                throw ProgramError("Option " + option.getName() + " already exists.");
            }
        }

        // Insert the option
        trailingOptions.push_back(option);
    }

    void CommandLine::registerAlias(const std::string &alias, const std::string &name) {
        NamedOptions::iterator option = namedOptions.find(name);

        if (option != namedOptions.end()) {
            option->second.addAlias(alias);
        }
        aliasNames[alias] = name;
        nameAliases[name].push_back(alias);
    }

    void CommandLine::registerDefault(const std::string &name, const std::string &value) {
        NamedOptions::iterator option = namedOptions.find(name);

        if (option != namedOptions.end()) {
            option->second.setDefaultValue(value);
        }
        defaultValues[name] = value;
    }

    std::string CommandLine::getDefaultValue(const std::string &name) const {
        DefaultValues::const_iterator value = defaultValues.find(name);

        if (value != defaultValues.end()) {
            return value->second;
        } else
            return "";
    }

    void CommandLine::parse(int argc, const char *const *argv, bool lenient) {
        parse(*argv, std::vector<std::string>(argv + 1, argv + argc), lenient);
    }

    void CommandLine::parse(const std::string &command, const std::vector<std::string> &options, bool lenient) {
        this->command = command;
        optionValues.clear();
        std::vector<std::string> trailing;

        // Check through all the supplied options
        for (std::vector<std::string>::const_iterator it = options.begin(); it != options.end();) {
            if ((*it)[0] == '-') {
                if (!trailing.empty()) {
                    // We have previously had an option with no associated
                    // -x tag. These may only occur at the end so flag an
                    // error.
                    throw ProgramError(getUsage());
                }

                // Find the option in the list of registered options.
                std::string optionName = *it++;

                if (aliasNames.find(optionName) != aliasNames.end()) {
                    optionName = aliasNames.find(optionName)->second;
                }

                NamedOptions::const_iterator found = namedOptions.find(optionName);
                if (found != namedOptions.end()) {
                    const NamedOption &option = found->second;

                    // Get the value for this option
                    std::string paramValue;
                    if (option.hasParameter()) {
                        if (it != options.end() && (*it)[0] != '-') {
                            // A valid value was supplied
                            paramValue = *it++;
                        } else if (option.getDefaultValue() != "") {
                            paramValue = option.getDefaultValue();
                        } else {
                            // Nothing supplied, check whether it was flagged as
                            // required
                            if (option.isParamRequired()) {
                                throw ProgramError(getUsage());
                            }
                        }
                    }

                    // Add to the list of valid options
                    if (option.isMultipleAllowed() || !optionPresent(option.getName())) {
                        optionValues.insert(OptionValues::value_type(option.getName(), paramValue));
                    } else {
                        throw ProgramError(getUsage());
                    }
                } else {
                    // Not an option we know about, so see if it has a
                    // value and register it anyway. There is a
                    // possibility that we will leave an extra trailing
                    // value on this pass, but that should all get
                    // sorted when a strict parse has happened. Nothing
                    // that happens before the strict parse can rely on
                    // trailing values being correct.
                    if (!lenient)
                        throw ProgramError(getUsage());
                    std::string paramValue;
                    if (it != options.end() && (*it)[0] != '-' && (it + 1) != options.end() && (*(it + 1))[0] == '-') {
                        // next value is not an option, and the one after is
                        paramValue = *it++;
                    }
                    optionValues.insert(OptionValues::value_type(optionName, paramValue));
                }
            } else {
                trailing.push_back(*it++);
            }
        }

        // Check for any options that weren't supplied
        for (NamedOptions::const_iterator it = namedOptions.begin(); it != namedOptions.end(); ++it) {
            OptionValues::const_iterator found = optionValues.find(it->first);
            if (found == optionValues.end()) {
                if (it->second.isRequired()) {
                    throw ProgramError(getUsage());
                }
            }
        }

        // Sort out trailing values
        std::vector<std::string>::const_iterator valueIt = trailing.begin();
        for (TrailingOptions::const_iterator it = trailingOptions.begin(); it != trailingOptions.end(); ++it) {
            if (valueIt == trailing.end()) {
                if (it->isRequired()) {
                    throw ProgramError(getUsage());
                }
            } else {
                optionValues.insert(OptionValues::value_type(it->getName(), *valueIt++));

                if (it->isMultiple()) {
                    while (valueIt != trailing.end()) {
                        optionValues.insert(OptionValues::value_type(it->getName(), *valueIt++));
                    }
                }
            }
        }

        // Check for no extra trailing values
        if (valueIt != trailing.end()) {
            if (!lenient)
                throw ProgramError(getUsage());
        }
    }

    std::string CommandLine::getOption(const std::string &name, const std::string &defaultVal) const {
        OptionValues::const_iterator it = optionValues.find(name);
        if (it == optionValues.end()) {
            DefaultValues::const_iterator dit = defaultValues.find(name);
            if (dit == defaultValues.end() || dit->second.empty()) {
                return defaultVal;
            } else {
                return dit->second;
            }
        } else {
            return it->second;
        }
    }

    template <class InnerIterator>
    class PairSecondIterator
        : public boost::
              input_iteratable<PairSecondIterator<InnerIterator>, typename InnerIterator::value_type::second_type *> {
      public:
        using iterator_category = std::input_iterator_tag;
        using value_type = typename InnerIterator::value_type::second_type;
        using difference_type = std::ptrdiff_t;
        using pointer = value_type *;
        using reference = value_type &;

        PairSecondIterator(const InnerIterator &pos)
            : pos(pos) {}
        const typename InnerIterator::value_type::second_type &operator*() const {
            return pos->second;
        }
        PairSecondIterator &operator++() {
            ++pos;
            return *this;
        }
        bool operator==(const PairSecondIterator &rhs) const {
            return pos == rhs.pos;
        }

      private:
        InnerIterator pos;
    };

    std::vector<std::string> CommandLine::getMultiOption(const std::string &name, const std::string &defaultVal) const {
        std::pair<OptionValues::const_iterator, OptionValues::const_iterator> range = optionValues.equal_range(name);

        std::vector<std::string> result(
            PairSecondIterator<OptionValues::const_iterator>(range.first),
            PairSecondIterator<OptionValues::const_iterator>(range.second)
        );

        if (result.empty() && !defaultVal.empty()) {
            result.push_back(defaultVal);
        }
        return result;
    }

    int CommandLine::getIntOption(const std::string &name, int defaultVal) const {
        std::string val = getOption(name);

        return val.empty() ? defaultVal : std::stoi(val);
    }

    bool CommandLine::optionPresent(const std::string &name) const {
        return optionValues.find(name) != optionValues.end() || defaultValues.find(name) != defaultValues.end();
    }

    std::string CommandLine::getUsage() const {
        std::string usage = "Usage: " + command;
        std::string detail;

        for (NamedOptions::const_iterator it = namedOptions.begin(); it != namedOptions.end(); ++it) {
            usage += " " + it->second.getUsage();
            detail += "\n  " + it->second.getUsageDetail();
        }

        std::string trailing;
        for (TrailingOptions::const_iterator it = trailingOptions.begin(); it != trailingOptions.end(); ++it) {
            usage += " " + it->getUsage();
            detail += "\n  " + it->getUsageDetail();
        }
        usage += detail + "\n";

        return usage;
    }
} // namespace SWA
