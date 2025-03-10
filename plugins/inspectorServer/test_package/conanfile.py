import os

from conan import ConanFile
from conan.tools.cmake import CMake, cmake_layout
from conan.tools.build import can_run
from conan.tools.env import Environment


class TestConan(ConanFile):
    settings = "os", "compiler", "build_type", "arch"
    generators = "CMakeDeps", "CMakeToolchain"

    def requirements(self):
        self.requires(self.tested_reference_str)

    def generate(self):
        env = Environment()
        env.append_path("LD_LIBRARY_PATH", self.build_folder)
        envvars = env.vars(self, scope="run")
        envvars.save_script("plugin_path")

    def build(self):
        cmake = CMake(self)
        cmake.configure()
        cmake.build()

    def layout(self):
        cmake_layout(self)

    def test(self):
        if can_run(self):
            cmd = os.path.join(self.cpp.build.bindir, "testlib")
            self.run(
                f"{cmd} -mainloop-disable -util Inspector -inspector-port 0",
                env="conanrun",
            )
