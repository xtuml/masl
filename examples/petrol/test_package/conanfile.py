import conan
from conan.tools.build import can_run
from conan.tools.layout import basic_layout


class ConanFile(conan.ConanFile):
    settings = "os", "compiler", "build_type", "arch"

    def layout(self):
        basic_layout(self)

    def requirements(self):
        self.requires(self.tested_reference_str)

    def test(self):
        if can_run(self):
            self.run(f"PETROL_PROC_sqlite -db :memory: -util Inspector -postinit {self.source_folder}/run.sch", env="conanrun")
