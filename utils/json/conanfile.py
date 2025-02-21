import conan

class ConanFile(conan.ConanFile):
    name = "masl_json"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=1.0 <2]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires('nlohmann_json/[>=3.11 <4]')
        self.test_requires('gtest/[>=1.14.0 <2]')
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']

    def package_info(self):
        self.cpp_info.components["Json"].requires += ['nlohmann_json::nlohmann_json']
        super().package_info()