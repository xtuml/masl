#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
set(INSTALL_LIB_DIR     lib                         CACHE PATH "Installation directory for libraries")

execute_process ( COMMAND gradle -PbuildDir=${CMAKE_CURRENT_BINARY_DIR} installDist
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} )

add_custom_target ( masl-core )

set_property ( TARGET masl-core PROPERTY MAIN_JAR ${CMAKE_CURRENT_BINARY_DIR}/install/masl-core/lib/masl-core.jar ) 

install (
          DIRECTORY   ${CMAKE_CURRENT_BINARY_DIR}/install/masl-core/lib/
          DESTINATION ${INSTALL_LIB_DIR}
  )

install (
          DIRECTORY   ${CMAKE_CURRENT_BINARY_DIR}/install/masl-core/bin/
          DESTINATION ${INSTALL_BIN_DIR}
          PATTERN "masl-core*" PERMISSIONS WORLD_EXECUTE
  )
