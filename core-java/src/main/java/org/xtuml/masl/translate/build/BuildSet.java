//
// File: BuildSet.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.TextFile;
import org.xtuml.masl.javagen.JavaFile;
import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;

import com.google.common.collect.ImmutableSet;

public class BuildSet
{

  static private final Map<Domain, BuildSet> domainBuildSets              = new HashMap<>();
  static private final Map<Project, BuildSet> projectBuildSets             = new HashMap<>();

  private final static int                   MAX_CONCURRENT_THREADS = 50;
  private final static boolean               FLUSH_ALL_THREADS      = true;
  private final static boolean               CONDITIONAL_FLUSH      = false;


  public static BuildSet getBuildSet ( final Domain domain )
  {

    domainBuildSets.putIfAbsent(domain, new BuildSet(null));

    return domainBuildSets.get(domain);
  }

  public static BuildSet getBuildSet ( final Project project )
  {
    projectBuildSets.putIfAbsent(project, new BuildSet(null));

    return projectBuildSets.get(project);
  }

  public static void addBuildSet ( final Project project, final BuildSet buildSet )
  {
    projectBuildSets.put(project,buildSet);
  }

  public static void addBuildSet ( final Domain domain, final BuildSet buildSet )
  {
    domainBuildSets.put(domain,buildSet);
  }

  static private void waitForThreads ( final List<Thread> activeThreads, final boolean flushThreads )
  {
    if ( flushThreads == true || activeThreads.size() >= MAX_CONCURRENT_THREADS )
    {
      for ( final Thread thread : activeThreads )
      {
        try
        {
          thread.join();
        }
        catch ( final InterruptedException e )
        {
          e.printStackTrace();
        }
      }
      activeThreads.clear();
    }
  }

  public BuildSet ( final String name )
  {
    this.name = name;
  }

  public TextFile createTextFile ( final String name )
  {
    final TextFile textFile = new TextFile(null,name);
    writeableFiles.add(textFile);
    return textFile;
  }

  public TextFile createTextFile ( final String name, final FileGroup fileList )
  {
    final TextFile result = createTextFile(name);
    fileList.addFile(result);
    return result;
  }

  public XMLFile createXMLFile ( final String name )
  {
    final XMLFile xmlFile = new XMLFile(null,new File(name));
    writeableFiles.add(xmlFile);
    return xmlFile;
  }

  public XMLFile createXMLFile ( final String name, final FileGroup fileList )
  {
    final XMLFile result = createXMLFile(name);
    fileList.addFile(result);
    return result;
  }


  public void dump ( final File directory )
  {
    System.out.println("Dumping Files");
    final long millis = System.currentTimeMillis();

    directory.mkdirs();
    final List<Thread> activeThreads = new ArrayList<Thread>();
    for ( final WriteableFile writeableFile : writeableFiles )
    {
      final Thread writerThread = new Thread(new Runnable()
      {
        @Override
        public void run ()
        {
          try
          {
            final File file = new File(directory, writeableFile.getFile().getPath());
            final Writer newCode = new StringWriter();
            writeableFile.writeCode(newCode);
            updateFile(file, newCode);
          }
          catch ( final IOException e )
          {
            e.printStackTrace();
          }
        }

      }, writeableFile.getFile().getName());

      writerThread.start();
      if ( serialiseThread(writerThread) == false )
      {
        activeThreads.add(writerThread);
        waitForThreads(activeThreads, CONDITIONAL_FLUSH);
      }
    }

    waitForThreads(activeThreads, FLUSH_ALL_THREADS);

    System.out.println("Dumped (" + (System.currentTimeMillis() - millis) / 1000.0 + "secs)");
  }

  public JavaFile getJavaFile ( final CompilationUnit cu, final FileGroup jarFile )
  {
    final JavaFile result = new JavaFile(cu, jarFile);
    jarFile.addFile(result);
    writeableFiles.add(result);
    return result;

  }

  public FileGroup getJarFile ( final String name, final boolean installed )
  {
    final FileGroup jar = FileGroup.getFileGroup(name);
    if ( installed )
    {
      // Force public
      jars.add(jar);
      localJars.remove(jar);
    }
    else
    {
      // Don't make local if already public
      if ( !jars.contains(jar) )
      {
        localJars.add(jar);
      }
    }
    return jar;
  }


  private final List<FileGroup> fileGroups = new ArrayList<>();

  public List<FileGroup> getFileGroups()
  {
    return fileGroups ;
  }


  public void addFileGroup ( final FileGroup group )
  {
    fileGroups.add(group);
  }

  public Set<String> getIncludes ()
  {
    return includes;
  }

  public void addInclude ( final String include )
  {
    includes.add(include);
  }

  public void addIncludeDir ( final File include )
  {
    rawIncludes.add(include);
  }

  public void addSourceDir ( final File sourceDir )
  {
    sourceDirs.add(sourceDir);
  }


  public Set<File> getRawIncludes ()
  {
    return rawIncludes;
  }

  public Set<File> getSourceDirs ()
  {
    return sourceDirs;
  }

  public void removeLibrary ( final FileGroup lib )
  {
    libraries.remove(lib.getName());
  }

  public void removeArchive ( final FileGroup lib )
  {
    archives.remove(lib.getName());
  }

  public void skipExecutable ( final String name )
  {
    skipExecutables.add(name);
  }

  public void skipLibrary ( final String name )
  {
    skipLibraries.add(name);
  }

  public void skipArchive ( final String name )
  {
    skipArchives.add(name);
  }

  final Set<CompilationUnit>                javaFiles         = new LinkedHashSet<CompilationUnit>();
  final Set<FileGroup>                      jars              = new LinkedHashSet<FileGroup>();
  final Set<FileGroup>                      localJars         = new LinkedHashSet<FileGroup>();

  final Map<String,FileGroup>               executables       = new LinkedHashMap<>();
  final Map<String,FileGroup>               archives          = new LinkedHashMap<>();
  final Map<String,FileGroup>               libraries         = new LinkedHashMap<>();
  final Map<String,FileGroup>               headerSets           = new LinkedHashMap<>();

  final Set<String>                         includes          = new LinkedHashSet<String>();

  final Set<String>                         skipExecutables   = new LinkedHashSet<>();
  final Set<String>                         skipLibraries     = new LinkedHashSet<>();
  final Set<String>                         skipArchives      = new LinkedHashSet<>();
  final Set<String>                         skipHeaderSets      = new LinkedHashSet<>();

  final Set<File>                           rawIncludes       = new LinkedHashSet<File>();

  final Set<File>                           sourceDirs        = new LinkedHashSet<File>();

  private final String                      name;

  private final Set<WriteableFile>          writeableFiles    = new HashSet<WriteableFile>();

  final Set<String>                         publishedExecutables     = new LinkedHashSet<String>();
  final Set<String>                         publishedArchives        = new LinkedHashSet<String>();
  final Set<String>                         publishedLibraries       = new LinkedHashSet<String>();
  final Set<String>                         publishedHeaderSets      = new LinkedHashSet<String>();

  private final Set<SubdirFileGroup>        publishedEtc      = new LinkedHashSet<SubdirFileGroup>();
  private final Set<SubdirFileGroup>        publishedShare    = new LinkedHashSet<SubdirFileGroup>();
  private final Set<SubdirFileGroup>        publishedDoc      = new LinkedHashSet<SubdirFileGroup>();
  private final Set<SubdirFileGroup>        publishedInclude  = new LinkedHashSet<SubdirFileGroup>();
  private final Set<SubdirFileGroup>        publishedTopLevel = new LinkedHashSet<SubdirFileGroup>();
  private final Set<FileGroup>              publishedBin      = new LinkedHashSet<FileGroup>();
  private final Set<FileGroup>              publishedLib      = new LinkedHashSet<FileGroup>();

  private static final String               copyrightNotice   = CommandLine.INSTANCE.getRawCopyrightNotice();

  private static Pattern                    copyrightPattern  = null;
  static private java.text.SimpleDateFormat yearFormatter     = new java.text.SimpleDateFormat("yyyy");

  private final Set<File> fileDependents = new LinkedHashSet<>();


  static public void updateFile ( final File file, final Writer newCode ) throws IOException
  {
    if ( file.canRead() )
    {
      final BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                                                                                 Charset.forName("ISO-8859-1")));
      StringBuilder oldCode = new StringBuilder();

      while ( fileReader.ready() )
      {
        oldCode.append(fileReader.readLine() + "\n");
      }
      fileReader.close();

      // Don't replace the file if it's just the copyright date that has
      // changed. Set the year in the old file to the current year before
      // comparing the two.
      String[] noticeChunks = ( copyrightNotice == null ? new String[0] : copyrightNotice.split( "yyyy" ) );
      if ( 2 == noticeChunks.length ) { // only account for one instance of the date in the copyright notice
        copyrightPattern = Pattern.compile( "(?<=" + Pattern.quote(noticeChunks[0]) + ")[\\d]{4}(?=" + Pattern.quote(noticeChunks[1]) + ")" );
        final Matcher copyrightMatcher = copyrightPattern.matcher(oldCode);
        if ( copyrightMatcher.find() )
        {
          oldCode = new StringBuilder(copyrightMatcher.replaceFirst(yearFormatter.format(new Date())));
        }
      }

      final boolean fileChanged = !newCode.toString().equals(oldCode.toString());
      final boolean forcedBuild = CommandLine.INSTANCE.getForceBuild();

      if ( fileChanged || forcedBuild )
      {
        if ( fileChanged )
        {
          System.out.println("Updating " + file.getPath());
          file.renameTo(new File(file.getPath() + ".old"));
        }
        else if ( forcedBuild )
        {
          System.out.println("Unchanged " + file.getPath());
        }

        final Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                                            Charset.forName("ISO-8859-1")));
        fileWriter.write(newCode.toString());
        fileWriter.flush();
        fileWriter.close();
      }
    }
    else
    {
      System.out.println("Creating " + file.getPath());
      file.getParentFile().mkdirs();
      final Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("ISO-8859-1")));
      fileWriter.write(newCode.toString());
      fileWriter.flush();
      fileWriter.close();
    }
  }

  static private boolean serialiseThread ( final Thread currentThread )
  {
    // When debugging the application, the number of threads produced can cause
    // the debugger to grind to a halt due to the number of concurrent threads
    // being executed. Therefore test a flag and run in sequence if required.
    final boolean serialiseThread = CommandLine.INSTANCE.getSerialFileWrites();
    if ( serialiseThread == true )
    {
      try
      {
        currentThread.join();
      }
      catch ( final InterruptedException ie )
      {
        ie.printStackTrace();
      }
    }
    return serialiseThread;
  }

  public String getName ()
  {
    return name;
  }

  public Set<FileGroup> getLocalJars ()
  {
    return localJars;
  }

  public Set<FileGroup> getJars ()
  {
    return jars;
  }

  public static class SubdirFileGroup
  {


    public SubdirFileGroup ( final String subdir, final FileGroup fileGroup )
    {
      this.subdir = subdir;
      this.fileGroup = fileGroup;
    }

    public String getSubdir ()
    {
      return subdir;
    }

    public FileGroup getFileGroup ()
    {
      return fileGroup;
    }

    private final String    subdir;
    private final FileGroup fileGroup;
  }

  public void addPublishedEtc ( final String subdir, final FileGroup group )
  {
    publishedEtc.add(new SubdirFileGroup(subdir, group));
  }

  public Set<SubdirFileGroup> getPublishedEtc ()
  {
    return publishedEtc;
  }

  public void addPublishedShare ( final String subdir, final FileGroup group )
  {
    publishedShare.add(new SubdirFileGroup(subdir, group));
  }

  public Set<SubdirFileGroup> getPublishedShare ()
  {
    return publishedShare;
  }

  public void addPublishedDoc ( final String subdir, final FileGroup group )
  {
    publishedDoc.add(new SubdirFileGroup(subdir, group));
  }

  public Set<SubdirFileGroup> getPublishedDoc ()
  {
    return publishedDoc;
  }

  public void addPublishedInclude ( final String subdir, final FileGroup group )
  {
    publishedInclude.add(new SubdirFileGroup(subdir, group));
  }

  public Set<SubdirFileGroup> getPublishedInclude ()
  {
    return publishedInclude;
  }

  public void addPublishedTopLevel ( final String subdir, final FileGroup group )
  {
    publishedTopLevel.add(new SubdirFileGroup(subdir, group));
  }

  public Set<SubdirFileGroup> getPublishedTopLevel ()
  {
    return publishedTopLevel;
  }

  public void addPublishedBin ( final FileGroup group )
  {
    publishedBin.add(group);
  }

  public Set<FileGroup> getPublishedBin ()
  {
    return publishedBin;
  }

  public void addPublishedLib ( final FileGroup group )
  {
    publishedLib.add(group);
  }

  public Set<FileGroup> getPublishedLib ()
  {
    return publishedLib;
  }

  public void addFileDependent ( final File file )
  {
    fileDependents.add(file);
  }

  public Set<File> getFileDependents()
  {
    return ImmutableSet.copyOf(fileDependents);
  }

}
