import os

from conan import ConanFile
from conan.tools.build import can_run
from conan.tools.cmake import CMake, cmake_layout


class TestConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    generators = "CMakeDeps", "CMakeToolchain"

    def requirements(self):
        self.tool_requires(self.tested_reference_str)
        self.requires("xtuml_swa/[>=1.0 <2]@xtuml")
        self.requires("xtuml_asn1/[>=1.0 <2]@xtuml")
        self.requires("xtuml_inspector_server/[>=1.0 <2]@xtuml")
        self.requires("xtuml_transient/[>=1.0 <2]@xtuml")
        self.requires("xtuml_sqlite/[>=1.0 <2]@xtuml")
        self.requires("xtuml_kafka/[>=1.0 <2]@xtuml")

    def layout(self):
        cmake_layout(self)

    def build(self):
        src = os.path.join(self.source_folder,'Example.mod')
        self.run(f"masl-codegen -output generated -mod {src}", env="conanbuild")
        
        cmake = CMake(self)
        cmake.configure(build_script_folder=os.path.join(self.build_folder, 'generated'))
        cmake.build()


    def test(self):
        if can_run(self):
            cmd = os.path.join(self.cpp.build.bindir, "Example_transient_standalone")
            self.run(f"{cmd} -mainloop-disable", env="conanrun")
