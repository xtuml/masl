import conan
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps
from conan.errors import ConanInvalidConfiguration


class ConanFile(conan.ConanFile):
    name = "xtuml_logging"
    version = "1.0.1"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "Modern C++ wrapper around log4cplus"
    topics = ("xtuml", "masl", "logging", "log4cplus")

    settings = "os", "compiler", "build_type", "arch"
    default_options = {
        "log4cplus/*:unicode": False,
        "log4cplus/*:shared": True,
    }

    exports_sources = "CMakeLists.txt", "src/*", "include/*", "share/*"

    def validate(self):
        if not self.dependencies["log4cplus"].options.shared:
            raise ConanInvalidConfiguration("log4cplus only works as a shared library")

    def requirements(self):
        self.requires(
            "fmt/[>=11 <12]",
            transitive_headers=True,
            transitive_libs=True,
        )
        self.requires("nlohmann_json/[>=3.11 <4]", visible=False)
        self.requires(
            "log4cplus/[>=2.1.0 <3]",
            transitive_headers=True,
            transitive_libs=True,
        )

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
        self.cpp_info.libs = ["xtuml_logging"]
        self.cpp_info.requires.append("log4cplus::log4cplus")
        self.cpp_info.requires.append("fmt::fmt")
