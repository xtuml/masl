import conan

class ConanFile(conan.ConanFile):
    name = "masl_schedule"
    version = "1.2.1"
    user = "xtuml"
    
    python_requires = 'xtuml_masl_conan/[>=5.0 <6]@xtuml'
    python_requires_extend = 'xtuml_masl_conan.MaslConanHelper'

    def omit_requirements(self):
        return ['xtuml_sql']
