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

macro(_simple_handle_common_args)
    if (DEFINED ARGS_SRCDIR)
        set(SRCDIR "${ARGS_SRCDIR}/")
    else ()
        if (${ARGC} GREATER 0)
            set(SRCDIR "${CMAKE_CURRENT_LIST_DIR}/${ARGV0}/")
        else ()
            set(SRCDIR "${CMAKE_CURRENT_LIST_DIR}/src/")
        endif ()
    endif ()

    foreach (src ${ARGS_SOURCES})
        list(APPEND sourceFiles "${SRCDIR}${src}")
    endforeach ()

    foreach (src ${ARGS_DEPENDS})
        list(APPEND dependFiles "${SRCDIR}${src}")
    endforeach ()

    if (DEFINED ARGS_INCDIR)
        set(INCDIR "${ARGS_INCDIR}")
    else ()
        set(INCDIR "include")
    endif ()
endmacro()

macro(_simple_handle_common_install)
    if (DEFINED ARGS_EXPORT OR ARGS_INSTALL)

        if (DEFINED ARGS_EXPORT)
            install(
                TARGETS ${ARGS_NAME}
                EXPORT ${ARGS_EXPORT}Targets
                RUNTIME DESTINATION ${INSTALL_BIN_DIR}
                LIBRARY DESTINATION ${INSTALL_LIB_DIR}
                ARCHIVE DESTINATION ${INSTALL_LIB_DIR}
                INCLUDES DESTINATION ${INSTALL_INCLUDE_DIR}
            )
        else ()
            install(
                TARGETS ${ARGS_NAME}
                RUNTIME DESTINATION ${INSTALL_BIN_DIR}
                LIBRARY DESTINATION ${INSTALL_LIB_DIR}
                ARCHIVE DESTINATION ${INSTALL_LIB_DIR}
                INCLUDES DESTINATION ${INSTALL_INCLUDE_DIR}
            )
        endif ()

        if (DEFINED ARGS_INCLUDES AND NOT ARGS_FIXPROTOINC)
            if ("${ARGS_INCLUDES}" STREQUAL "ALL")
                install(
                    DIRECTORY ${INCDIR}/
                    DESTINATION ${INSTALL_INCLUDE_DIR}
                )
            else ()
                foreach (file ${ARGS_INCLUDES})
                    get_filename_component(dir ${file} DIRECTORY)
                    install(
                        FILES ${INCDIR}/${file}
                        DESTINATION ${INSTALL_INCLUDE_DIR}/${dir}
                    )
                endforeach ()
            endif ()
        elseif(DEFINED ARGS_INCLUDES AND ARGS_FIXPROTOINC)
            foreach (file ${ARGS_INCLUDES})
                get_filename_component(filename ${file} NAME)
                get_filename_component(dir ${file} DIRECTORY)
                install(
                    FILES "${GENERATED_TARGET_DIR}/${filename}"
                    DESTINATION ${INSTALL_INCLUDE_DIR}/${dir}
                )
            endforeach ()
        endif ()
    endif ()
endmacro()

macro(simple_print_cmake_vars)
    get_cmake_property(_variableNames VARIABLES)
    list (SORT _variableNames)
    foreach (_variableName ${_variableNames})
        message(STATUS "${_variableName}=${${_variableName}}")
    endforeach()
endmacro()

