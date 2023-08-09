#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package(libuuid REQUIRED)

target_sources( UUID PRIVATE ${CMAKE_CURRENT_LIST_DIR}/UUID_services.cc )

target_link_libraries ( UUID PUBLIC libuuid::libuuid )

file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/UUIDExtras.cmake "
find_package (libuuid CONFIG REQUIRED)
")

install (
        FILES
        ${CMAKE_CURRENT_BINARY_DIR}/UUIDExtras.cmake
        DESTINATION ${INSTALL_LIB_DIR}/cmake/UUID
)
