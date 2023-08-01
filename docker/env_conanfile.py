import conan

class ConanFile(conan.ConanFile):
    generators = "VirtualRunEnv", "VirtualBuildEnv"
    settings = "os", "compiler", "build_type", "arch"

    def requirements(self):
        self.requires("masl_utils/[>=0.1]@xtuml/stable",run=True)
        self.requires("masl_codegen/[>=0.1]@xtuml/stable",run=True)
