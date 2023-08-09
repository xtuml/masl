import shutil

import conan
from conan.errors import ConanException

from conan.tools.files import save
from conan.tools.files import copy
from conan.tools.cmake import CMake, cmake_layout

import glob
import os
import textwrap


class ConanFile(conan.ConanFile):
    name = 'masl_conan'
    version = '0.1'
    user = 'xtuml'
    channel = 'stable'

    def build(self):
        pass

    def package(self):
        pass


class MaslGen(object):
    def __init__(self, conanfile):
        self.conanfile: ConanFile = conanfile
        self.main = 'org.xtuml.masl.Main'
        self.output = conanfile.build_folder
        self.extras = []
        self.sources = []

        self.domainpath = [os.path.join(inc, 'masl')
                           for dep in self.conanfile.dependencies.values()
                           for inc in dep.cpp_info.includedirs
                           if os.path.exists(os.path.join(inc, 'masl'))]

    def multi_generate(self, sources, output='.', extras=None, domainpath=None):
        extras = extras or []
        domainpath = domainpath or []
        output = output or self.output
        names = []
        validated_sources = []
        for source in sources:
            if (not os.path.exists(source)):
                if (os.path.exists(os.path.join(self.conanfile.source_folder, source))):
                    source = os.path.join(self.conanfile.source_folder, source)
                else:
                    raise ConanException("Masl source file {} not found.".format(source))
            validated_sources.append(source)

        dp = domainpath + list({os.path.dirname(s) for s in validated_sources})

        for source in validated_sources:
            path, file = os.path.split(source)
            name, __ = os.path.splitext(file)
            self.generate(source, output=os.path.join(output, name), extras=extras, domainpath=dp)
            names.append(name)
        return names

    def generate(self, source, output='.', extras=None, domainpath=None):
        extras = extras or []
        domainpath = domainpath or []

        dp = set(self.domainpath + domainpath)
        dp_args = ('-domainpath ' + ':'.join(dp)) if dp else ''

        output = output if os.path.isabs(output) else os.path.normpath(os.path.join(self.output, output))

        if (not os.path.exists(source)):
            if (os.path.exists(os.path.join(self.conanfile.source_folder, source))):
                source = os.path.join(self.conanfile.source_folder, source)
            else:
                raise ConanException("Masl source file {} not found.".format(source))

        extra_args = ' '.join(self.extras + extras)

        self.sources.append(source)

        name, ext = os.path.splitext(os.path.basename(source))
        source_type = f'-{ext[1:]}'
        output_args = '-output ' + output

        cmd = f'masl-codegen {dp_args} {source_type} {source} {output_args} -version {self.conanfile.version} {extra_args}'
        return self.conanfile.run(cmd)


class MaslConanHelper():
    exports_sources = 'src/*', 'masl/*', 'schedule/*', 'config/*'
    generators = 'CMakeDeps', 'CMakeToolchain', 'VirtualBuildEnv', 'VirtualRunEnv'
    settings = 'os', 'compiler', 'build_type', 'arch'

    options = {'test': [True, False]}
    default_options = {'test': False}

    def layout(self):
        cmake_layout(self)
        self.cpp.package.resdirs = ['res']

    def requirements(self):
        if 'masl_codegen' not in ( r.ref.name for r in self.requires.values()):
            self.tool_requires(f'masl_codegen/0.1@xtuml/stable',visible=True)
        if 'masl_core' not in ( r.ref.name for r in self.requires.values()):
            self.requires(f'masl_core/0.1@xtuml/stable')

    def masl_src_roots(self):
        src_roots = []
        for sub in ['src', 'masl']:
            if os.path.exists(os.path.join(self.source_folder, sub)):
                src_roots.append(os.path.join(self.source_folder, sub))
        if not src_roots:
            src_roots = [self.source_folder]
        return src_roots

    def masl_src(self):
        all = [glob.glob(os.path.join(root, '**', '*.mod'), recursive=True) +
               glob.glob(os.path.join(root, '**', '*.prj'), recursive=True)
               for root in self.masl_src_roots()]
        src = [f for items in all for f in items]
        if not src:
            raise IOError('Neither .mod nor .prj file found.')
        return src

    def masl_extras(self):
        return []

    def build(self):
        extras = self.masl_extras()
        if self.options.test:
            extras.append('-test')

        subdirs = MaslGen(self).multi_generate(self.masl_src(), extras=extras, output='generated')

        cmake_subdirs = '\n'.join('add_subdirectory({})'.format(d)
                                  for d in subdirs
                                  if os.path.isfile(os.path.join(self.build_folder, 'generated', d, 'CMakeLists.txt')))

        if cmake_subdirs:
            save(self, 'generated/CMakeLists.txt', textwrap.dedent(f"""
                cmake_minimum_required(VERSION 3.9)
                project({self.name})

                enable_testing()
                """) + cmake_subdirs + '\n')

            cmake = CMake(self)
            cmake.configure(build_script_folder=os.path.join(self.build_folder, 'generated'))
            cmake.build()
            cmake.test()

    def package(self):
        cmake = CMake(self)
        cmake.install()
        res_dir = os.path.join(self.package_folder, 'res')
        lib_dir = os.path.join(self.package_folder, 'lib')
        inc_dir = os.path.join(self.package_folder, 'include')
        masl_include = os.path.join(inc_dir, 'masl')

        for source in self.masl_src():
            src, srcfile = os.path.split(source)
            name, ext = os.path.splitext(srcfile)

            masl_src_resource = os.path.join(res_dir, 'masl-src', name)

            copy(self, srcfile, src=src, dst=masl_src_resource)
            copy(self, '*.tr', src=src, dst=masl_src_resource)
            if ext == '.mod':
                # Use a raw copy, as conan copy preserves the symlink, and we want a copy
                os.makedirs(masl_include, exist_ok=True)
                shutil.copy(os.path.join(src, name + '.int'), os.path.join(masl_include, name + '.int'))

                copy(self, name + '.int', src=src, dst=masl_src_resource)
                copy(self, '*.al', src=src, dst=masl_src_resource)
                copy(self, '*.fn', src=src, dst=masl_src_resource)
                copy(self, '*.svc', src=src, dst=masl_src_resource)
                copy(self, '*.scn', src=src, dst=masl_src_resource)
                copy(self, '*.ext', src=src, dst=masl_src_resource)

        copy(self, 'config/*', src=self.source_folder, dst=res_dir)
        copy(self, 'schedule/*', src=self.source_folder, dst=res_dir)

    def package_info(self):
        self.cpp_info.set_property("cmake_find_mode", "none")
        self.cpp_info.builddirs.append(os.path.join('lib', 'cmake'))
        for inc in self.cpp_info.includedirs:
            self.buildenv_info.append('MASL_DOMAINPATH', os.path.join(self.package_folder, inc, 'masl'), )
        for res in self.cpp_info.resdirs:
            self.runenv_info.append('MASL_SRCPATH', os.path.join(self.package_folder, res, 'masl-src'), )
        for d in self.cpp_info.bindirs:
            self.runenv_info.append_path("PATH",os.path.join(self.package_folder,d))
        for d in self.cpp_info.libdirs:
            self.runenv_info.append_path("LD_LIBRARY_PATH",os.path.join(self.package_folder,d))