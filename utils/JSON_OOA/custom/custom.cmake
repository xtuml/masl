target_sources( JSON
            PRIVATE
            ${CMAKE_CURRENT_LIST_DIR}/src/JSON_services.cc
            ${CMAKE_CURRENT_LIST_DIR}/src/helpers.cc
            ${CMAKE_CURRENT_LIST_DIR}/src/schema.cc
            ${CMAKE_CURRENT_LIST_DIR}/src/DocumentStore.cc
)

find_package(nlohmann_json REQUIRED)
find_package(nlohmann_json_schema_validator REQUIRED)
find_package (libuuid REQUIRED)

target_link_libraries(
        JSON
            PRIVATE
                nlohmann_json::nlohmann_json
                nlohmann_json_schema_validator
            PUBLIC
                libuuid::libuuid

)

include(GoogleTest)
find_package(GTest CONFIG REQUIRED)

simple_add_executable(
        NAME testJSON
        SOURCES
        test_parse.cc
        test_dump.cc
        test_patch.cc
        test_pointer.cc
        test_schema.cc
        test_document_store.cc
        LINKS
        JSON
        GTest::gtest_main
        GTest::gmock
        nlohmann_json_schema_validator
)

gtest_add_tests(TARGET testJSON)

