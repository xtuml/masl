import conan
from conan.tools.files import copy
from conan.tools.layout import basic_layout


class ConanFile(conan.ConanFile):
    name = "xtuml_sockets"
    version = "1.0"
    user = "xtuml"

    package_type = "header-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "Linux Socket helpers"
    topics = ("xtuml", "masl", "socket")

    settings = None

    exports_sources = "include/*"

    def layout(self):
        basic_layout(self)

    def package(self):
        copy(
            self,
            "include/sockets/*",
            self.source_folder,
            self.package_folder,
            keep_path=True,
        )

    def package_info(self):
        self.cpp_info.bindirs = []
        self.cpp_info.libdirs = []
