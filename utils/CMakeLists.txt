#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
project(UtilityDomains VERSION 0.1)


include(Masl)

set(MaslExportTarget ${PROJECT_NAME})

add_masl_domain(Assertions_OOA/Assertions.mod INSTALL Assertions_OOA/Assertions.int)
add_masl_domain(BinaryIO_OOA/BinaryIO.mod INSTALL BinaryIO_OOA/BinaryIO.int)
add_masl_domain(CommandLine_OOA/CommandLine.mod INSTALL CommandLine_OOA/CommandLine.int)
add_masl_domain(Environment_OOA/Environment.mod INSTALL Environment_OOA/Environment.int)
add_masl_domain(Filesystem_OOA/Filesystem.mod INSTALL Filesystem_OOA/Filesystem.int)
add_masl_domain(Format_OOA/Format.mod INSTALL Format_OOA/Format.int)
add_masl_domain(Host_OOA/Host.mod INSTALL Host_OOA/Host.int)
add_masl_domain(JSON_OOA/JSON.mod INSTALL JSON_OOA/JSON.int)
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
