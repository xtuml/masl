//
// File: Translator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.cmake;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Collectors;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.ArchiveLibrary;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.cppgen.TextFile;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.BuildTranslator;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.BuildSet;
import org.xtuml.masl.translate.build.WriteableFile;
import org.xtuml.masl.translate.cmake.language.arguments.QuotedArgument;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;
import org.xtuml.masl.translate.cmake.language.commands.Command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


@Alias("cmake")
@Default
public class Translator
    extends BuildTranslator
{

  public Translator ()
  {
  }

  @Override
  public void translate ( final BuildSet buildSet, final File sourceDirectory )
  {
    this.buildSet = buildSet;

    for ( final File dependency : buildSet.getFileDependents() )
    {
      try
      {
        depstxt.getBuffer().append(dependency.getCanonicalPath() + "\n");
      }
      catch ( IOException e )
      {
        depstxt.getBuffer().append(dependency.getAbsolutePath() + "\n");
      }
    }

    final Section archives = new Section("Archives", '-');
    final Section libraries = new Section("Libraries", '-');
    final Section executables = new Section("Executables", '-');

    // Turn off compiler warnings... this is generated code, we know what we're doing. Honest.
    cmakelists.add(new Command("add_compile_options",new QuotedArgument("-w")));

    // Newer compilers are picky about template function specialization order - allow the old style until we can fix it properly
    cmakelists.add(new Command("add_compile_options",new QuotedArgument("-fpermissive")));
    cmakelists.add(new Command("set",Lists.newArrayList(new SingleArgument("CMAKE_SHARED_LINKER_FLAGS"), new QuotedArgument("-Wl,--no-as-needed"))));
    cmakelists.add(new Command("set",Lists.newArrayList(new SingleArgument("CMAKE_EXE_LINKER_FLAGS"), new QuotedArgument("-Wl,--no-as-needed"))));
    // Assume we need boost... 
    cmakelists.add(new Command("find_package",Lists.newArrayList(new SingleArgument("Boost"),new SingleArgument("REQUIRED"),new SingleArgument("QUIET")
        // workaround for cmake Issue #0016057 need to specify at least one component for Boost::boost target to be set up - should be fixed in cmake 3.6
        ,new SingleArgument("COMPONENTS"), new SingleArgument("system")
      )));

    cmakelists.add(archives);
    cmakelists.add(libraries);
    cmakelists.add(executables);

    for ( final ArchiveLibrary lib : Iterables.filter(buildSet.getFileGroups(), ArchiveLibrary.class) )
    {
      archives.add(new BuildArchiveLibrary(lib, srcPath));
    }

    for ( final SharedLibrary lib : Iterables.filter(buildSet.getFileGroups(), SharedLibrary.class) )
    {
      libraries.add(new BuildSharedLibrary(lib, srcPath));
    }

    for ( final InterfaceLibrary lib : Iterables.filter(buildSet.getFileGroups(), InterfaceLibrary.class) )
    {
      libraries.add(new BuildInterfaceLibrary(lib, srcPath));
    }


    for ( final Executable exe : Iterables.filter(buildSet.getFileGroups(), Executable.class) )
    {
      executables.add(new BuildExecutable(exe, srcPath));
    }

    final File customInclude = new File(sourceDirectory,"custom/custom.cmake");

    cmakelists.add(new Command("include",Lists.newArrayList(Utils.getPathArg(customInclude.getAbsoluteFile()),new SingleArgument("OPTIONAL"))));

    String customBuildFile = CommandLine.INSTANCE.getCustomBuildFile();
    if ( null != customBuildFile ) {
      final File customIncludeGenFolder = new File( customBuildFile );
      cmakelists.add(new Command("include",Lists.newArrayList(Utils.getPathArg(customIncludeGenFolder.getAbsoluteFile()),new SingleArgument("OPTIONAL"))));
    }

  }

  @Override
  public void translateBuild ( final org.xtuml.masl.translate.Translator<?> parent, final File sourceDirectory )
  {
    final Class<?> parentClass = parent.getClass();
    try
    {
      final Class<?> buildTransClass = Class.forName(parentClass.getPackage().getName() + ".cmake." + parentClass.getSimpleName());

      System.out.println("Translating cmake " + parent.getName() + ".");
      final long millis = System.currentTimeMillis();

      buildTransClass.getConstructor(parentClass, CMakeLists.class).newInstance(parent, cmakelists);

      System.out.println("Translated  cmake " + parent.getName() + " (" + (System.currentTimeMillis() - millis) / 1000.0 + "secs)");

    }
    catch ( final ClassNotFoundException e )
    {
      // System.out.println("Warning : imake translator not found for " +
      // parentClass);
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }

  }

  @Override
  public void dump ( final File directory )
  {
    try
    {
      cmakelists.writeToFile(directory);
      depstxt.writeToFile(directory);

      final File srcDir = new File(directory, srcPath.getPath());
      final File includeDir = new File(directory, includePath.getPath());

      final Set<WriteableFile> files =
      buildSet.getFileGroups()
              .stream()
              .flatMap(g -> g.getFiles().stream())
              .filter(f -> f instanceof WriteableFile)
              .map(f -> (WriteableFile)f).collect(Collectors.toSet());

      for ( final WriteableFile codeFile : files )
      {
        final Writer newFileCode = new StringWriter();
        codeFile.writeCode(newFileCode);
        final File outFile = new File((codeFile instanceof CodeFile && ((CodeFile)codeFile).isPublicHeader()) ? includeDir : srcDir, codeFile.getFile().getPath());
        BuildSet.updateFile(outFile, newFileCode);

      }

    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
  }

  private final CMakeLists cmakelists = new CMakeLists();
  private final TextFile   depstxt = new TextFile(null,"dependencies.txt");

  private BuildSet         buildSet;
  private final File       srcPath     = new File("src");
  private final File       includePath = new File("include");

}
