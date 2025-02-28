/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Domain_HH
#define SWA_Domain_HH

#include <functional>

#include <map>
#include <stdint.h>
#include <string>

#include "types.hh"

namespace SWA {

    class Domain {
      public:
        Domain()
            : id(0), name(""), interface(true) {}
        Domain(int id, const std::string &name, bool interface)
            : id(id), name(name), interface(interface) {}

        int getId() const {
            return id;
        }

        const std::string &getName() const {
            return name;
        }

        bool isInterface() const {
            return interface;
        }
        void setInterface(bool interface) {
            this->interface = interface;
        }

        typedef std::function<void()> Scenario;
        typedef std::function<void()> External;

        void addExternal(int id, const Scenario &external);
        void addScenario(int id, const External &scenario);

        const std::function<void()> &getScenario(int id) const;
        const std::function<void()> &getExternal(int id) const;

      private:
        typedef std::map<int, Scenario> ScenarioLookup;
        typedef std::map<int, External> ExternalLookup;

        int id;
        std::string name;
        bool interface;

        ScenarioLookup scenarios;
        ExternalLookup externals;
    };
} // namespace SWA

#endif
