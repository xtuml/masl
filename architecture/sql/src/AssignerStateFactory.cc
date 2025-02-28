/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/AssignerStateFactory.hh"

namespace SQL {

    // **********************************************************************
    // **********************************************************************
    AssignerStateFactory &AssignerStateFactory::singleton() {
        static AssignerStateFactory instance;
        return instance;
    }

    // **********************************************************************
    // **********************************************************************
    AssignerStateFactory::AssignerStateFactory() {}

    // **********************************************************************
    // **********************************************************************
    AssignerStateFactory::~AssignerStateFactory() {}

    // **********************************************************************
    // **********************************************************************
    bool AssignerStateFactory::registerImpl(const std::shared_ptr<AssignerStateImpl> &impl) {
        impl_ = impl;
        return true;
    }

    // **********************************************************************
    // **********************************************************************
    std::shared_ptr<AssignerStateImpl> &AssignerStateFactory::getImpl() {
        return impl_;
    }

} // end namespace SQL
