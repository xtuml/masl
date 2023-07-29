#  ----------------------------------------------------------------------------
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ----------------------------------------------------------------------------
#  Classification: UK OFFICIAL
#  ----------------------------------------------------------------------------

import conan
from conan.tools.cmake import CMake, cmake_layout
from conan.tools.files import copy
import os

class ConanFile(conan.ConanFile):
    name = "masl_core"
    version = "0.1"
    user = 'xtuml'
    channel = 'stable'

    requires = ("openssl/[>=3.1]",
                "poco/[>=1.12]",
                "fmt/[>=10.0]",
                "nlohmann_json/[>=3.11.2]",
                "boost/[>=1.82]",
                "sqlite3/[>=3.41]",
                "libuuid/[>=1.0]"
                )

    generators = ("CMakeDeps",
                  "CMakeToolchain",
                  "VirtualBuildEnv",
                  "VirtualRunEnv")

    settings = "os", "compiler", "build_type", "arch"

    def layout(self):
        cmake_layout(self)

    exports_sources= ( "CMakeLists.txt",
                       "asn1/*",
                       "backlogMonitor/*",
                       "cmake/*",
                       "codeCoverage/*",
                       "eventCollector/*",
                       "inspectorServer/*",
                       "logging/*",
                       "metadata/*",
                       "sockets/*",
                       "sql/*",
                       "sqlite/*",
                       "swa/*",
                       "threadtimer/*",
                       "trace/*",
                       "transient/*",
                       )

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()


    def package(self):
        cmake = CMake(self)
        cmake.install()

    def package_info(self):
        self.cpp_info.set_property("cmake_find_mode", "none")
        self.cpp_info.builddirs.append(os.path.join('lib', 'cmake'))
