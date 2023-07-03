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

function(simple_add_object_library)
    cmake_parse_arguments(
        PARSE_ARGV 0
        ARGS
            ""
            "NAME;SRCDIR;INCDIR"
            "LINKS;SOURCES"
    )

    _simple_handle_common_args()

    add_library(${ARGS_NAME} OBJECT ${sourceFiles})

    target_include_directories(${ARGS_NAME} PUBLIC $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/${INCDIR}>)
   
    target_compile_options(${ARGS_NAME}
        PRIVATE
            $<$<COMPILE_LANGUAGE:CXX>:-Wall>
        PUBLIC
            $<$<COMPILE_LANGUAGE:CXX>:$<$<VERSION_GREATER_EQUAL:$<CXX_COMPILER_VERSION>,8.0.0>:-fmacro-prefix-map=${CMAKE_SOURCE_DIR}=[${CMAKE_PROJECT_NAME}]>>
    )

    # Conditionally append compile option
    if((NOT DEFINED ARGS_NOWARNERROR) OR (NOT "${ARGS_NOWARNERROR}" STREQUAL "TRUE"))
        target_compile_options(${ARGS_NAME} PRIVATE
            $<$<COMPILE_LANGUAGE:CXX>:-Werror>
        )
    endif()

    if (DEFINED ARGS_LINKS)
        target_link_libraries(${ARGS_NAME} PUBLIC ${ARGS_LINKS})
    endif ()

    set_property(
        TARGET ${ARGS_NAME}
        PROPERTY NO_SYSTEM_FROM_IMPORTED TRUE
    )
endfunction()

