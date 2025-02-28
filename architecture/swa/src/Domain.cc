/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/Domain.hh"
#include "swa/ProgramError.hh"

#include "boost/tuple/tuple.hpp"

namespace SWA {

    void Domain::addScenario(int id, const Scenario &scenario) {
        scenarios.insert(ScenarioLookup::value_type(id, scenario));
    }

    void Domain::addExternal(int id, const External &external) {
        externals.insert(ExternalLookup::value_type(id, external));
    }

    const Domain::Scenario &Domain::getScenario(int id) const {
        ScenarioLookup::const_iterator it = scenarios.find(id);
        if (it == scenarios.end()) {
            throw SWA::ProgramError(::boost::make_tuple("Invalid Scenario Number ", id, " for Domain ", getName()));
        }
        return it->second;
    }

    const Domain::External &Domain::getExternal(int id) const {
        ExternalLookup::const_iterator it = externals.find(id);
        if (it == externals.end()) {
            throw SWA::ProgramError(::boost::make_tuple("Invalid External Number ", id, " for Domain ", getName()));
        }
        return it->second;
    }

} // namespace SWA
