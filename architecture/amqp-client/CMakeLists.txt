cmake_minimum_required(VERSION 3.15)
project(xtuml_amqp_client CXX)

enable_testing()

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_logging REQUIRED)

find_package(Boost REQUIRED)
find_package(asio REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(fmt REQUIRED)
find_package(GTest REQUIRED)
find_package(cyrus-sasl REQUIRED)
find_package(nlohmann_json REQUIRED)
find_package(CLI11 CONFIG REQUIRED)


xtuml_add_library(${CMAKE_PROJECT_NAME} SHARED
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

target_link_libraries(${CMAKE_PROJECT_NAME}
        PUBLIC
                xtuml_logging::xtuml_logging
                Boost::headers
                asio::asio
                openssl::openssl
                fmt::fmt
                cyrus-sasl::cyrus-sasl
                nlohmann_json::nlohmann_json
)
# False positive in non-debug builds regarding messages::Detach::handle and messages::Detach::closed when optimizing
target_compile_options(${CMAKE_PROJECT_NAME} PUBLIC -Wno-maybe-uninitialized)

# False positive for coroutines  when optimizing https://github.com/boostorg/cobalt/issues/7
target_compile_options(${CMAKE_PROJECT_NAME} PUBLIC -Wno-mismatched-new-delete)


xtuml_add_executable(amqp_example
        src/example/example.cc
)

target_link_libraries(amqp_example
        PRIVATE
                xtuml_amqp_client
                CLI11::CLI11
)

xtuml_add_local_executable(gen_to_tuple
        src/gen_to_tuple.cc
)
target_link_libraries(gen_to_tuple
        PRIVATE
                fmt::fmt
)

xtuml_add_local_executable(tests
        src/test/test_value_decode.cc 
        src/test/test_value_encode.cc
        src/test/test_delivery.cc
        src/test/test_receiver.cc
        src/test/test_sender.cc
        src/test/test_tracker.cc
        src/test/main.cc
)

target_link_libraries(tests
        PRIVATE
                xtuml_amqp_client
                GTest::gtest
                GTest::gmock
)


add_test(NAME tests COMMAND tests)
set_tests_properties(tests PROPERTIES TIMEOUT 10)

add_subdirectory(docs/examples)
