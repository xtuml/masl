find_package(xtuml_logging REQUIRED)
target_sources( Logger PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/Logger_services.cc )

target_link_libraries( Logger PRIVATE xtuml_logging::xtuml_logging)

find_package(GTest CONFIG REQUIRED)

add_executable( testLogger
        ${CMAKE_CURRENT_LIST_DIR}/src/test_logger.cc
        ${CMAKE_CURRENT_LIST_DIR}/src/test_main.cc
)

target_link_libraries(testLogger PRIVATE Logger
        GTest::gtest
        GTest::gmock
)

add_test(NAME tests COMMAND testLogger)

