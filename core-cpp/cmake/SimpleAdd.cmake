#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
include(CMakeParseArguments)

set(DEFAULT_LIB_SOVERSION 1   CACHE STRING "Default shared library so version")
set(DEFAULT_LIB_VERSION   1.0 CACHE STRING "Default shared library version")

set(INSTALL_LIB_DIR     lib                         CACHE PATH "Installation directory for libraries")
set(INSTALL_BIN_DIR     bin                         CACHE PATH "Installation directory for executables")
set(INSTALL_INCLUDE_DIR include                     CACHE PATH "Installation directory for header files")
set(INSTALL_DOC_DIR     doc                         CACHE PATH "Installation directory for documentation")


macro(_simple_handle_common_args)

  if ( DEFINED ARGS_SRCDIR )
    set(SRCDIR "${ARGS_SRCDIR}/")
  else()
    if ( ${ARGC} GREATER 0 )
      set(SRCDIR "${CMAKE_CURRENT_SOURCE_DIR}/${ARGV0}/")
    else()
      set(SRCDIR "${CMAKE_CURRENT_SOURCE_DIR}/src/")
    endif()
  endif()

  foreach ( src ${ARGS_SOURCES} )
    list(APPEND sourceFiles "${SRCDIR}${src}")
  endforeach()

  foreach ( src ${ARGS_DEPENDS} )
    list(APPEND dependFiles "${SRCDIR}${src}")
  endforeach()


  if ( DEFINED ARGS_INCDIR )
    set(INCDIR "${ARGS_INCDIR}")
  else()
    set(INCDIR "include")
  endif()

endmacro()

macro(_simple_handle_common_install)

  if ( DEFINED ARGS_EXPORT OR ARGS_INSTALL )

    if ( DEFINED ARGS_EXPORT )
      install (
        TARGETS   ${ARGS_NAME}
        EXPORT    ${ARGS_EXPORT}Targets
        RUNTIME   DESTINATION ${INSTALL_BIN_DIR}
        LIBRARY   DESTINATION ${INSTALL_LIB_DIR}
        ARCHIVE   DESTINATION ${INSTALL_LIB_DIR}
        INCLUDES  DESTINATION ${INSTALL_INCLUDE_DIR}
      )
    else()
      install (
        TARGETS   ${ARGS_NAME}
        RUNTIME   DESTINATION ${INSTALL_BIN_DIR}
        LIBRARY   DESTINATION ${INSTALL_LIB_DIR}
        ARCHIVE   DESTINATION ${INSTALL_LIB_DIR}
        INCLUDES  DESTINATION ${INSTALL_INCLUDE_DIR}
      )
    endif()

    if ( DEFINED ARGS_INCLUDES )
      if ( "${ARGS_INCLUDES}" STREQUAL "ALL" )
        install (
          DIRECTORY   ${INCDIR}/
          DESTINATION ${INSTALL_INCLUDE_DIR}
        )
      else()
        foreach ( file ${ARGS_INCLUDES} )
          get_filename_component(dir ${file} DIRECTORY )
          install (
            FILES ${INCDIR}/${file}
            DESTINATION ${INSTALL_INCLUDE_DIR}/${dir}
          )
        endforeach()
      endif()
    endif()

  endif()
  



endmacro()


function(simple_add_lyxpdf)
  cmake_parse_arguments(ARGS
    "INSTALL"
    "NAME;SRCDIR"
    "SOURCES;DEPENDS"
    ${ARGN})
  
  _simple_handle_common_args("doc")

  set(OUTPUT_FILE ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}.pdf)

  add_custom_command (
    OUTPUT    ${OUTPUT_FILE}
    COMMAND   lyx
    ARGS      -E pdf ${OUTPUT_FILE} ${sourceFiles}
    DEPENDS   
              ${sourceFiles}
              ${dependFiles}
  )

  add_custom_target (
    ${ARGS_NAME}-lyx ALL 
    DEPENDS ${OUTPUT_FILE}
  )

  if ( DEFINED ARGS_INSTALL )
    install (
      FILES ${OUTPUT_FILE}
      DESTINATION ${INSTALL_DOC_DIR}
    )
  endif()

endfunction()


function(simple_add_shared_library)
  cmake_parse_arguments(ARGS 
    "INSTALL" 
    "NAME;LIBNAME;EXPORT;VERSION;SOVERSION;SRCDIR;INCDIR" 
    "LINKS;SOURCES;INCLUDES" 
    ${ARGN})

  _simple_handle_common_args()

  add_library( ${ARGS_NAME} SHARED ${sourceFiles} )

  target_include_directories(${ARGS_NAME} PUBLIC $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)

  if ( DEFINED ARGS_LINKS )
    target_link_libraries(${ARGS_NAME} ${ARGS_LINKS})
  endif()

  if ( DEFINED ARGS_SOVERSION )
    set_property (
      TARGET              ${ARGS_NAME}
      PROPERTY SOVERSION  ${ARGS_SOVERSION}
    )
  else()
    set_property (
      TARGET              ${ARGS_NAME}
      PROPERTY SOVERSION  ${DEFAULT_LIB_SOVERSION})
  endif()

  if ( DEFINED ARGS_VERSION )
    set_property (
      TARGET           ${ARGS_NAME}
      PROPERTY VERSION ${ARGS_VERSION}
      )
  else()
    set_property (
      TARGET           ${ARGS_NAME}
      PROPERTY VERSION ${DEFAULT_LIB_VERSION}
      )
  endif()

  set_property ( 
    TARGET                            ${ARGS_NAME} 
    PROPERTY NO_SYSTEM_FROM_IMPORTED  TRUE
    )

  if ( DEFINED ARGS_LIBNAME )
    set_property (
      TARGET                ${ARGS_NAME} 
      PROPERTY  OUTPUT_NAME ${ARGS_LIBNAME} 
      )
  endif()

  _simple_handle_common_install()

  if ( DEFINED ARGS_EXPORT )
    add_library(${ARGS_EXPORT}::${ARGS_NAME} ALIAS ${ARGS_NAME})
  endif()

endfunction()


function(simple_add_archive_library)
  cmake_parse_arguments(ARGS 
    "INSTALL" 
    "NAME;LIBNAME;EXPORT;SRCDIR;INCDIR" 
    "LINKS;SOURCES;INCLUDES" 
    ${ARGN})

  _simple_handle_common_args()

  add_library( ${ARGS_NAME} STATIC ${sourceFiles} )

  target_include_directories(${ARGS_NAME} PUBLIC $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)

  if ( DEFINED ARGS_LINKS )
    target_link_libraries(${ARGS_NAME} ${ARGS_LINKS})
  endif()

  set_property ( 
    TARGET                            ${ARGS_NAME} 
    PROPERTY NO_SYSTEM_FROM_IMPORTED  TRUE
    )

  if ( DEFINED ARGS_LIBNAME )
    set_property (
      TARGET                ${ARGS_NAME} 
      PROPERTY  OUTPUT_NAME ${ARGS_LIBNAME} 
      )
  endif()

  _simple_handle_common_install()

  if ( DEFINED ARGS_EXPORT )
    add_library(${ARGS_EXPORT}::${ARGS_NAME} ALIAS ${ARGS_NAME})
  endif()

endfunction()

function(simple_add_interface_library)
  cmake_parse_arguments(ARGS 
    "INSTALL" 
    "NAME;EXPORT;INCDIR" 
    "LINKS;SOURCES;INCLUDES" 
    ${ARGN})

  _simple_handle_common_args()


  add_library( ${ARGS_NAME} INTERFACE )

  target_include_directories(${ARGS_NAME} INTERFACE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)

  if ( DEFINED ARGS_LINKS )
    target_link_libraries(${ARGS_NAME} INTERFACE ${ARGS_LINKS})
  endif()

  if ( DEFINED ARGS_LIBNAME )
    set_property (
      TARGET                ${ARGS_NAME} 
      PROPERTY  OUTPUT_NAME ${ARGS_LIBNAME} 
      )
  endif()

  if ( DEFINED ARGS_SOURCES )
    target_sources( ${ARGS_NAME} INTERFACE ${sourceFiles} )
  endif()
 
  _simple_handle_common_install()
  
  if ( DEFINED ARGS_EXPORT )
    add_library(${ARGS_EXPORT}::${ARGS_NAME} ALIAS ${ARGS_NAME})
  endif()

endfunction()



function(simple_add_executable)
  cmake_parse_arguments(ARGS 
    "INSTALL" 
    "NAME;EXENAME;SRCDIR;INCDIR" 
    "LINKS;SOURCES" 
    ${ARGN})

  _simple_handle_common_args()

  add_executable( ${ARGS_NAME} ${sourceFiles} )

  target_include_directories(${ARGS_NAME} PRIVATE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)

  if ( DEFINED ARGS_LINKS )
    target_link_libraries(${ARGS_NAME} ${ARGS_LINKS})
  endif()

  set_property ( 
    TARGET                            ${ARGS_NAME} 
    PROPERTY NO_SYSTEM_FROM_IMPORTED  TRUE
    )

  if ( DEFINED ARGS_EXENAME )
    set_property (
      TARGET                ${ARGS_NAME} 
      PROPERTY  OUTPUT_NAME ${ARGS_EXENAME} 
      )
  endif()
  
  _simple_handle_common_install()
  
  if ( DEFINED ARGS_EXPORT )
    add_executable(${ARGS_EXPORT}::${ARGS_NAME} ALIAS ${ARGS_NAME})
  endif()

endfunction()

