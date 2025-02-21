/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include <algorithm>
#include <functional>
#include <map>
#include <vector>

#include "sql/AssignerStateFactory.hh"
#include "sql/AssignerStateMapper.hh"
#include "sql/Util.hh"

namespace SQL {

// *****************************************************************
// *****************************************************************
AssignerStateMapper &AssignerStateMapper::singleton() {
    static AssignerStateMapper instance;
    return instance;
}

// *****************************************************************
// *****************************************************************
AssignerStateMapper::AssignerStateMapper()
    : impl_(AssignerStateFactory::singleton().getImpl()) {
    for (const auto &[key, state] : impl_->initialise()) {
        cacheAssignerState(key, state);
    }
}

// *****************************************************************
// *****************************************************************
AssignerStateMapper::~AssignerStateMapper() {}

// *****************************************************************
// *****************************************************************
bool AssignerStateMapper::isAssignerSet(const std::string &objectKey) {
    return assignerStates_.find(objectKey) != assignerStates_.end();
}

// *****************************************************************
// *****************************************************************
void AssignerStateMapper::cacheAssignerState(const std::string &objectKey,
                                             const int32_t currentState) {
    assignerStates_[objectKey] = currentState;
}

// *****************************************************************
// *****************************************************************
void AssignerStateMapper::setAssignerState(const std::string &objectKey,
                                           const int32_t currentState) {
    if (isAssignerSet(objectKey)) {
        impl_->updateState(objectKey, currentState);
    } else {
        impl_->insertState(objectKey, currentState);
    }
    cacheAssignerState(objectKey, currentState);
}

} // namespace SQL
