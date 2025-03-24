import conan

class ConanFile(conan.ConanFile):
    name = "masl_test"
    version = "1.2"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires("masl_json/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_assertions/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_logger/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_filesystem/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_schedule/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        self.requires("masl_format/[>=1.0 <2]@xtuml", transitive_libs=True, transitive_headers=True)
        super().requirements()

    def omit_requirements(self):
        return ['nlohmann_json']
