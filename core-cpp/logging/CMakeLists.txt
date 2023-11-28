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

find_package(Poco CONFIG REQUIRED QUIET COMPONENTS XML Foundation)
find_package(Boost CONFIG REQUIRED QUIET)

simple_add_shared_library(
  NAME     logging           
  SOURCES 
           FilterChannel.cc  
           Logger.cc         
           XMLExtractor.cc   
  LINKS   
           Poco::Foundation
           Poco::XML
           Boost::boost
  INCLUDES 
           logging/Logger.hh 
           logging/Logging.hh 
  EXPORT   MaslCore
  )

install (
  FILES 
    share/full_example.xml
    share/simple_example.xml
    share/configuration.xsd
  DESTINATION share/logging
)
