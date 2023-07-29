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
from conan.tools.files import save
import os
import glob
import textwrap

class ConanFile(conan.ConanFile):
    name = "masl_utils"
    version = "0.1"
    user = 'xtuml'
    channel = 'stable'
    requires = (
            "masl_core/[>=0.1]@xtuml/stable",
            "masl_codegen/[>=0.1]@xtuml/stable"
            )

    generators = ("CMakeDeps",
                  "CMakeToolchain",
                  "VirtualBuildEnv",
                  "VirtualRunEnv")

    settings = "os", "compiler", "build_type", "arch"


    def layout(self):
        cmake_layout(self)

    exports_sources= ( "CMakeLists.txt",
                       "*_OOA/*")

    def build(self):
        cmakelists=textwrap.dedent('''\
        cmake_minimum_required(VERSION 3.27.1)
        project(masl_utils)
        ''')
        for src_file in ( glob.glob(os.path.join(self.source_folder,'**','*.mod')) +
                          glob.glob(os.path.join(self.source_folder,'**','*.prj'))):
            path,file = os.path.split(src_file)
            name, type = os.path.splitext(file)
            self.run(f'masl-codegen -output generated/{name} -{type[1:]} {src_file}')
            cmakelists += f'add_subdirectory({self.build_folder}/generated/{name})\n'


        save(self,'generated/CMakeLists.txt', cmakelists)

        cmake = CMake(self)
        cmake.configure(build_script_folder=os.path.join(self.build_folder,'generated'))
        cmake.build()

    def package(self):
        cmake = CMake(self)
        cmake.install()
