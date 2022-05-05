#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
project(UtilityDomains VERSION 0.1)


include(Masl)

set(MaslExportTarget ${PROJECT_NAME})

add_masl_domain(BinaryIO_OOA/BinaryIO.mod)
add_masl_domain(CommandLine_OOA/CommandLine.mod)
add_masl_domain(Environment_OOA/Environment.mod)
add_masl_domain(Filesystem_OOA/Filesystem.mod)
add_masl_domain(Format_OOA/Format.mod)
add_masl_domain(Host_OOA/Host.mod)
add_masl_domain(Logger_OOA/Logger.mod)
add_masl_domain(Math_OOA/Math.mod)
add_masl_domain(Regex_OOA/Regex.mod)
add_masl_domain(Strings_OOA/Strings.mod)
add_masl_domain(Test_OOA/Test.mod)
add_masl_domain(UUID_OOA/UUID.mod)

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
