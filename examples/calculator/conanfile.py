import conan

class ConanFile(conan.ConanFile):
    name = "calculator-masl"
    user = "xtuml"
    channel = "stable"
    python_requires = 'masl_conan/[>=0.1]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    def requirements(self):
        super().requirements()
        self.requires(f"masl_utils/{self.version}@xtuml/stable")
