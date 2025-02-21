/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef PluginRegistry_HH
#define PluginRegistry_HH

#include <functional>
#include <boost/signals2.hpp>
#include <vector>

namespace SWA {
// Interface class
class PluginRegistry {
  public:
    typedef std::function<void()> Action;
    typedef boost::signals2::signal_type<
        void(), boost::signals2::keywords::mutex_type<
                    boost::signals2::dummy_mutex>>::type Signal;
    typedef std::function<bool()> FlagGetter;
    typedef std::function<void(bool)> FlagSetter;
    typedef std::function<std::string()> PropertyGetter;
    typedef std::function<void(std::string)> PropertySetter;

    static PluginRegistry &getInstance();

    virtual std::vector<std::string> getPlugins() const = 0;

    // Actions
    virtual std::vector<std::string>
    getActions(const std::string &pluginName) const = 0;
    virtual void registerAction(const std::string &pluginName,
                                const std::string &actionName,
                                const Action &action) = 0;
    virtual void invokeAction(const std::string &pluginName,
                              const std::string &actionName) const = 0;

    // Signals
    virtual std::vector<std::string>
    getSignals(const std::string &pluginName) const = 0;
    virtual void registerSignal(const std::string &pluginName,
                                const std::string &signalName,
                                Signal &signal) = 0;
    virtual void registerSignalAction(const std::string &pluginName,
                                      const std::string &signalName,
                                      const Action &action) const = 0;

    // Flags
    virtual std::vector<std::string>
    getFlags(const std::string &pluginName) const = 0;

    virtual void registerFlagGetter(const std::string &pluginName,
                                    const std::string &flagName,
                                    const FlagGetter &getter) = 0;
    virtual void registerFlagSetter(const std::string &pluginName,
                                    const std::string &flagName,
                                    const FlagSetter &setter) = 0;

    virtual bool isWriteableFlag(const std::string &pluginName,
                                 const std::string &flagName) const = 0;
    virtual void setFlag(const std::string &pluginName,
                         const std::string &flagName, bool value) const = 0;

    virtual bool isReadableFlag(const std::string &pluginName,
                                const std::string &flagName) const = 0;
    virtual bool getFlag(const std::string &pluginName,
                         const std::string &flagName) const = 0;

    // Properties
    virtual std::vector<std::string>
    getProperties(const std::string &pluginName) const = 0;

    virtual void registerPropertyGetter(const std::string &pluginName,
                                        const std::string &propertyName,
                                        const PropertyGetter &getter) = 0;
    virtual void registerPropertySetter(const std::string &pluginName,
                                        const std::string &propertyName,
                                        const PropertySetter &setter) = 0;

    virtual bool isWriteableProperty(const std::string &pluginName,
                                     const std::string &propertyName) const = 0;
    virtual void setProperty(const std::string &pluginName,
                             const std::string &propertyName,
                             const std::string &value) const = 0;

    virtual bool isReadableProperty(const std::string &pluginName,
                                    const std::string &propertyName) const = 0;
    virtual std::string getProperty(const std::string &pluginName,
                                    const std::string &propertyName) const = 0;

    virtual ~PluginRegistry() {};
};

} // namespace SWA

#endif
