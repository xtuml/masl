# -----------------------------------------------------------------------------
# Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# -----------------------------------------------------------------------------
# SPDX-License-Identifier: Apache-2.0
# -----------------------------------------------------------------------------

cmake_minimum_required(VERSION 3.15)
project(PackageTest CXX)

find_package(amqp_asio CONFIG REQUIRED)

add_executable(pkgtst src/pkgtst.cc)
target_link_libraries(pkgtst amqp_asio::amqp_asio)
