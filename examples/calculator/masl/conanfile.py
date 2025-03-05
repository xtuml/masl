import conan

class ConanFile(conan.ConanFile):
    name = "masl_examples_calculator"
    version = "1.0"
    user = "xtuml"

    python_requires = 'xtuml_masl_conan/[>=1.0 <2]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def requirements(self):
        self.requires('masl_uuid/[>=1.0 <2]@xtuml')
        super().requirements()
        
    def omit_requirements(self):
        return ['nlohmann_json']
