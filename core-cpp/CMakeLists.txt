#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
cmake_minimum_required(VERSION 3.5)

project(MaslCore VERSION 0.1)

set (CMAKE_CXX_STANDARD 11)

list(APPEND CMAKE_PREFIX_PATH /opt/poco)
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake-common)
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)

if ( NOT ${CMAKE_CURRENT_SOURCE_DIR} STREQUAL ${CMAKE_SOURCE_DIR} )
  set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} PARENT_SCOPE )
endif()

set(INSTALL_LIB_DIR     lib                         CACHE PATH "Installation directory for libraries")
set(INSTALL_BIN_DIR     bin                         CACHE PATH "Installation directory for executables")
set(INSTALL_INCLUDE_DIR include                     CACHE PATH "Installation directory for header files")

add_compile_options("-Werror" "-Wall")

find_package(Boost REQUIRED QUIET
 # workaround for cmake Issue #0016057 need to specify at least one component for Boost::boost target to be set up - should be fixed in cmake 3.6
 COMPONENTS system
)

# Make sure all libraries link against the specified version of the boost headers, rather than the OS installed version
link_libraries(Boost::boost)

include(SimpleAdd)

add_subdirectory(logging)
add_subdirectory(swa)
add_subdirectory(asn1)
add_subdirectory(transient)
add_subdirectory(sql)
add_subdirectory(sqlite)
add_subdirectory(codecoverage)
add_subdirectory(metadata)
add_subdirectory(sockets)
add_subdirectory(inspectorServer)
add_subdirectory(trace)
add_subdirectory(threadtimer)
add_subdirectory(backlogMonitor)
add_subdirectory(eventCollector)


include(CMakePackageConfigHelpers)

write_basic_package_version_file (
  ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}ConfigVersion.cmake
  VERSION       ${PROJECT_VERSION}
  COMPATIBILITY ExactVersion
)

configure_package_config_file( 
  ${CMAKE_CURRENT_SOURCE_DIR}/cmake/${PROJECT_NAME}Config.cmake.in
  ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}Config.cmake
  INSTALL_DESTINATION ${INSTALL_LIB_DIR}/cmake/${PROJECT_NAME}
  )

export( EXPORT ${PROJECT_NAME}Targets 
        NAMESPACE ${PROJECT_NAME}:: )

install(
  EXPORT      ${PROJECT_NAME}Targets
  FILE        ${PROJECT_NAME}Targets.cmake
  NAMESPACE   ${PROJECT_NAME}::
  DESTINATION ${INSTALL_LIB_DIR}/cmake/${PROJECT_NAME}
  )

install(
  FILES
              cmake/FindPkgConfigTarget.cmake
              cmake/FindSqlite3.cmake
              cmake/Masl.cmake
              cmake/MaslCodeGen.cmake
              cmake/MaslCoreConfig.cmake.in
              cmake/MavenRepo.cmake
              cmake/SimpleAdd.cmake
              ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}Config.cmake
              ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}ConfigVersion.cmake
  DESTINATION ${INSTALL_LIB_DIR}/cmake/${PROJECT_NAME}
  )
