/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include "amqp_asio/connection.hh"
#include <asio/io_context.hpp>

using namespace amqp_asio;


int main() {

    asio::io_context io_context;

    auto c = Connection::create<protocol::amqp>("test-container", io_context.get_executor());

}
