from conan import ConanFile, conan_version
from conan.tools.files import copy
from conan.tools.layout import basic_layout


import os.path


class CMakeSimpleAddConan(ConanFile):
    name = "xtuml_cmake_helpers"
    version = "1.0"
    user = "xtuml"

    package_type = "build-scripts"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML CMake helper functions"
    topics = ("xtuml", "masl", "cmake")

    settings = None

    exports_sources = ("*.cmake",)

    def layout(self):
        basic_layout(self)

    def build(self):
        pass

    def package(self):
        copy(self, "*.cmake", self.source_folder, self.package_folder)

    def package_info(self):
        self.cpp_info.set_property("cmake_build_modules", ["helpers.cmake"])
