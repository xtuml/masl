import conan
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps
from conan.errors import ConanInvalidConfiguration


class ConanFile(conan.ConanFile):
    name = "xtuml_activemq"
    version = "1.0"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML C++ Software Architecture ActiveMQ IPC"
    topics = ("xtuml", "masl", "activemq", "ipc")

    settings = "os", "compiler", "build_type", "arch"

    exports_sources = "CMakeLists.txt", "src/*", "include/*", "share/*"

    default_options = {
        "log4cplus/*:unicode": False,
        "log4cplus/*:shared": True,
    }

    def validate(self):
        if not self.dependencies["log4cplus"].options.shared:
            raise ConanInvalidConfiguration("log4cplus only works as a shared library")

    def requirements(self):
        self.requires("xtuml_amqp_client/[>=1 <2]@xtuml", transitive_headers=True)
        self.requires('xtuml_logging/[>=1.0 <2]@xtuml')
        self.requires("xtuml_swa/[>=1 <2]@xtuml", transitive_headers=True)
        self.requires("xtuml_idm/[>=1 <2]@xtuml", transitive_headers=True)
        self.requires("fmt/[>=11.1.3 <12]")
        self.requires("boost/[>=1.86.0 <2]", override=True)
        self.requires("log4cplus/[>=2.1.0 <3]")

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
        self.cpp_info.libs = ["ActiveMQ"]
        self.cpp_info.requires.append("xtuml_swa::xtuml_swa")
        self.cpp_info.requires.append("xtuml_idm::xtuml_idm")
        self.cpp_info.requires.append("xtuml_logging::xtuml_logging")
        self.cpp_info.requires.append("xtuml_amqp_client::xtuml_amqp_client")
        self.cpp_info.requires.append("fmt::fmt")
        self.cpp_info.requires.append("log4cplus::log4cplus")
