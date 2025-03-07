cmake_minimum_required(VERSION 3.30)
project(xtuml_amqp_client CXX)

enable_testing()

find_package(xtuml_logging REQUIRED)

find_package(Boost REQUIRED)
find_package(asio REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(fmt REQUIRED)
find_package(GTest REQUIRED)
find_package(cyrus-sasl REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(CLI11 REQUIRED)

add_library(
        xtuml_amqp_client
        SHARED
        src/sasl.cc
        src/spawn.cc
        src/connection.cc
        src/session.cc
        src/sender.cc
        src/receiver.cc
        src/link.cc
        src/tracker.cc
        src/delivery.cc
)

target_sources(xtuml_amqp_client
        PUBLIC FILE_SET HEADERS
        BASE_DIRS include
        FILES
        include/amqp_asio)

target_link_libraries(
        xtuml_amqp_client
        PUBLIC xtuml_logging::xtuml_logging
        Boost::headers
        asio::asio
        openssl::openssl
        fmt::fmt
        cyrus-sasl::cyrus-sasl
        nlohmann_json::nlohmann_json)

target_compile_options(xtuml_amqp_client PRIVATE -Wall -Werror)

# False positive in non-debug builds regarding messages::Detach::handle and
# messages::Detach::closed when optimizing
target_compile_options(xtuml_amqp_client PUBLIC $<$<CXX_COMPILER_ID:GNU>:-Wno-maybe-uninitialized>)

# False positive for coroutines  when optimizing
# https://github.com/boostorg/cobalt/issues/7
target_compile_options(xtuml_amqp_client PUBLIC $<$<CXX_COMPILER_ID:GNU>:-Wno-mismatched-new-delete>)

install(TARGETS xtuml_amqp_client FILE_SET HEADERS)


# Example
add_executable(amqp_example src/example/example.cc)
target_compile_options(amqp_example PRIVATE -Wall -Werror)
target_link_libraries(amqp_example PRIVATE xtuml_amqp_client CLI11::CLI11)
install(TARGETS amqp_example)


# To Tuple code gen.
add_executable(gen_to_tuple src/gen_to_tuple.cc)
target_link_libraries(gen_to_tuple PRIVATE fmt::fmt)
target_compile_options(gen_to_tuple PRIVATE -Wall -Werror)

# Tests
add_executable(
        tests
        src/test/test_value_decode.cc
        src/test/test_value_encode.cc
        src/test/test_delivery.cc
        src/test/test_receiver.cc
        src/test/test_sender.cc
        src/test/test_tracker.cc
        src/test/main.cc)

target_link_libraries(tests PRIVATE xtuml_amqp_client GTest::gtest GTest::gmock)
target_compile_options(tests PRIVATE -Wall -Werror)

add_test(NAME tests COMMAND tests)
set_tests_properties(tests PROPERTIES TIMEOUT 10)

# Doc examples
add_subdirectory(docs/examples)


