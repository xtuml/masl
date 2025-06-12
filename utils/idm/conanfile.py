import conan

class ConanFile(conan.ConanFile):
    name = "masl_idm"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires('xtuml_idm/[>=1.0 <2]@xtuml')
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql', 'nlohmann_json', 'boost']


    def package_info(self):
        self.cpp_info.components["IDM"].requires += ['xtuml_idm::xtuml_idm']
        super().package_info()
