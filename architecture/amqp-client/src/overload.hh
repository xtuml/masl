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

namespace amqp_asio {

    template <typename... Ts>
    struct overload : Ts... {
        using Ts::operator()...;
    };
    template <typename... Ts>
    overload(Ts...) -> overload<Ts...>;

} // namespace amqp_asio