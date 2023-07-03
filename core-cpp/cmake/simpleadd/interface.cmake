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

function(simple_add_interface_library)
    cmake_parse_arguments(
        PARSE_ARGV 0
        ARGS
            "INSTALL"
            "NAME;EXPORT;INCDIR"
            "LINKS;SOURCES;INCLUDES"
    )

    _simple_handle_common_args()

    add_library(${ARGS_NAME} INTERFACE)

    target_include_directories(${ARGS_NAME} INTERFACE $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)

    if (DEFINED ARGS_LINKS)
        target_link_libraries(${ARGS_NAME} INTERFACE ${ARGS_LINKS})
    endif ()

    if (DEFINED ARGS_LIBNAME)
        set_property(
            TARGET ${ARGS_NAME}
            PROPERTY OUTPUT_NAME ${ARGS_LIBNAME}
        )
    endif ()

    if (DEFINED ARGS_SOURCES)
        target_sources(${ARGS_NAME} INTERFACE ${sourceFiles})
    endif ()

    _simple_handle_common_install()

    if (DEFINED ARGS_EXPORT)
        add_library(${ARGS_EXPORT}::${ARGS_NAME} ALIAS ${ARGS_NAME})
    endif ()
endfunction()

