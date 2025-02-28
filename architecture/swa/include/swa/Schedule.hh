/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Schedule_HH
#define SWA_Schedule_HH

#include <functional>
#include <string>
#include <vector>

namespace SWA {
    class Schedule {
      public:
        Schedule(const std::string &name, const std::string &text);

        bool isValid() const {
            return valid;
        }

        const std::string &getName() const {
            return name;
        }

        typedef std::function<void()> Action;

        typedef std::vector<Action> Actions;
        const Actions &getActions() const {
            return actions;
        }

      private:
        void reportError(int lineNo, const std::string &error);
        std::string name;
        std::string text;
        bool valid;

        Actions actions;
    };
} // namespace SWA

#endif
