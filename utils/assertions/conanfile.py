import conan

class ConanFile(conan.ConanFile):
    name = "masl_assertions"
    version = "1.0"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=1.0 <2]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def omit_requirements(self):
        return ['xtuml_sql', 'nlohmann_json']
