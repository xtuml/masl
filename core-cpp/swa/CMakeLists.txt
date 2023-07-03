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


find_package(nlohmann_json CONFIG REQUIRED)
find_package(Boost CONFIG REQUIRED)

simple_add_interface_library (
  NAME     swa-headers     
  EXPORT   MaslCore
  LINKS
           Boost::boost
           nlohmann_json::nlohmann_json
  INCLUDES ALL
  )

simple_add_shared_library ( 
  NAME     swa
  SOURCES
           ActivityMonitor.cc
           CommandLine.cc
           Domain.cc
           Duration.cc
           Event.cc
           EventQueue.cc
           EventTimer.cc
           EventTimers.cc
           Exception.cc
           FileDescriptorListener.cc
           ListenerPriority.cc
           Logging.cc
           Main.cc
           NameFormatter.cc
           PluginRegistry.cc
           Process.cc
           ProcessMonitor.cc
           RealTimeSignalListener.cc
           Schedule.cc
           Stack.cc
           StackFrame.cc
           TimerListener.cc
           Timestamp.cc
           console.cc
           parse.cc
  LINKS
           Boost::boost
           swa-headers
           logging
          -lrt
          -ldl
  EXPORT   MaslCore
  INCLUDES ALL
  )

if(CMAKE_CXX_COMPILER_VERSION VERSION_GREATER_EQUAL 12.0 AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 12.3 )
  # Compiler bug... see https://stackoverflow.com/questions/74677951
  # also https://godbolt.org/z/Pe5aE59xG - Should be fixed in gcc 12.3
  target_compile_options(swa PRIVATE -Wno-restrict)
endif()


simple_add_executable (
        NAME arch_test
        SOURCES test.cc
        LINKS swa
)