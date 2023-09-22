import conan

class ConanFile(conan.ConanFile):
    name = "calculator-masl"
    version = "4.1.1"
    user = "xtuml"
    channel = "stable"
    python_requires = 'masl_conan/[>=4.1.1]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    def requirements(self):
        super().requirements()
        self.requires("masl_utils/4.1.1@xtuml/stable")
