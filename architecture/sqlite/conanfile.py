import conan
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps
from conan.errors import ConanInvalidConfiguration

class ConanFile(conan.ConanFile):
    name = "xtuml_sqlite"
    version = "1.0.1"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML C++ Software Architecture SQLite Instance Populations"
    topics = ("xtuml", "masl", "sqlite")

    settings = "os", "compiler", "build_type", "arch"

    exports_sources = "CMakeLists.txt", "src/*", "include/*"

    def requirements(self):
        self.requires("xtuml_sql/[>=1 <2]@xtuml", transitive_headers=True,transitive_libs=True)
        self.requires("xtuml_swa/[>=1 <2]@xtuml", transitive_headers=True,transitive_libs=True)
        self.requires("sqlite3/[>=3.48.0 <4]", transitive_headers=True,transitive_libs=True)

    default_options = {
        "sqlite3/*:shared": True,
    }
    
    def validate(self):
        if not self.dependencies["sqlite3"].options.shared:
            raise conan.ConanInvalidConfiguration("sqlite3 only works as a shared library")


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
        self.cpp_info.libs = ["xtuml_sqlite"]
        self.cpp_info.requires.append("xtuml_sql::xtuml_sql")
        self.cpp_info.requires.append("xtuml_swa::xtuml_swa")
        self.cpp_info.requires.append("sqlite3::sqlite3")
