#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
cmake_minimum_required(VERSION 3.5)

project(UtilityDomains VERSION 0.1)

set (CMAKE_CXX_STANDARD 11)

set(CMAKE_PREFIX_PATH ${CMAKE_PREFIX_PATH} /opt/masl)
set(CMAKE_PREFIX_PATH ${CMAKE_PREFIX_PATH} /opt/poco)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} /opt/masl/lib/cmake/MaslCore)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} /opt/poco/lib/cmake/Poco)

set(INSTALL_LIB_DIR lib CACHE PATH "Installation directory for libraries")
set(INSTALL_BIN_DIR bin CACHE PATH "Installation directory for executables")
set(INSTALL_INCLUDE_DIR include CACHE PATH "Installation directory for header files")

include(MaslCoreConfig)
include(SimpleAdd)
include(Masl)

add_custom_target (masl-core)
set_property (TARGET masl-core PROPERTY MAIN_JAR /opt/masl/lib/masl-core.jar)

find_package(Poco REQUIRED COMPONENTS XML Foundation)
find_package(Sqlite3 REQUIRED)
find_package(Boost REQUIRED QUIET COMPONENTS system)
link_libraries(Boost::boost)

set(MaslExportTarget ${PROJECT_NAME})

add_masl_domain(Assertions_OOA/Assertions.mod INSTALL Assertions_OOA/Assertions.int)
add_masl_domain(BinaryIO_OOA/BinaryIO.mod INSTALL BinaryIO_OOA/BinaryIO.int)
add_masl_domain(CommandLine_OOA/CommandLine.mod INSTALL CommandLine_OOA/CommandLine.int)
add_masl_domain(Environment_OOA/Environment.mod INSTALL Environment_OOA/Environment.int)
add_masl_domain(Filesystem_OOA/Filesystem.mod INSTALL Filesystem_OOA/Filesystem.int)
add_masl_domain(Format_OOA/Format.mod INSTALL Format_OOA/Format.int)
add_masl_domain(Host_OOA/Host.mod INSTALL Host_OOA/Host.int)
add_masl_domain(JSON_OOA/JSON.mod INSTALL JSON_OOA/JSON.int)
add_masl_domain(JSONValidation_OOA/JSONValidation.mod INSTALL JSONValidation_OOA/JSONValidation.int JVM_ARGS -Dmetaschema="/opt/masl/share/json/json-metaschema.json")
add_masl_domain(Logger_OOA/Logger.mod INSTALL Logger_OOA/Logger.int)
add_masl_domain(Math_OOA/Math.mod INSTALL Math_OOA/Math.int)
add_masl_domain(Regex_OOA/Regex.mod INSTALL Regex_OOA/Regex.int)
add_masl_domain(Schedule_OOA/Schedule.mod INSTALL Schedule_OOA/Schedule.int)
add_masl_domain(Strings_OOA/Strings.mod INSTALL Strings_OOA/Strings.int)
add_masl_domain(Test_OOA/Test.mod INSTALL Test_OOA/Test.int)
add_masl_domain(UUID_OOA/UUID.mod INSTALL UUID_OOA/UUID.int)

generate_masl()

include(CMakePackageConfigHelpers)

write_basic_package_version_file (
  ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}ConfigVersion.cmake
  VERSION       ${PROJECT_VERSION}
  COMPATIBILITY ExactVersion
)

export( EXPORT ${PROJECT_NAME}Targets 
        NAMESPACE ${PROJECT_NAME}:: )

install(
  EXPORT      ${PROJECT_NAME}Targets
  FILE        ${PROJECT_NAME}Config.cmake
  NAMESPACE   ${PROJECT_NAME}::
  DESTINATION ${INSTALL_LIB_DIR}/cmake/${PROJECT_NAME}
  )

install(
  FILES
              ${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}ConfigVersion.cmake
  DESTINATION ${INSTALL_LIB_DIR}/cmake/${PROJECT_NAME}
  )
