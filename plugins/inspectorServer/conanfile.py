import conan
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps


class ConanFile(conan.ConanFile):
    name = "xtuml_inspector_server"
    version = "1.0.1"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML C++ Software Architecture Inspector Server"
    topics = ("xtuml", "masl", "inspector")

    settings = "os", "compiler", "build_type", "arch"

    exports_sources = "CMakeLists.txt", "src/*", "include/*"

    def requirements(self):
        self.requires(
            "xtuml_swa/[>=1.0 <2]@xtuml",
            transitive_headers=True,
            transitive_libs=True,
        )
        self.requires(
            "xtuml_metadata/[>=1.0 <2]@xtuml",
            transitive_headers=True,
            transitive_libs=True,
        )
        self.requires("asio/[>=1.31.0 <2]", transitive_headers=True)

    def layout(self):
        cmake_layout(self)

    def generate(self):
        deps = CMakeDeps(self)
        deps.generate()
        tc = CMakeToolchain(self)
        tc.generate()

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()

    def package(self):
        cmake = CMake(self)
        cmake.install()

    def package_info(self):
        self.cpp_info.libs = ["Inspector"]
        self.cpp_info.requires.append("xtuml_swa::xtuml_swa")
        self.cpp_info.requires.append("xtuml_metadata::xtuml_metadata")
        self.cpp_info.requires.append("asio::asio")
