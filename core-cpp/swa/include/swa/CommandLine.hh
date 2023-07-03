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

#ifndef CommandLine_
#define CommandLine_

#include <map>
#include <string>
#include <vector>


namespace SWA
{
  class NamedOption
  {
    public:
      NamedOption ( const std::string& name, const std::string& usageText, bool required = false, const std::string& paramDescription = "", bool paramRequired = false, bool multipleAllowed = false );

      const std::string& getName() const { return name; }
      bool isRequired() const { return required; }

      const std::string& getParamDescription() const { return paramDescription; }
      bool hasParameter() const { return !paramDescription.empty(); }
      bool isParamRequired() const { return paramRequired; }
      bool isMultipleAllowed() const { return multipleAllowed; }
      const std::string& getDefaultValue() const { return defaultValue; }

      void setDefaultValue( const std::string& defaultValue ) { this->defaultValue = defaultValue; required = false; paramRequired = false; } 
      void addAlias ( const std::string& alias ) { aliases.push_back(alias); }
      std::string getUsage() const;
      std::string getUsageDetail() const;

    private:
      std::string name;
      std::vector<std::string> aliases;
      std::string defaultValue;
      bool required;
      std::string usageText;
      std::string paramDescription;
      bool paramRequired;
      bool multipleAllowed;
  };

  class TrailingOption
  {
    public:
      TrailingOption ( const std::string& name, const std::string& usageText, bool required = true, bool multiple = false );

      const std::string& getName() const { return name; }
      bool isRequired() const { return required; }
      bool isMultiple() const { return multiple; }

      std::string getUsage() const;
      std::string getUsageDetail() const;
 
    private:
      std::string name;
      std::string usageText;
      bool required;
      bool multiple;
  };


  class CommandLine
  {

    public:
      static CommandLine&  getInstance();

    public:
      std::string getOption ( const std::string& name, const std::string& defaultValue = "" ) const;
      std::vector<std::string> getMultiOption ( const std::string& name, const std::string& defaultValue = "" ) const;

      int getIntOption ( const std::string& name, int defaultValue = 0 ) const;    
      bool optionPresent ( const std::string& name ) const;

      void parse( int argc, const char* const * argv, bool lenient );
      void parse( const std::string& command, const std::vector<std::string>& options, bool lenient );

      const std::string& getCommand() const { return command; }

      void registerOption ( const NamedOption& option );
      void registerOption ( const TrailingOption& option );

      void registerAlias   ( const std::string& alias, const std::string& name );
      void registerDefault ( const std::string& name, const std::string& value );

      const std::vector<std::string>& getNameAliases ( const std::string& name ) { return nameAliases[name]; }
      std::string getDefaultValue ( const std::string& name ) const;

      std::string getUsage() const;

    private:
        CommandLine();
       ~CommandLine();

        CommandLine(const CommandLine& rhs);
        CommandLine& operator=(const CommandLine& rhs);

    private:
      typedef std::vector<TrailingOption> TrailingOptions;
      typedef std::map<std::string,NamedOption> NamedOptions;
      typedef std::multimap<std::string,std::string> OptionValues;

      typedef std::map<std::string,std::string> AliasNames;
      typedef std::map<std::string,std::vector<std::string> > NameAliases;
      typedef std::map<std::string,std::string> DefaultValues;

      TrailingOptions trailingOptions;
      bool trailingContinued;
      bool trailingRequired;
      NamedOptions namedOptions;
      std::string command;
      OptionValues optionValues;

      AliasNames aliasNames;
      NameAliases nameAliases;
      DefaultValues defaultValues;

  };

}
#endif
