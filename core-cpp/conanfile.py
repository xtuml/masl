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
import os

class ConanFile(conan.ConanFile):
    name = "masl_core"
    user = 'xtuml'
    channel = 'stable'

    generators = ("CMakeDeps",
                  "CMakeToolchain",
                  "VirtualBuildEnv",
                  "VirtualRunEnv",
                  )

    settings = "os", "compiler", "build_type", "arch"

    def layout(self):
        cmake_layout(self)

    def requirements(self):
        self.requires("openssl/3.3.2", transitive_headers=True)
        self.requires("poco/1.13.3")
        self.requires("fmt/11.0.2", transitive_headers=True)
        self.requires("log4cplus/2.1.0")
        self.requires("nlohmann_json/3.11.2", transitive_headers=True)
        self.requires("boost/1.86.0", transitive_headers=True, force=True) # CppKafka is asking for 1.81.0
        self.requires("sqlite3/3.48.0",force=True)
        self.requires("zlib/1.3")
        self.requires("libuuid/1.0.3")
        self.requires("cppkafka/0.4.0")
        self.requires("asio/1.31.0", transitive_headers=True)
        self.requires("cyrus-sasl/2.1.28")
        self.test_requires("gtest/1.15.0")
        self.requires('cli11/2.4.2')

    default_options = {'log4cplus/*:unicode': False}

    exports_sources= ( "CMakeLists.txt",
                       "amqp-client/*",
                       "asn1/*",
                       "backlogMonitor/*",
                       "cmake/*",
                       "codeCoverage/*",
                       "eventCollector/*",
                       "inspectorServer/*",
                       "kafka/*",
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

    def configure(self):
        self.options["*"].shared = True

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()
#        cmake.ctest(cli_args=['--output-on-failure'])


    def package(self):
        cmake = CMake(self)
        cmake.install()

    def package_info(self):
        self.cpp_info.set_property("cmake_find_mode", "none")
        self.cpp_info.builddirs.append(os.path.join('lib', 'cmake'))
        for d in self.cpp_info.bindirs:
            self.runenv_info.append_path("PATH",os.path.join(self.package_folder,d))
        for d in self.cpp_info.libdirs:
            self.runenv_info.append_path("LD_LIBRARY_PATH",os.path.join(self.package_folder,d))
