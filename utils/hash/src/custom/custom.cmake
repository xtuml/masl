find_package(OpenSSL REQUIRED)
find_package(xxHash REQUIRED)
find_package(Boost REQUIRED headers)
include(GoogleTest)
find_package(GTest REQUIRED)

target_sources( Hash PRIVATE ${CMAKE_CURRENT_LIST_DIR}/src/Hash_services.cc )
target_link_libraries(Hash PUBLIC openssl::openssl xxHash::xxhash )


add_executable(testHash
        ${CMAKE_CURRENT_LIST_DIR}/src/test_hash.cc
)

target_link_libraries(testHash PRIVATE Hash Boost::headers GTest::gtest)

gtest_add_tests(TARGET testHash)
