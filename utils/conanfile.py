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
import os

from conan.tools.files import copy


class ConanFile(conan.ConanFile):
    name = "masl_utils"
    version = "4.2.1"
    user = 'xtuml'
    channel = 'stable'
    python_requires = 'masl_conan/4.2.1@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    exports_sources = "*_OOA/*"

    def requirements(self):
        self.requires(f"masl_core/{self.version}@xtuml/stable")
        self.requires("libuuid/1.0.3")
        self.requires("nlohmann_json/3.11.2")
        self.tool_requires(f"masl_codegen/{self.version}@xtuml/stable")
        self.test_requires('gtest/1.14.0')

    def package(self):
        super().package()
        res_dir = os.path.join(self.package_folder, 'res')
        copy(self, 'json-metaschema.json', src=os.path.join(self.source_folder, 'JSONValidation_OOA', 'custom'), dst=res_dir)

    def package_info(self):
        super().package_info()
        self.runenv_info.define_path('JSON_META_SCHEMA_PATH', os.path.join(self.package_folder, 'res', 'json-metaschema.json'))
