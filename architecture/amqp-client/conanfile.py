import conan
from conan.tools.env import Environment
from conan.tools.cmake import CMakeToolchain, CMake, cmake_layout, CMakeDeps
from conan.errors import ConanInvalidConfiguration
import os


class ConanFile(conan.ConanFile):
    name = "xtuml_amqp_client"
    version = "1.1.3"
    user = "xtuml"

    package_type = "shared-library"

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML C++ Software Architecture AMQP Client interface"
    topics = ("xtuml", "masl", "amqp", "ipc")

    settings = "os", "compiler", "build_type", "arch"

    exports_sources = "CMakeLists.txt", "src/*", "include/*", "share/*", "docs/*"

    default_options = {
        "cyrus-sasl/*:shared": True,
    }

    def validate(self):
        if not self.dependencies["cyrus-sasl"].options.shared:
            raise ConanInvalidConfiguration("cyrus-sasl only works as a shared library")

    def requirements(self):
        self.requires(
            "fmt/[>=11 <12]",
            transitive_headers=True,
            transitive_libs=True,
        )
        self.requires("nlohmann_json/[>=3.11 <4]", transitive_headers=True)
        self.requires("xtuml_logging/[>=1.0 <2]@xtuml")
        self.requires("asio/[>=1.31.0 <2]", transitive_headers=True)
        self.requires(f"cyrus-sasl/[>=2.1.28 <3]")
        self.test_requires("gtest/[>=1.15.0 <2]")
        self.requires("cli11/[>=2.4.2 <3]", visible=False)
        self.requires("boost/[>=1.86.0 <2]", transitive_headers=True)
        self.requires("openssl/[>=3.4.1 <4]", transitive_headers=True)

    def layout(self):
        cmake_layout(self)

    def generate(self):
        deps = CMakeDeps(self)
        deps.generate()
        tc = CMakeToolchain(self)
        tc.generate()

        sasl_env = Environment()
        for l in self.dependencies["cyrus-sasl"].cpp_info.libdirs:
            sasl_env.append_path("SASL_PATH", os.path.join(l, "sasl2"))
        sasl_vars = sasl_env.vars(self, scope="run")
        sasl_vars.save_script("sasl_env")

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()
        cmake.test()

    def package(self):
        cmake = CMake(self)
        cmake.install()

    def package_info(self):
        self.cpp_info.libs = ["xtuml_amqp_client"]
        self.cpp_info.requires.append("fmt::fmt")
        self.cpp_info.requires.append("asio::asio")
        self.cpp_info.requires.append("xtuml_logging::xtuml_logging")
        self.cpp_info.requires.append("cyrus-sasl::cyrus-sasl")
        self.cpp_info.requires.append("nlohmann_json::nlohmann_json")
        self.cpp_info.requires.append("boost::headers")
        self.cpp_info.requires.append("openssl::openssl")
        for l in self.dependencies["cyrus-sasl"].cpp_info.libdirs:
            self.runenv_info.append_path(
                "SASL_PATH",
                os.path.join(
                    self.dependencies["cyrus-sasl"].package_folder, l, "sasl2"
                ),
            )
