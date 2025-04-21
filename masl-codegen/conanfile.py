import conan
from conan.tools.files import copy
from conan.tools.layout import basic_layout
import os

class ConanFile(conan.ConanFile):
    name = "xtuml_masl_codegen"
    version = "1.1.2"
    user = 'xtuml'

    package_type = 'application'
    
    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML Masl Parser and Code generator"
    topics = ("xtuml", "masl")

    def layout(self):
        basic_layout(self)


    exports_sources= ( "CMakeLists.txt",
                       "gradle/*",
                       "src/*",
                       "bin/*",
                       "gradlew",
                       "build.gradle",
                       "settings.gradle")

    def build(self):
        self.run(f'{self.source_folder}/gradlew -PbuildDir={self.build_folder} -p {self.source_folder} installDist')


    def package(self):
        copy(self,"*",src=os.path.join(self.build_folder, 'install/masl-codegen/bin'), dst=os.path.join(self.package_folder,"bin"))
        copy(self,"*",src=os.path.join(self.build_folder, 'install/masl-codegen/lib'), dst=os.path.join(self.package_folder,"lib"))
