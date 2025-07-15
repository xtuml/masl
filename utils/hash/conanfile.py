import conan

class ConanFile(conan.ConanFile):
    name = "masl_hash"
    version = "1.2.3"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    default_options = {
        "inspector" : True,
        "metadata" : True,
        "transient" : True,
        "sqlite" : True,
        "idm" : True,
        "amqp" : False,
        "test" : False,
        "openssl/*:shared": True
    }

    def validate(self):
        if not self.dependencies["openssl"].options.shared:
            raise ConanInvalidConfiguration("openssl only works as a shared library")

    def requirements(self):
        self.requires("openssl/[>=3.4.1 <4]", transitive_headers=True, transitive_libs=True)
        self.requires('xxhash/[>=0.8.2 <1]')
        self.test_requires('gtest/[>=1.14.0 <2]')
        super().requirements()

    def omit_requirements(self):
        return ['xtuml_sql']

    def package_info(self):
        self.cpp_info.components["Hash"].requires += ['openssl::openssl', 'xxhash::xxhash']
        super().package_info()
