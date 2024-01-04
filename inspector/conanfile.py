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
import git

class ConanFile(conan.ConanFile):
    name = "masl_inspector"
    version = ver[1:] if (ver := git.Repo(search_parent_directories=True).git.describe('--tags')).startswith('v') else ver
    user = 'xtuml'
    channel = 'stable'

    settings = 'build_type'

    generators = ("VirtualBuildEnv",
                  "VirtualRunEnv")

    def layout(self):
        cmake_layout(self)

    exports_sources= ( "CMakeLists.txt",
                       "gradle/*",
                       "src/*",
                       "bin/*",
                       "gradlew",
                       "gradlew.bat",
                       "build.gradle",
                       "settings.gradle")

    def build(self):
        self.run(f'{self.source_folder}/gradlew -PbuildDir={self.build_folder} -p {self.source_folder} installDist')


    def package(self):
        copy(self,"*",src=os.path.join(self.source_folder, 'bin'), dst=os.path.join(self.package_folder,"bin"))
        copy(self,"*",src=os.path.join(self.build_folder, 'install/masl-inspector/lib'), dst=os.path.join(self.package_folder,"lib"))

