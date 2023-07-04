/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.building;

import com.google.common.collect.ImmutableSet;
import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.TextFile;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildSet {

    static private final Map<Domain, BuildSet> domainBuildSets = new HashMap<>();
    static private final Map<Project, BuildSet> projectBuildSets = new HashMap<>();

    private final static int MAX_CONCURRENT_THREADS = 50;
    private final static boolean FLUSH_ALL_THREADS = true;
    private final static boolean CONDITIONAL_FLUSH = false;

    public static BuildSet getBuildSet(final Domain domain) {

        domainBuildSets.putIfAbsent(domain, new BuildSet(null));

        return domainBuildSets.get(domain);
    }

    public static BuildSet getBuildSet(final Project project) {
        projectBuildSets.putIfAbsent(project, new BuildSet(null));

        return projectBuildSets.get(project);
    }

    public static void addBuildSet(final Project project, final BuildSet buildSet) {
        projectBuildSets.put(project, buildSet);
    }

    public static void addBuildSet(final Domain domain, final BuildSet buildSet) {
        domainBuildSets.put(domain, buildSet);
    }

    static private void waitForThreads(final List<Thread> activeThreads, final boolean flushThreads) {
        if (flushThreads || activeThreads.size() >= MAX_CONCURRENT_THREADS) {
            for (final Thread thread : activeThreads) {
                try {
                    thread.join();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            activeThreads.clear();
        }
    }

    public BuildSet(final String name, String pkg) {
        this.name = name;
        this.pkg = pkg;
    }

    public BuildSet(final String name) {
        this(name, name);
    }

    public TextFile createTextFile(final String name) {
        final TextFile textFile = new TextFile(null, name);
        writeableFiles.add(textFile);
        return textFile;
    }

    public TextFile createTextFile(final String name, final FileGroup fileList) {
        final TextFile result = createTextFile(name);
        fileList.addFile(result);
        return result;
    }

    public XMLFile createXMLFile(final String name) {
        final XMLFile xmlFile = new XMLFile(null, new File(name));
        writeableFiles.add(xmlFile);
        return xmlFile;
    }

    public XMLFile createXMLFile(final String name, final FileGroup fileList) {
        final XMLFile result = createXMLFile(name);
        fileList.addFile(result);
        return result;
    }

    private final List<FileGroup> fileGroups = new ArrayList<>();

    public List<FileGroup> getFileGroups() {
        return fileGroups;
    }

    public void addFileGroup(final FileGroup group) {
        fileGroups.add(group);
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public void addInclude(final String include) {
        includes.add(include);
    }

    public void addIncludeDir(final File include) {
        rawIncludes.add(include);
    }

    public void addSourceDir(final File sourceDir) {
        sourceDirs.add(sourceDir);
    }

    public Set<File> getRawIncludes() {
        return rawIncludes;
    }

    public Set<File> getSourceDirs() {
        return sourceDirs;
    }

    public void skipExecutable(final String name) {
        skipExecutables.add(name);
    }

    public void skipLibrary(final String name) {
        skipLibraries.add(name);
    }

    public void skipArchive(final String name) {
        skipArchives.add(name);
    }

    private final Set<String> includes = new LinkedHashSet<>();

    private final Set<String> skipExecutables = new LinkedHashSet<>();
    private final Set<String> skipLibraries = new LinkedHashSet<>();
    private final Set<String> skipArchives = new LinkedHashSet<>();

    private final Set<File> rawIncludes = new LinkedHashSet<>();

    private final Set<File> sourceDirs = new LinkedHashSet<>();

    private final String name;
    private final String pkg;

    private final Set<WriteableFile> writeableFiles = new HashSet<>();

    private final Set<SubdirFileGroup> publishedEtc = new LinkedHashSet<>();
    private final Set<SubdirFileGroup> publishedShare = new LinkedHashSet<>();
    private final Set<SubdirFileGroup> publishedDoc = new LinkedHashSet<>();
    private final Set<SubdirFileGroup> publishedInclude = new LinkedHashSet<>();
    private final Set<SubdirFileGroup> publishedTopLevel = new LinkedHashSet<>();
    private final Set<FileGroup> publishedBin = new LinkedHashSet<>();
    private final Set<FileGroup> publishedLib = new LinkedHashSet<>();

    private final Set<File> fileDependents = new LinkedHashSet<>();

    static public void updateFile(final File file, final Writer newCode) throws IOException {
        if (file.canRead()) {
            final BufferedReader
                    fileReader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
            StringBuilder oldCode = new StringBuilder();

            while (fileReader.ready()) {
                oldCode.append(fileReader.readLine() + "\n");
            }
            fileReader.close();

            final boolean fileChanged = !newCode.toString().contentEquals(oldCode);
            final boolean forcedBuild = CommandLine.INSTANCE.getForceBuild();

            if (fileChanged || forcedBuild) {
                if (fileChanged) {
                    file.renameTo(new File(file.getPath() + ".old"));
                } else if (forcedBuild) {
                }

                final Writer
                        fileWriter =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                                  StandardCharsets.ISO_8859_1));
                fileWriter.write(newCode.toString());
                fileWriter.flush();
                fileWriter.close();
            }
        } else {
            file.getParentFile().mkdirs();
            final Writer
                    fileWriter =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1));
            fileWriter.write(newCode.toString());
            fileWriter.flush();
            fileWriter.close();
        }
    }

    static private boolean serialiseThread(final Thread currentThread) {
        // When debugging the application, the number of threads produced can cause
        // the debugger to grind to a halt due to the number of concurrent threads
        // being executed. Therefore test a flag and run in sequence if required.
        final boolean serialiseThread = CommandLine.INSTANCE.getSerialFileWrites();
        if (serialiseThread) {
            try {
                currentThread.join();
            } catch (final InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        return serialiseThread;
    }

    public String getName() {
        return name;
    }

    public String getPackage() {
        return pkg;
    }

    public static class SubdirFileGroup {

        public SubdirFileGroup(final String subdir, final FileGroup fileGroup) {
            this.subdir = subdir;
            this.fileGroup = fileGroup;
        }

        public String getSubdir() {
            return subdir;
        }

        public FileGroup getFileGroup() {
            return fileGroup;
        }

        private final String subdir;
        private final FileGroup fileGroup;
    }

    public void addPublishedEtc(final String subdir, final FileGroup group) {
        publishedEtc.add(new SubdirFileGroup(subdir, group));
    }

    public Set<SubdirFileGroup> getPublishedEtc() {
        return publishedEtc;
    }

    public void addPublishedShare(final String subdir, final FileGroup group) {
        publishedShare.add(new SubdirFileGroup(subdir, group));
    }

    public Set<SubdirFileGroup> getPublishedShare() {
        return publishedShare;
    }

    public void addPublishedDoc(final String subdir, final FileGroup group) {
        publishedDoc.add(new SubdirFileGroup(subdir, group));
    }

    public Set<SubdirFileGroup> getPublishedDoc() {
        return publishedDoc;
    }

    public void addPublishedInclude(final String subdir, final FileGroup group) {
        publishedInclude.add(new SubdirFileGroup(subdir, group));
    }

    public Set<SubdirFileGroup> getPublishedInclude() {
        return publishedInclude;
    }

    public void addPublishedTopLevel(final String subdir, final FileGroup group) {
        publishedTopLevel.add(new SubdirFileGroup(subdir, group));
    }

    public Set<SubdirFileGroup> getPublishedTopLevel() {
        return publishedTopLevel;
    }

    public void addPublishedBin(final FileGroup group) {
        publishedBin.add(group);
    }

    public Set<FileGroup> getPublishedBin() {
        return publishedBin;
    }

    public void addPublishedLib(final FileGroup group) {
        publishedLib.add(group);
    }

    public Set<FileGroup> getPublishedLib() {
        return publishedLib;
    }

}
