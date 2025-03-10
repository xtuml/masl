/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.ArchiveLibrary;
import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.BuildTranslator;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.BuildSet;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.building.WriteableFile;
import org.xtuml.masl.translate.cmake.language.TemplateSubtitutionPlaceholder;
import org.xtuml.masl.translate.cmake.language.arguments.CompoundArgument;
import org.xtuml.masl.translate.cmake.language.arguments.QuotedArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.commands.Command;
import org.xtuml.masl.translate.cmake.language.commands.SetVariable;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Alias("cmake")
@Default
public class Translator extends BuildTranslator {

    public Translator() {
    }

    @Override
    public void translate(final File sourceDirectory) {
        final Section projectSetup = new Section();
        final Section exports = new Section("Export", '-');

        final Section archives = new Section("Archives", '-');
        final Section libraries = new Section("Libraries", '-');
        final Section executables = new Section("Executables", '-');
        final Section dependencies = new Section();

        cmakelists.add(projectSetup);
        cmakelists.add(archives);
        cmakelists.add(libraries);
        cmakelists.add(executables);
        cmakelists.add(exports);

        projectSetup.add(new Command("cmake_minimum_required", new CompoundArgument("VERSION", "3.30")));
        final String name = getBuildSet().getName();
        projectSetup.add(new Command("project", name));

        projectSetup.add(new SetVariable("PROJECT_VERSION", "${CONAN_PACKAGE_VERSION}"));
        projectSetup.add(new SetVariable(name + "_VERSION", "${PROJECT_VERSION}"));

        // Turn off compiler warnings... this is generated code, we know what we're
        // doing. Honest.
        projectSetup.add(new Command("add_compile_options", new QuotedArgument("-w")));

        // Newer compilers are picky about template function specialization order -
        // allow the old style until we can fix it properly
        projectSetup.add(new Command("add_compile_options", new QuotedArgument("-fpermissive")));

        projectSetup.add(dependencies);

        final Set<BuildSet> buildSets = new LinkedHashSet<>();

        for (final ArchiveLibrary lib : Iterables.filter(getBuildSet().getFileGroups(), ArchiveLibrary.class)) {
            archives.add(new BuildArchiveLibrary(lib, srcPath));
            buildSets.addAll(lib.getDependencies().stream().map(fg -> fg.getParent()).collect(Collectors.toSet()));
        }

        for (final SharedLibrary lib : Iterables.filter(getBuildSet().getFileGroups(), SharedLibrary.class)) {
            libraries.add(new BuildSharedLibrary(lib, srcPath));
            buildSets.addAll(lib.getDependencies().stream().map(fg -> fg.getParent()).collect(Collectors.toSet()));
        }

        for (final InterfaceLibrary lib : Iterables.filter(getBuildSet().getFileGroups(), InterfaceLibrary.class)) {
            libraries.add(new BuildInterfaceLibrary(lib, srcPath));
            buildSets.addAll(lib.getDependencies().stream().map(fg -> fg.getParent()).collect(Collectors.toSet()));
        }

        for (final Executable exe : Iterables.filter(getBuildSet().getFileGroups(), Executable.class)) {
            executables.add(new BuildExecutable(exe, srcPath));
            buildSets.addAll(exe.getDependencies().stream().map(fg -> fg.getParent()).collect(Collectors.toSet()));
        }

        buildSets.stream().filter(bs -> bs != null &&
                                        bs != getBuildSet() &&
                                        bs.getName() != null).map(BuildSet::getName).distinct().forEach(pkg -> {
            if ( !pkg.equals(getBuildSet().getName()) ) {
                dependencies.add(new Command("if", new CompoundArgument("NOT", pkg + "_FOUND")));
                dependencies.add(new Command("find_package", new CompoundArgument(pkg, "REQUIRED")));
                dependencies.add(new Command("endif"));
            }
        });

        final File customInclude = new File(sourceDirectory, "custom/custom.cmake");

        cmakelists.add(new Command("include",
                                   Lists.newArrayList(Utils.getPathArg(customInclude.getAbsoluteFile()),
                                                      new SingleArgument("OPTIONAL"))));

        String customBuildFile = CommandLine.INSTANCE.getCustomBuildFile();
        if (null != customBuildFile) {
            final File customIncludeGenFolder = new File(customBuildFile);
            cmakelists.add(new Command("include",
                                       Lists.newArrayList(Utils.getPathArg(customIncludeGenFolder.getAbsoluteFile()),
                                                          new SingleArgument("OPTIONAL"))));
        }

        final FileGroup buildFiles = FileGroup.getFileGroup("cmakeBuildFiles");
        buildFiles.addFile(cmakelists);

        getBuildSet().addFileGroup(buildFiles);

    }

    @Override
    public void translateBuild(final org.xtuml.masl.translate.Translator<?> parent, final File sourceDirectory) {
        final Class<?> parentClass = parent.getClass();
        try {
            final Class<?>
                    buildTransClass =
                    Class.forName(parentClass.getPackage().getName() + ".cmake." + parentClass.getSimpleName());

            final long millis = System.currentTimeMillis();

            buildTransClass.getConstructor(parentClass, CMakeLists.class).newInstance(parent, cmakelists);

        } catch (final ClassNotFoundException e) {
            // ignore
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dump(final File directory) {
        try {
            final File srcDir = new File(directory, srcPath.getPath());
            final File includeDir = new File(directory, includePath.getPath());

            final Set<WriteableFile>
                    files =
                    getBuildSet().getFileGroups().stream().flatMap(g -> g.getFiles().stream()).filter(f -> f instanceof WriteableFile).map(
                            f -> (WriteableFile) f).collect(Collectors.toSet());

            for (final WriteableFile codeFile : files) {
                final Writer newFileCode = new StringWriter();
                codeFile.writeCode(newFileCode);
                final File
                        dumpDir =
                        codeFile.isPublicHeader() ? includeDir : (codeFile.isSourceFile() ? srcDir : directory);
                final File outFile = new File(dumpDir, codeFile.getFile().getPath());
                BuildSet.updateFile(outFile, newFileCode);

            }

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private final CMakeLists cmakelists = new CMakeLists();

    private final File srcPath = new File("src");
    private final File includePath = new File("include");

}
