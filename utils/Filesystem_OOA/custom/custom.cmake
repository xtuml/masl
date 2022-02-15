#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Filesystem_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Filesystem_services.cc )

find_package(OpenSSL)

target_include_directories(Filesystem_interface SYSTEM PRIVATE ${OPENSSL_INCLUDE_DIR} )
target_link_libraries(Filesystem_interface ${OPENSSL_SSL_LIBRARY} )
