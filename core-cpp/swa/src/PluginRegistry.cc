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

#include "swa/PluginRegistry.hh"
#include "swa/ProgramError.hh"
#include <boost/unordered_map.hpp>
#include <boost/operators.hpp>

namespace SWA
{
  template<class InnerIterator>
  class KeyIterator : public boost::input_iteratable<KeyIterator<InnerIterator>,typename InnerIterator::value_type::first_type*>
  {
    public:
      using iterator_category = std::input_iterator_tag;
      using value_type = typename InnerIterator::value_type::first_type;
      using difference_type = std::ptrdiff_t;
      using pointer =  value_type*;
      using reference = value_type&;


      KeyIterator ( const InnerIterator& pos ) : pos(pos) {}
      const typename InnerIterator::value_type::first_type& operator*() const { return pos->first; }
      KeyIterator& operator++()  { ++pos; return *this; }
      bool operator== ( const KeyIterator& rhs ) const { return pos == rhs.pos; }

    private:
      InnerIterator pos;
  };

  template<class T>
  class Lookup
  {
    public:
      std::vector<std::string> getNames() const;

      const T& get ( const std::string& name ) const;
      T& get ( const std::string& name );

    private:
      typedef typename boost::unordered_map<std::string,T> Table;
      Table table;

  };

  class Plugin
  {
    public:
      typedef PluginRegistry::Action         Action;
      typedef PluginRegistry::Signal         Signal;
      typedef PluginRegistry::FlagGetter     FlagGetter;
      typedef PluginRegistry::FlagSetter     FlagSetter;
      typedef PluginRegistry::PropertyGetter PropertyGetter;
      typedef PluginRegistry::PropertySetter PropertySetter;

      std::vector<std::string> getActions() const { return actions.getNames(); }

      void registerAction ( const std::string& actionName, const Action& action ) { actions.get(actionName) = action; }

      void invokeAction ( const std::string& actionName ) const { actions.get(actionName)(); }

      // Signals
      std::vector<std::string> getSignals() const { return signals.getNames(); }

      void registerSignal ( const std::string& signalName, Signal& signal ) { signals.get(signalName) = &signal; }

      void registerSignalCallback ( const std::string& signalName, const Action& action ) const { signals.get(signalName)->connect(action); }

      // Flags
      std::vector<std::string> getFlags() const                                           { return flags.getNames(); } 

      void registerFlagGetter ( const std::string& flagName, const FlagGetter& getter )   { flags.get(flagName).setGetter(getter); } 
      void registerFlagSetter ( const std::string& flagName, const FlagSetter& setter )   { flags.get(flagName).setSetter(setter); } 

      bool isWriteableFlag ( const std::string& flagName ) const                          { return flags.get(flagName).isWriteable(); } 
      void setFlag ( const std::string& flagName, bool value ) const                      { flags.get(flagName).setValue(value); } 

      bool isReadableFlag ( const std::string& flagName ) const                           { return flags.get(flagName).isReadable(); } 
      bool getFlag ( const std::string& flagName ) const                                  { return flags.get(flagName).getValue(); } 

      // Properties
      std::vector<std::string> getProperties() const                                                     { return properties.getNames(); } 

      void registerPropertyGetter ( const std::string& propertyName, const PropertyGetter& getter )      { properties.get(propertyName).setGetter(getter); } 
      void registerPropertySetter ( const std::string& propertyName, const PropertySetter& setter )      { properties.get(propertyName).setSetter(setter); } 

      bool isWriteableProperty ( const std::string& propertyName ) const                                 { return properties.get(propertyName).isWriteable(); } 
      void setProperty ( const std::string& propertyName, const std::string& value ) const               { properties.get(propertyName).setValue(value); } 

      bool isReadableProperty ( const std::string& propertyName ) const                                  { return properties.get(propertyName).isReadable(); } 
      std::string getProperty ( const std::string& propertyName ) const                                  { return properties.get(propertyName).getValue(); } 

    private:
      template<class T>
      class Value
      {
        public:
          typedef boost::function<T()>     Getter;
          typedef boost::function<void(T)> Setter;

          Value () : getter(), setter() {}
          Value ( const Getter& getter, const Setter& setter ) : getter(getter), setter(setter) {}
          Value ( const Setter& setter ) : getter(), setter(setter) {}
          Value ( const Getter& getter ) : getter(getter), setter() {}

          void setSetter ( const Setter& setter ) { this->setter = setter; } 
          void setGetter ( const Getter& getter ) { this->getter = getter; } 
        
          void setValue( const T& value ) const { setter(value); }
          T getValue() const { return getter(); }

          bool isWriteable() const { return !setter.empty(); }
          bool isReadable() const  { return !getter.empty(); }

      private:
          Getter getter;
          Setter setter;


      };

      typedef Value<bool>         Flag;
      typedef Value<std::string > Property;

      typedef Lookup<Action>         ActionTable;
      typedef Lookup<Signal*>        SignalTable;
      typedef Lookup<Flag>           FlagTable;
      typedef Lookup<Property>       PropertyTable;

      ActionTable   actions;
      SignalTable   signals;
      FlagTable     flags;
      PropertyTable properties;
  };


  class PluginRegistryImpl : public PluginRegistry
  {
    private:
      virtual std::vector<std::string> getPlugins() const                                                                             { return plugins.getNames(); }

      // Actions
      virtual std::vector<std::string> getActions(const std::string& pluginName) const                                                { return plugins.get(pluginName).getActions(); }
      virtual void registerAction ( const std::string& pluginName, const std::string& actionName, const Action& action )              { plugins.get(pluginName).registerAction(actionName,action); }
      virtual void invokeAction ( const std::string& pluginName, const std::string& actionName ) const                                { plugins.get(pluginName).invokeAction(actionName); }
 
      // Signals
      virtual std::vector<std::string> getSignals(const std::string& pluginName) const                                                { return plugins.get(pluginName).getSignals(); }
      virtual void registerSignal ( const std::string& pluginName, const std::string& signalName, Signal& signal )                    { plugins.get(pluginName).registerSignal(signalName,signal); }
      virtual void registerSignalAction ( const std::string& pluginName, const std::string& signalName, const Action& action ) const  { plugins.get(pluginName).registerSignalCallback(signalName,action); };

      // Flags
      virtual std::vector<std::string> getFlags(const std::string& pluginName) const { return plugins.get(pluginName).getFlags(); }

      virtual void registerFlagGetter ( const std::string& pluginName, const std::string& flagName, const FlagGetter& getter ) { plugins.get(pluginName).registerFlagGetter(flagName,getter); }
      virtual void registerFlagSetter ( const std::string& pluginName, const std::string& flagName, const FlagSetter& setter ) { plugins.get(pluginName).registerFlagSetter(flagName,setter); }

      virtual bool isWriteableFlag    ( const std::string& pluginName, const std::string& flagName ) const                     { return plugins.get(pluginName).isWriteableFlag(flagName); }
      virtual void setFlag            ( const std::string& pluginName, const std::string& flagName, bool value ) const         { plugins.get(pluginName).setFlag(flagName,value); }

      virtual bool isReadableFlag     ( const std::string& pluginName, const std::string& flagName ) const                     { return plugins.get(pluginName).isReadableFlag(flagName); }
      virtual bool getFlag            ( const std::string& pluginName, const std::string& flagName ) const                     { return plugins.get(pluginName).getFlag(flagName); }

      // Properties
      virtual std::vector<std::string> getProperties(const std::string& pluginName) const { return plugins.get(pluginName).getProperties(); };

      virtual void registerPropertyGetter ( const std::string& pluginName, const std::string& propertyName, const PropertyGetter& getter )    { plugins.get(pluginName).registerPropertyGetter(propertyName,getter); }
      virtual void registerPropertySetter ( const std::string& pluginName, const std::string& propertyName, const PropertySetter& setter )    { plugins.get(pluginName).registerPropertySetter(propertyName,setter); }

      virtual bool isWriteableProperty    ( const std::string& pluginName, const std::string& propertyName ) const                            { return plugins.get(pluginName).isWriteableProperty(propertyName); }
      virtual void setProperty            ( const std::string& pluginName, const std::string& propertyName, const std::string& value ) const  { plugins.get(pluginName).setProperty(propertyName,value); }

      virtual bool isReadableProperty     ( const std::string& pluginName, const std::string& propertyName ) const                            { return plugins.get(pluginName).isReadableProperty(propertyName); }
      virtual std::string getProperty     ( const std::string& pluginName, const std::string& propertyName ) const                            { return plugins.get(pluginName).getProperty(propertyName); }


    private:
      typedef Lookup<Plugin> PluginTable;
      PluginTable plugins;
  };



  template<class T>
  std::vector<std::string> Lookup<T>::getNames() const
  {
    typedef KeyIterator<typename Table::const_iterator> NameIterator;
    return std::vector<std::string>(NameIterator(table.begin()),NameIterator(table.end()));
  }

  template<class T>
  T& Lookup<T>::get( const std::string& name )
  {
    typename Table::iterator it = table.find(name);
    if ( it == table.end() )
    {
      it = table.insert(typename Table::value_type(name,T())).first;
    }
    return it->second;
  }
    
  template<class T>
  const T& Lookup<T>::get( const std::string& name ) const
  {
    typename Table::const_iterator it = table.find(name);
    if ( it == table.end() )
    {
      throw ProgramError ( name + " not found." );
    }
    return it->second;
  }
    

  PluginRegistry& PluginRegistry::getInstance()
  {
    static PluginRegistryImpl instance;
    return instance;
  }

}
