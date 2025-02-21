import conan

class ConanFile(conan.ConanFile):
    name = "masl_format"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=1.0 <2]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires("fmt/[>=11.1.3 <12]")
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']

    def package_info(self):
        self.cpp_info.components["Format"].requires += ['fmt::fmt']
        super().package_info()