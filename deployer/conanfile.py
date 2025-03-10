import collections
import shutil
import subprocess
from pathlib import Path

import conan


class Deployer:
    def __init__(self, conanfile: conan.ConanFile, base_dir=None):
        self.conanfile = conanfile
        self.deploy_folder = Path(conanfile.deploy_folder)
        if base_dir:
            self.deploy_folder = self.deploy_folder / base_dir

        self._files: dict[Path, set[Path]] = collections.defaultdict(set)

    def _add(self, src, dest):
        self._files[dest].add(src)

    def _deps(self, packages: list | None):
        return [self.conanfile.dependencies[p] for p in packages or []] or self.conanfile.dependencies.host.values()

    def _bindirs(self, packages: list | None = None):
        return (Path(d) for dep in self._deps(packages) for d in dep.cpp_info.bindirs)

    def _libdirs(self, packages: list | None = None):
        return (Path(d) for dep in self._deps(packages) for d in dep.cpp_info.libdirs)

    def _resdirs(self, packages: list | None = None):
        return (Path(d) for dep in self._deps(packages) for d in dep.cpp_info.resdirs)

    def _dynamic_libs(self, parent: Path, dest='lib'):

        env = {"LD_LIBRARY_PATH": ":".join(f"{d}" for d in self._libdirs())}
        ldd = subprocess.run(["ldd", parent], env=env, capture_output=True, check=True, text=True)
        for line in ldd.stdout.splitlines():
            split = line.split()
            if len(split) == 4:
                lib, _, path, _ = split
                if not lib.startswith("libc.so"):
                    self._add(path, dest)

    def library(self, pattern: str = None, wrap: bool = True, packages: list | None = None, dest='lib'):
        if wrap:
            pattern = f"lib{pattern}.so"
        for lib in (f for dir in self._libdirs(packages) for f in dir.glob(pattern)):
            self._add(lib, dest)
            self._dynamic_libs(lib)

    def executable(self, pattern: str, packages: list | None = None, dest='bin', libdest='lib'):
        for exe in (f for dir in self._bindirs(packages) for f in dir.glob(pattern)):
            self._add(exe, dest)
            self._dynamic_libs(exe, dest=libdest)

    def resource(self, pattern: str, packages: list | None = None, dest='res'):
        for file in (f for dir in self._resdirs(packages) for f in dir.glob(pattern)):
            self._add(file, dest)

    def deploy(self):
        for dest, files in self._files.items():
            destdir = self.deploy_folder / dest
            destdir.mkdir(exist_ok=True, parents=True)
            for file in files:
                self.conanfile.output.info(f"Deploying {file} to {destdir}")
                shutil.copy(file, destdir)


class ConanFile(conan.ConanFile):
    name = "xtuml_deployer"
    version = "1.0"
    user = "xtuml"

    package_type = 'python-require'

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML conan helper functions for building MASL domains"
    topics = ("xtuml", "masl")
