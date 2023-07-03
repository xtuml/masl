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

#ifndef PluginRegistry_HH
#define PluginRegistry_HH

#include <vector>
#include <boost/function.hpp>
#include <boost/signals2.hpp>

namespace SWA
{
  // Interface class
  class PluginRegistry
  {
    public:

      typedef boost::function<void()>             Action;
      typedef boost::signals2::signal_type<void(),boost::signals2::keywords::mutex_type<boost::signals2::dummy_mutex> >::type Signal;
      typedef boost::function<bool()>             FlagGetter;
      typedef boost::function<void(bool)>         FlagSetter;
      typedef boost::function<std::string()>      PropertyGetter;
      typedef boost::function<void(std::string)>  PropertySetter;

      static PluginRegistry& getInstance();

      virtual std::vector<std::string> getPlugins() const = 0;

      // Actions
      virtual std::vector<std::string> getActions ( const std::string& pluginName ) const = 0;
      virtual void registerAction ( const std::string& pluginName, const std::string& actionName, const Action& action ) = 0;
      virtual void invokeAction ( const std::string& pluginName, const std::string& actionName ) const = 0;

      // Signals
      virtual std::vector<std::string> getSignals ( const std::string& pluginName ) const = 0;
      virtual void registerSignal         ( const std::string& pluginName, const std::string& signalName, Signal& signal ) = 0;
      virtual void registerSignalAction   ( const std::string& pluginName, const std::string& signalName, const Action& action ) const = 0;

      // Flags
      virtual std::vector<std::string> getFlags(const std::string& pluginName) const = 0;

      virtual void registerFlagGetter ( const std::string& pluginName, const std::string& flagName, const FlagGetter& getter ) = 0;
      virtual void registerFlagSetter ( const std::string& pluginName, const std::string& flagName, const FlagSetter& setter ) = 0;

      virtual bool isWriteableFlag    ( const std::string& pluginName, const std::string& flagName ) const = 0;             
      virtual void setFlag            ( const std::string& pluginName, const std::string& flagName, bool value ) const = 0; 

      virtual bool isReadableFlag     ( const std::string& pluginName, const std::string& flagName ) const = 0;             
      virtual bool getFlag            ( const std::string& pluginName, const std::string& flagName ) const = 0;             
    
      // Properties
      virtual std::vector<std::string> getProperties ( const std::string& pluginName ) const = 0;

      virtual void registerPropertyGetter ( const std::string& pluginName, const std::string& propertyName, const PropertyGetter& getter ) = 0;
      virtual void registerPropertySetter ( const std::string& pluginName, const std::string& propertyName, const PropertySetter& setter ) = 0;

      virtual bool isWriteableProperty    ( const std::string& pluginName, const std::string& propertyName ) const = 0;                           
      virtual void setProperty            ( const std::string& pluginName, const std::string& propertyName, const std::string& value ) const = 0; 

      virtual bool isReadableProperty     ( const std::string& pluginName, const std::string& propertyName ) const = 0;                           
      virtual std::string getProperty     ( const std::string& pluginName, const std::string& propertyName ) const = 0;                           

      virtual ~PluginRegistry() {};

  };


}

#endif
