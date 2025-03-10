import conan

class ConanFile(conan.ConanFile):
    name = "masl_logger"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires('xtuml_logging/[>=1.0 <2]@xtuml')
        self.requires('masl_format/[>=1.0 <2]@xtuml', transitive_libs=True, transitive_headers=True)
        self.test_requires('gtest/[>=1.14.0 <2]')
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']


    def package_info(self):
        self.cpp_info.components["Logger"].requires += ['xtuml_logging::xtuml_logging']
        super().package_info()
