/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#pragma once

#include <cxxabi.h>
#include <string>

namespace amqp_asio::detail {

    template <typename T>
    std::string type_name() {
        int status;
        auto realname = abi::__cxa_demangle(typeid(T).name(), nullptr, nullptr, &status);
        std::string result = realname;
        std::free(realname);
        return result;
    }

} // namespace amqp_asio::detail