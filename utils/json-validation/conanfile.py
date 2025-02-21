import conan
import os

class ConanFile(conan.ConanFile):
    name = "masl_json_validation"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=1.0 <2]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires("masl_json/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_environment/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_math/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_filesystem/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_strings/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_uuid/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_regex/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']
        
    def package_info(self):
        super().package_info()
        self.runenv_info.define_path('JSON_META_SCHEMA_PATH', os.path.join(self.package_folder, 'res', 'config', 'json-metaschema.json'))
