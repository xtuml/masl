find_package(nlohmann_json)

target_sources( JSON PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/JSON_services.cc)

target_link_libraries(JSON PRIVATE nlohmann_json::nlohmann_json)

include(GoogleTest)
find_package(GTest CONFIG REQUIRED)

add_executable( testJSON
${CMAKE_CURRENT_LIST_DIR}/src/test_parse.cc
${CMAKE_CURRENT_LIST_DIR}/src/test_dump.cc
${CMAKE_CURRENT_LIST_DIR}/src/test_patch.cc
${CMAKE_CURRENT_LIST_DIR}/src/test_pointer.cc
${CMAKE_CURRENT_LIST_DIR}/src/test_main.cc )

target_link_libraries(testJSON PRIVATE JSON GTest::gtest)

gtest_add_tests(TARGET testJSON)

