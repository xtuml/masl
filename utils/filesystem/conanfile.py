import conan

class ConanFile(conan.ConanFile):
    name = "masl_filesystem"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'
    
    def requirements(self):
        self.requires('masl_host/[>=1.0 <2]@xtuml', transitive_libs=True, transitive_headers=True)
        self.requires('openssl/[>=3.4.1 <4]')
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']

    def package_info(self):
        self.cpp_info.components["Filesystem"].requires += ['openssl::openssl']
        super().package_info()
