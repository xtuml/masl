/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/CriteriaFactory.hh"

namespace SQL {

    // *****************************************************************
    // *****************************************************************
    CriteriaFactory &CriteriaFactory::singleton() {
        static CriteriaFactory instance;
        return instance;
    }

    // *****************************************************************
    // *****************************************************************
    CriteriaFactory::CriteriaFactory() {}

    // *****************************************************************
    // *****************************************************************
    CriteriaFactory::~CriteriaFactory() {}

    // *****************************************************************
    // *****************************************************************
    bool CriteriaFactory::registerImpl(const std::shared_ptr<CloneableCriteria> &impl) {
        impl_ = impl;
        return true;
    }

    // *****************************************************************
    // *****************************************************************
    std::shared_ptr<CriteriaImpl> CriteriaFactory::newInstance() {
        return impl_->clone();
    }

} // namespace SQL
