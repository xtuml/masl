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

find_package(fmt CONFIG REQUIRED)
find_package(log4cplus CONFIG REQUIRED)
find_package(nlohmann_json CONFIG REQUIRED)

simple_add_shared_library(
  NAME     logging           
  SOURCES  log.cc
  LINKS
        fmt::fmt
        log4cplus::log4cplus
        nlohmann_json::nlohmann_json
  INCLUDES
           logging/log.hh
  EXPORT   MaslCore
  )


simple_add_executable(
        NAME logging-example
        SOURCES example.cc
        LINKS logging
)

install (
  FILES 
    share/logging-example.properties
  DESTINATION share/logging
)
