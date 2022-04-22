#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
set(MASL_DIR ${CMAKE_BINARY_DIR}/cmake-masl)

file ( WRITE ${MASL_DIR}/CMakeLists.txt.in "
cmake_minimum_required(VERSION @CMAKE_MINIMUM_REQUIRED_VERSION@)

list(APPEND CMAKE_MODULE_PATH @CMAKE_MODULE_PATH@)

set(MASL_VERSION @MASL_VERSION@)

include(MaslCodeGen)

include(add_masl_codegen.cmake)
")

configure_file(${MASL_DIR}/CMakeLists.txt.in ${MASL_DIR}/CMakeLists.txt @ONLY)

file(WRITE ${MASL_DIR}/add_masl_codegen.cmake "")
file(WRITE ${MASL_DIR}/add_masl_subdirs.cmake "")

set(MASL_CONCURRENCY 10 CACHE STRING "Number of Masl processes to run concurrently")
set(INSTALL_MASL_DIR ${INSTALL_LIB_DIR}/masl)

set(MaslExportTarget Masl)

function(add_masl_package)
  file(APPEND ${MASL_DIR}/add_masl_codegen.cmake "find_java_package(${ARGN})\n")
endfunction()


function(add_masl_project sourceFile)
    cmake_parse_arguments(ARGS
    "" 
    "MASL_PACKAGE;PROJECT" 
    "JVM_ARGS;EXTRA_DEPS;EXTRA_ARGS;EXTRA_DOMAIN_PATHS;INSTALL" 
    ${ARGN})

  if ( DEFINED ARGS_PROJECT )
    set(PROJECT "${ARGS_PROJECT}" )
  else()
    get_filename_component( PROJECT     ${sourceFile} NAME_WE )
  endif()

  get_filename_component( SOURCE_DIR ${sourceFile} DIRECTORY )
  get_filename_component( ABSOLUTE_SOURCE_FILE ${sourceFile} ABSOLUTE )

  if ( DEFINED ARGS_MASL_PACKAGE )
    set(MASL_PACKAGE "${ARGS_MASL_PACKAGE}")
  else()
    set(MASL_PACKAGE "masl-core")
  endif()

  get_target_property ( MASL_JAR ${MASL_PACKAGE} MAIN_JAR )

  list(APPEND ARGS_EXTRA_DOMAIN_PATHS ${MASL_DOMAIN_PATH} )
  list(LENGTH ARGS_EXTRA_DOMAIN_PATHS domainPathLen )
  if ( domainPathLen )
    string(REPLACE ";" ":" domainPaths  "${ARGS_EXTRA_DOMAIN_PATHS}")
    list(APPEND ARGS_EXTRA_ARGS "-domainpath" "${domainPaths}")
  endif()

  string(REPLACE ";" " " JVM_ARGS   "${ARGS_JVM_ARGS}")
  string(REPLACE ";" " " EXTRA_ARGS "${ARGS_EXTRA_ARGS}")
  string(REPLACE ";" " " EXTRA_DEPS "${ARGS_EXTRA_DEPS}")
  

  file(APPEND ${MASL_DIR}/add_masl_codegen.cmake "\
masl_codegen(
  ${ABSOLUTE_SOURCE_FILE}
    MASL_JAR      ${MASL_JAR}
    DOMAIN_PATHS  ${MASL_DOMAIN_PATHS}
    JVM_ARGS      ${JVM_ARGS}
    EXTRA_ARGS    ${EXTRA_ARGS}
    EXTRA_DEPS    ${EXTRA_DEPS}
    NAME          ${PROJECT}
    SRC_TYPE      prj
 )
")

  set(PROJECT_DIR ${MASL_DIR}/${PROJECT})
  file(MAKE_DIRECTORY ${PROJECT_DIR})
  
  file(APPEND ${MASL_DIR}/add_masl_subdirs.cmake "\
add_subdirectory(${PROJECT_DIR} ${MASL_DIR}/build/${PROJECT})
"
  )

  if ( NOT EXISTS ${PROJECT_DIR}/dependencies.txt )
    file(WRITE ${PROJECT_DIR}/dependencies.txt "")
  endif()

  file(STRINGS ${PROJECT_DIR}/dependencies.txt deps )

  set_property ( DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${PROJECT_DIR}/dependencies.txt)
  set_property ( DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${deps})

  foreach ( file ${ARGS_INSTALL} )
    # need to follow symlinks as .int files are often symlinked to .mod, and 
    # the install command copies the symlink directly, leaving the installed one dangling...
    get_filename_component(resolvedFile "${file}" REALPATH)
    get_filename_component(name "${file}" NAME)
    install ( 
        FILES       ${resolvedFile}
        DESTINATION ${INSTALL_MASL_DIR}
        RENAME      ${name}
      )
  endforeach()

endfunction()

function(add_masl_domain sourceFile)

    cmake_parse_arguments(ARGS
    "" 
    "MASL_PACKAGE;DOMAIN" 
    "JVM_ARGS;EXTRA_DEPS;EXTRA_ARGS;EXTRA_DOMAIN_PATHS;INSTALL" 
    ${ARGN})

  if ( DEFINED ARGS_DOMAIN )
    set(DOMAIN "${ARGS_DOMAIN}" )
  else()
    get_filename_component( DOMAIN     ${sourceFile} NAME_WE )
  endif()

  get_filename_component( SOURCE_DIR ${sourceFile} DIRECTORY )
  get_filename_component( ABSOLUTE_SOURCE_FILE ${sourceFile} ABSOLUTE )

  if ( DEFINED ARGS_MASL_PACKAGE )
    set(MASL_PACKAGE "${ARGS_MASL_PACKAGE}")
  else()
    set(MASL_PACKAGE "masl-core")
  endif()

  get_target_property ( MASL_JAR ${MASL_PACKAGE} MAIN_JAR )

  list(APPEND ARGS_EXTRA_DOMAIN_PATHS ${MASL_DOMAIN_PATH} )
  list(LENGTH ARGS_EXTRA_DOMAIN_PATHS domainPathLen )
  if ( domainPathLen )
    string(REPLACE ";" ":" domainPaths  "${ARGS_EXTRA_DOMAIN_PATHS}")
    list(APPEND ARGS_EXTRA_ARGS "-domainpath" "${domainPaths}")
  endif()

  string(REPLACE ";" " " JVM_ARGS   "${ARGS_JVM_ARGS}")
  string(REPLACE ";" " " EXTRA_ARGS "${ARGS_EXTRA_ARGS}")
  string(REPLACE ";" " " EXTRA_DEPS "${ARGS_EXTRA_DEPS}")
  

  file(APPEND ${MASL_DIR}/add_masl_codegen.cmake "\
masl_codegen(
  ${ABSOLUTE_SOURCE_FILE} ${ARGS_DOMAIN} ${ARGS_PROJECT}
    MASL_JAR      ${MASL_JAR}
    DOMAIN_PATHS  ${MASL_DOMAIN_PATHS}
    JVM_ARGS      ${JVM_ARGS}
    EXTRA_ARGS    ${EXTRA_ARGS}
    EXTRA_DEPS    ${EXTRA_DEPS}
    NAME          ${DOMAIN}_OOA
    SRC_TYPE      mod
 )
")

  set(DOMAIN_DIR ${MASL_DIR}/${DOMAIN}_OOA)
  file(MAKE_DIRECTORY ${DOMAIN_DIR})
  
  file(APPEND ${MASL_DIR}/add_masl_subdirs.cmake "\
add_subdirectory(${DOMAIN_DIR} ${MASL_DIR}/build/${DOMAIN}_OOA)
"
  )

  if ( NOT EXISTS ${DOMAIN_DIR}/dependencies.txt )
    file(WRITE ${DOMAIN_DIR}/dependencies.txt "")
  endif()

  file(STRINGS ${DOMAIN_DIR}/dependencies.txt deps )

  set_property ( DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${DOMAIN_DIR}/dependencies.txt)
  set_property ( DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${deps})

  foreach ( file ${ARGS_INSTALL} )
    # need to follow symlinks as .int files are often symlinked to .mod, and 
    # the install command copies the symlink directly, leaving the installed one dangling...
    get_filename_component(resolvedFile "${file}" REALPATH)
    get_filename_component(name "${file}" NAME)
    install ( 
        FILES       ${resolvedFile}
        DESTINATION ${INSTALL_MASL_DIR}
        RENAME      ${name}
      )
  endforeach()


endfunction()


function(generate_masl)

  if ( NOT EXISTS ${MASL_DIR}/CMakeCache.txt )
    execute_process (
      COMMAND   ${CMAKE_COMMAND} 
                -G ${CMAKE_GENERATOR} 
                -H${MASL_DIR} 
                -B${MASL_DIR}
     RESULT_VARIABLE RESULT )
    if ( NOT RESULT EQUAL 0 )
      message ( FATAL_ERROR "masl cmake failed" )
    endif()
  endif()

  execute_process (
      COMMAND             ${CMAKE_COMMAND} --build ${MASL_DIR}
      RESULT_VARIABLE     RESULT
    )
    if ( NOT RESULT EQUAL 0 )
      message ( FATAL_ERROR "masl code generation failed" )
    endif()

  include(${MASL_DIR}/add_masl_subdirs.cmake)

endfunction()
