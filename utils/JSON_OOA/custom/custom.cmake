target_sources( JSON PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/JSON_services.cc)

include(GoogleTest)
find_package(GTest CONFIG REQUIRED)

simple_add_executable(
        NAME testJSON
        SOURCES
        test_parse.cc
        test_dump.cc
        test_patch.cc
        test_pointer.cc
        LINKS
        JSON
        GTest::gtest_main
)

gtest_add_tests(TARGET testJSON)

