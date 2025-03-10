import conan

class ConanFile(conan.ConanFile):
    name = "masl_uuid"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires("libuuid/1.0.3")
        super().requirements()

    def omit_requirements(self):
        return ['boost', 'xtuml_sql', 'nlohmann_json']

    def package_info(self):
        self.cpp_info.components["UUID"].requires += ['libuuid::libuuid']
        super().package_info()
