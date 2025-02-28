/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql__WriteSqlOnChangeMonitor_HH
#define Sql__WriteSqlOnChangeMonitor_HH

#include <string>

namespace SQL {

    // *****************************************************************
    //! @brief
    // *****************************************************************
    class WriteOnChangeEnabler {
      public:
        explicit WriteOnChangeEnabler(const std::string &objectName);
        ~WriteOnChangeEnabler();
        bool isEnabled();

      private:
        // prevent copy and assignment
        WriteOnChangeEnabler(const WriteOnChangeEnabler &rhs);
        WriteOnChangeEnabler &operator=(const WriteOnChangeEnabler &rhs);

      private:
        const std::string name;
    };

} // end namespace SQL
#endif
