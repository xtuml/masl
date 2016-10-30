#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
set(INSTALL_LIB_DIR     lib                         CACHE PATH "Installation directory for libraries")

add_custom_target ( masl-inspector ALL
                  COMMAND gradle -PbuildDir=${CMAKE_CURRENT_BINARY_DIR} installDist
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} )

install (
          DIRECTORY   ${CMAKE_CURRENT_BINARY_DIR}/install/masl-inspector/lib/
          DESTINATION ${INSTALL_LIB_DIR}
  )

install ( 
  PROGRAMS 
      bin/masl-inspector
      bin/masl-inspector-command-line
      bin/masl-domain-import
      bin/masl-domain-export
      bin/masl-schema-export
  DESTINATION ${INSTALL_BIN_DIR} )
