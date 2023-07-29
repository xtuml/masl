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

cmake_minimum_required(VERSION 3.5)
project(MaslCore)
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)
find_package(SimpleAdd REQUIRED)

add_subdirectory(logging)
add_subdirectory(swa)
add_subdirectory(asn1)
add_subdirectory(transient)
add_subdirectory(sql)
add_subdirectory(sqlite)
add_subdirectory(codecoverage)
add_subdirectory(metadata)
add_subdirectory(sockets)
add_subdirectory(inspectorServer)
add_subdirectory(trace)
add_subdirectory(threadtimer)
add_subdirectory(backlogMonitor)
add_subdirectory(eventCollector)

simple_package_config()
install(FILES cmake/Masl.cmake cmake/MaslCodeGen.cmake cmake/FindSimpleAdd.cmake DESTINATION lib/cmake )
install(DIRECTORY cmake/simpleadd DESTINATION lib/cmake )