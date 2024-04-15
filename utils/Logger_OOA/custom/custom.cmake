#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Logger PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/Logger_services.cc )



include(GoogleTest)
find_package(GTest CONFIG REQUIRED)

simple_add_executable(
        NAME testLogger
        SOURCES
        test_logger.cc
        LINKS
        Logger
        GTest::gtest_main
        GTest::gmock
)

gtest_add_tests(TARGET testLogger)

