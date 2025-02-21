# -----------------------------------------------------------------------------
# Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# -----------------------------------------------------------------------------
# SPDX-License-Identifier: Apache-2.0
# -----------------------------------------------------------------------------

cmake_minimum_required(VERSION 3.15)
project(PackageTest CXX)

find_package(xtuml_amqp_client CONFIG REQUIRED)

add_executable(pkgtst src/pkgtst.cc)
target_link_libraries(pkgtst xtuml_amqp_client::xtuml_amqp_client)
