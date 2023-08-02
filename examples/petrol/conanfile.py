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

class ConanFile(conan.ConanFile):
    name = "masl_examples_petrol"
    version = "0.1"
    user = 'xtuml'
    channel = 'stable'
    python_requires = 'masl_conan/[>=0.1]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    exports_sources= "src/*"

    def requirements(self):
        self.requires("masl_core/[>=0.1]@xtuml/stable")
        self.tool_requires("masl_codegen/[>=0.1]@xtuml/stable")