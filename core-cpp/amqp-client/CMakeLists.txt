# -----------------------------------------------------------------------------
# Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# -----------------------------------------------------------------------------
# SPDX-License-Identifier: Apache-2.0
# -----------------------------------------------------------------------------

cmake_minimum_required(VERSION 3.15)
project(async_amqp CXX)

enable_testing()

find_package(SimpleAdd REQUIRED)

find_package(Boost REQUIRED)
find_package(asio REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(fmt REQUIRED)
find_package(GTest REQUIRED)
find_package(cyrus-sasl REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(CLI11 CONFIG REQUIRED)


add_compile_options(-fdiagnostics-color=always)

# False positive in non-debug builds regarding messages::Detach::handle and messages::Detach::closed when optimizing
add_compile_options(-Wno-maybe-uninitialized)

# False positive for coroutines  when optimizing https://github.com/boostorg/cobalt/issues/7
add_compile_options(-Wno-mismatched-new-delete)


simple_add_shared_library(
        NAME amqp_asio
        SOURCES 
                sasl.cc
                spawn.cc
                connection.cc
                session.cc
                sender.cc
                receiver.cc
                link.cc
                tracker.cc
                delivery.cc
        LINKS
                Boost::headers
                asio::asio
                openssl::openssl
                fmt::fmt
                cyrus-sasl::cyrus-sasl
                nlohmann_json::nlohmann_json
                logging
        EXPORT amqp_asio
        INCLUDES ALL
        )

simple_add_executable(
        NAME example
        SOURCES
                example/example.cc
        LINKS
                amqp_asio::amqp_asio
                CLI11::CLI11        
)

simple_add_executable(
        NAME gen_to_tuple
        SOURCES
                gen_to_tuple.cc
        LINKS
                fmt::fmt
)

simple_add_executable(
        NAME tests
        SOURCES
                test/test_value_decode.cc 
                test/test_value_encode.cc
                test/test_delivery.cc
                test/test_receiver.cc
                test/test_sender.cc
                test/test_tracker.cc
                test/main.cc
        LINKS
                amqp_asio
                GTest::gtest
                GTest::gmock
)


add_test(NAME tests COMMAND tests)
set_tests_properties(tests PROPERTIES TIMEOUT 10)

add_subdirectory(docs/examples)
