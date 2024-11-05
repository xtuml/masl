import conan

class ConanFile(conan.ConanFile):
    name = "calculator-masl"
    user = "xtuml"
    channel = "stable"
    python_requires = 'masl_conan/[>=0.1]@xtuml/stable'
    python_requires_extend = 'masl_conan.MaslConanHelper'

    exports_sources= "src/*"

    def requirements(self):
        self.requires("masl_core/[>=0.1]@xtuml/stable")
        self.requires("masl_utils/[>=0.1]@xtuml/stable")

    def build_requirements(self):
        self.tool_requires("masl_codegen/[>=0.1]@xtuml/stable")
