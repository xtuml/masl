import conan

class ConanFile(conan.ConanFile):
    name = "masl_regex"
    version = "1.2"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires("boost/[>=1.86.0 <2]")
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']

    def package_info(self):
        self.cpp_info.components["Regex"].requires += ['boost::regex']
        super().package_info()
