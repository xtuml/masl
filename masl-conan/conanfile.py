import shutil

import conan
from conan.errors import ConanException

from conan.tools.files import save
from conan.tools.files import copy
from conan.tools.env import Environment
from conan.tools.cmake import CMake, cmake_layout

import glob
import os
import textwrap
import json
from pathlib import Path

class ConanFile(conan.ConanFile):
    name = 'xtuml_masl_conan'
    version = '5.0'
    user = 'xtuml'

    package_type = 'python-require'

    license = "Apache-2.0"
    url = "https://github.com/xtuml/masl"
    description = "xtUML conan helper functions for building MASL domains"
    topics = ("xtuml", "masl")

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

        args = []

        dp = set(self.domainpath + domainpath)
        if dp:
            args+= ['-domainpath',':'.join(dp)]

        args += ['-defaultPackage',self.conanfile.name]

        for dep in self.conanfile.dependencies.values():
            domains = dep.cpp_info.get_property('masl_domains')
            if domains:
                for domain, pkg in domains.items():
                    args += ['-domainPackage', domain, pkg]

        output = output if os.path.isabs(output) else os.path.normpath(os.path.join(self.output, output))

        if (not os.path.exists(source)):
            if (os.path.exists(os.path.join(self.conanfile.source_folder, source))):
                source = os.path.join(self.conanfile.source_folder, source)
            else:
                raise ConanException("Masl source file {} not found.".format(source))

        self.sources.append(source)

        name, ext = os.path.splitext(os.path.basename(source))
        args += [ f'-{ext[1:]}', source ]

        args += ['-output', output]   
        if self.conanfile.version:
            args += ['-version', self.conanfile.version]

        if not self.conanfile.options.inspector:
            args+= ['-skiptranslator Inspector']

        if not self.conanfile.options.metadata:
            args+= ['-skiptranslator MetaData']

        if not self.conanfile.options.transient:
            args+= ['-skiptranslator Transient']

        if not self.conanfile.options.sqlite:
            args+= ['-skiptranslator Sqlite']

        if not self.conanfile.options.idm:
            args+= ['-skiptranslator InterDomainMessaging']

            
        args += self.extras
        args += extras

        cmd = f'masl-codegen {" ".join(args)}'
        return self.conanfile.run(cmd)


class MaslConanHelper():
    exports_sources = 'src/*', 'masl/*', 'schedule/*', 'config/*'
    generators = 'CMakeDeps', 'CMakeToolchain', 'VirtualBuildEnv', 'VirtualRunEnv'
    settings = 'os', 'compiler', 'build_type', 'arch'

    options = {
        "inspector" : [True,False],
        "metadata" : [True, False],
        "transient" : [True,False],
        "sqlite" : [True,False],
        "idm" : [True, False],
        "amqp" : [True, False],
        "test" : [True, False]
    }

    default_options = {
        "inspector" : True,
        "metadata" : True,
        "transient" : True,
        "sqlite" : True,
        "idm" : True,
        "amqp" : False,
        "test" : False
        
    }

    def layout(self):
        cmake_layout(self)
        self.cpp.package.resdirs = ['res']


    def omit_requirements(self):
        """
        Sometimes the generated code doesn't need all the default dependencies,
        but conan whinges about any unused ones.
        
        Override this method so supply a list to omit.
        """
        return []

    def requirements(self):
        omit = self.omit_requirements()
        if 'xtuml_masl_codegen' not in ( r.ref.name for r in self.requires.values()):
            self.tool_requires(f'xtuml_masl_codegen/[>=1.0 <2]@xtuml',visible=True)

        for pkg, ver, inc in (
            ('xtuml_swa', '[>=1.0 <2]', True ),
            ('xtuml_asn1',  '[>=1.0 <2]', True ),
            ('xtuml_inspector_server',  '[>=1.0 <2]', self.options.inspector ),
            ('xtuml_metadata', '[>=1.0 <2]', self.options.metadata ),
            ('xtuml_transient', '[>=1.0 <2]', self.options.transient ),
            ('xtuml_sql', '[>=1.0 <2]', self.options.sqlite ),
            ('xtuml_sqlite', '[>=1.0 <2]', self.options.sqlite ),
            ('xtuml_idm', '[>=1.0 <2]', self.options.idm ),
            ('xtuml_amqp_client', '[>=1.0 <2]', self.options.amqp ),
        ):
            if inc and  pkg not in omit and pkg not in ( r.ref.name for r in self.requires.values()):
                self.requires(f'{pkg}/{ver}@xtuml', transitive_libs=True, transitive_headers=True)
        for pkg, ver, inc in (
            ('nlohmann_json', '[>=3.11 <4]', True ),
            ('boost',  '[>=1.86.0 <2]', True ),
        ):
            if inc and pkg not in omit and pkg not in ( r.ref.name for r in self.requires.values()):
                self.requires(f'{pkg}/{ver}', transitive_libs=True, transitive_headers=True)
            

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

    def generate(self):
        env = Environment()
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.name={self.name}");
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.version={self.version}");
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.channel={self.channel}");
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.user={self.user}");

        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{self.name}.version={self.version}");
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{self.name}.channel={self.channel}");
        env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{self.name}.user={self.user}");
        for dep in self.dependencies.values():
            env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{dep.ref.name}.version={dep.ref.version}")
            env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{dep.ref.name}.channel={dep.ref.channel}")
            env.append("MASL_CODEGEN_OPTS", f"-Dpackage.{dep.ref.name}.user={dep.ref.user}")


        envvars = env.vars(self)
        envvars.save_script("package_vars")
        
        
    def build(self):
        extras = self.masl_extras()
        if self.options.test:
            extras.append('-test')

        subdirs = [ d for d in MaslGen(self).multi_generate(self.masl_src(), extras=extras, output='generated')
                   if os.path.isfile(os.path.join(self.build_folder, 'generated', d, 'CMakeLists.txt'))]

        if subdirs:
            save(self, 'generated/CMakeLists.txt', textwrap.dedent(f"""
                cmake_minimum_required(VERSION 3.9)
                project({self.name})

                enable_testing()
                """) 
                 + '\n'.join(f'add_subdirectory({d})' for d in subdirs) + '\n')

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
        for info in Path(self.build_folder).glob('generated/**/*.conan_info'):
            info_rel = info.relative_to(self.build_folder)
            copy(self, str(info_rel), src=self.build_folder, dst=self.package_folder,keep_path=False)


    def package_info(self):
        domains = {}

        for infopath in Path(self.package_folder).glob('*.conan_info'):
            with open(os.path.join(self.package_folder,infopath)) as infofile:
                info = json.load(infofile)
                curpkg = info.get("pkg")
                name = info.get("domain",info.get("project"))
                domains[name] = curpkg
                def depname(dep):
                    if dep.get("pkg") == curpkg:
                        return dep.get("name")
                    else:
                        return dep.get("pkg") + "::" + dep.get("name")
                for tgt in info.get("targets"):
                    name=tgt.get("name")
                    self.cpp_info.components[name].libs = tgt.get("libs")
                    self.cpp_info.components[name].requires += [ depname(dep) for dep in tgt.get("requires") ]
                
        self.cpp_info.set_property("masl_domains",domains)        
        for inc in self.cpp_info.includedirs:
            self.buildenv_info.append('MASL_DOMAINPATH', os.path.join(self.package_folder, inc, 'masl'), )
        for res in self.cpp_info.resdirs:
            self.runenv_info.append('MASL_SRCPATH', os.path.join(self.package_folder, res, 'masl-src'), )
        for d in self.cpp_info.bindirs:
            self.runenv_info.append_path("PATH",os.path.join(self.package_folder,d))
        for d in self.cpp_info.libdirs:
            self.runenv_info.append_path("LD_LIBRARY_PATH",os.path.join(self.package_folder,d))
            self.runenv_info.append_path("DYLD_LIBRARY_PATH",os.path.join(self.package_folder,d))
