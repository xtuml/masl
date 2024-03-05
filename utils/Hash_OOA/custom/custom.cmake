find_package(OpenSSL REQUIRED)
find_package(xxHash REQUIRED)
find_package(Boost REQUIRED headers)
include(GoogleTest)
find_package(GTest CONFIG REQUIRED)

target_sources( Hash PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/Hash_services.cc )
target_link_libraries(Hash PRIVATE openssl::openssl xxHash::xxhash )


simple_add_executable(
        NAME testHash
        SOURCES
        test_hash.cc
        LINKS
        Hash
        Boost::headers
        GTest::gtest_main
)

gtest_add_tests(TARGET testHash)

