#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
include(MavenRepo)
find_package(Java)

add_custom_target ( generate-masl ALL )

function(masl_codegen sourceFile)

  cmake_parse_arguments(ARGS
    "" 
    "MASL_JAR;NAME;SRC_TYPE" 
    "EXTRA_ARGS;JVM_ARGS;EXTRA_DEPS" 
    ${ARGN})

  set(MASL_OUTPUT_PATH "${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}")

  file(MAKE_DIRECTORY ${MASL_OUTPUT_PATH})

  if ( NOT EXISTS ${MASL_OUTPUT_PATH}/dependencies.txt )
    FILE(WRITE ${MASL_OUTPUT_PATH}/dependencies.txt "")
  endif()

  set_property ( DIRECTORY APPEND PROPERTY CMAKE_CONFIGURE_DEPENDS ${MASL_OUTPUT_PATH}/dependencies.txt)

  file(STRINGS "${MASL_OUTPUT_PATH}/dependencies.txt" deps)

  add_custom_command ( 
    OUTPUT             
                       ${MASL_OUTPUT_PATH}/CMakeLists.txt
    COMMAND            ${Java_JAVA_EXECUTABLE} ${ARGS_JVM_ARGS} -jar ${ARGS_MASL_JAR} -${ARGS_SRC_TYPE} ${sourceFile} ${ARGS_EXTRA_ARGS}
    DEPENDS            ${deps};${ARGS_EXTRA_DEPS}
    WORKING_DIRECTORY  ${MASL_OUTPUT_PATH}
  )

  add_custom_target ( generate-${ARGS_NAME} DEPENDS ${MASL_OUTPUT_PATH}/CMakeLists.txt )

  add_dependencies ( generate-masl generate-${ARGS_NAME} )

endfunction()

