# ----------------------------------------------------------------------------
# (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# ----------------------------------------------------------------------------
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------
# Classification: UK OFFICIAL
# ----------------------------------------------------------------------------

include(CMakePackageConfigHelpers)

set(_simple_package_config_default_template_file ${CMAKE_CURRENT_LIST_DIR}/Config.cmake.in)

function(simple_package_config)
    cmake_parse_arguments(
        PARSE_ARGV 0
        ARGS
            ""
            "NAME;VERSION;COMPATIBILITY;CONFIG_TEMPLATE"
            ""
    )

    if ( NOT DEFINED ARGS_NAME )
        set(ARGS_NAME ${PROJECT_NAME})
    endif()

    if ( NOT DEFINED ARGS_VERSION )
        set(ARGS_VERSION ${PROJECT_VERSION})
    endif()

    if ( NOT DEFINED ARGS_COMPATIBILITY )
        set(ARGS_COMPATIBILITY ExactVersion)
    endif()

    if ( NOT DEFINED ARGS_CONFIG_TEMPLATE )
        set(ARGS_CONFIG_TEMPLATE ${CMAKE_CURRENT_SOURCE_DIR}/cmake/${ARGS_NAME}Config.cmake.in)

        if ( NOT EXISTS ${ARGS_CONFIG_TEMPLATE})
            message("Config Template ${ARGS_CONFIG_TEMPLATE} not found - using default template")
            set(PACKAGE_INIT "@PACKAGE_INIT@")
            configure_file(${_simple_package_config_default_template_file} ${ARGS_NAME}Config.cmake @ONLY)
            set(ARGS_CONFIG_TEMPLATE ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}Config.cmake)
        endif()
    endif()

    configure_package_config_file(
        ${ARGS_CONFIG_TEMPLATE}
        ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}Config.cmake
        INSTALL_DESTINATION ${INSTALL_LIB_DIR}/cmake/${ARGS_NAME}
    )

    install(
        FILES ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}Config.cmake
        DESTINATION ${INSTALL_LIB_DIR}/cmake/${ARGS_NAME}
    )
 
    export(
        EXPORT ${ARGS_NAME}Targets
        NAMESPACE ${ARGS_NAME}::
    )

    install(
        EXPORT ${ARGS_NAME}Targets
        FILE ${ARGS_NAME}Targets.cmake
        NAMESPACE ${ARGS_NAME}::
        DESTINATION ${INSTALL_LIB_DIR}/cmake/${ARGS_NAME}
    )

   if (DEFINED ARGS_VERSION)
        write_basic_package_version_file (
            ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}ConfigVersion.cmake
            VERSION ${ARGS_VERSION}
            COMPATIBILITY ${ARGS_COMPATIBILITY}
        )
        install(
            FILES ${CMAKE_CURRENT_BINARY_DIR}/${ARGS_NAME}ConfigVersion.cmake
            DESTINATION ${INSTALL_LIB_DIR}/cmake/${ARGS_NAME}
        )
    endif()
endfunction()

