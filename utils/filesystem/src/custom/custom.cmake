#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Filesystem PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Filesystem_services.cc )

find_package(OpenSSL)

target_link_libraries(Filesystem PUBLIC openssl::openssl )
