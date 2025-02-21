import conan
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps


class ConanFile(conan.ConanFile):
    name = "xtuml_sql"
    version = "1.0"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML C++ Software Architecture SQL Instance Populations"
    topics = ("xtuml", "masl", "sql")

    settings = "os", "compiler", "build_type", "arch"

    exports_sources = "CMakeLists.txt", "src/*", "include/*"

    def requirements(self):
        self.requires("xtuml_cmake_helpers/[>=1.0 <2]@xtuml", visible=False)
        self.requires("xtuml_swa/[>=1 <2]@xtuml", transitive_headers=True)
        self.requires("boost/[>=1.86.0 <2]", transitive_headers=True)

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
        self.cpp_info.libs = ["xtuml_sql"]
        self.cpp_info.requires.append("xtuml_swa::xtuml_swa")
        self.cpp_info.requires.append("boost::boost")
