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

if (NOT SimpleAdd_FOUND)
    set(SimpleAdd_FOUND TRUE)
    set(SIMPLEADD_FOUND TRUE)

    set(DEFAULT_LIB_SOVERSION 1 CACHE STRING "Default shared library so version")
    set(DEFAULT_LIB_VERSION 1.0 CACHE STRING "Default shared library version")

    set(INSTALL_LIB_DIR lib CACHE PATH "Installation directory for libraries")
    set(INSTALL_BIN_DIR bin CACHE PATH "Installation directory for executables")
    set(INSTALL_INCLUDE_DIR include CACHE PATH "Installation directory for header files")

    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/common.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/archive.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/object.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/shared.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/interface.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/executable.cmake)
    include(${CMAKE_CURRENT_LIST_DIR}/simpleadd/package_config.cmake)
endif ()
