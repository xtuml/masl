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

import java.io.File;
import java.util.*;

public class FileGroup {

    private static final Map<String, FileGroup> lookup = new HashMap<>();

    public static FileGroup getFileGroup(final String name) {
        FileGroup result = lookup.get(name);
        if (result == null) {
            result = new FileGroup(name);
            lookup.put(name, result);
        }
        return result;
    }

    protected FileGroup(final String name) {
        this.name = name;
    }

    public <G extends FileGroup> G addDependency(final G dependent) {
        dependencies.add(dependent);
        return dependent;
    }

    public void removeDependency(final FileGroup dependent) {
        dependencies.remove(dependent);
    }

    public <F extends ReferencedFile> F addFile(final F file) {
        files.put(file.getFile(), file);
        return file;
    }

    public void addLibPath(final String name) {
        libPaths.add(name);
    }

    public void skipDependency(final String name) {
        skipDeps.add(name);
    }

    public void skipFile(final File file) {
        skipFiles.add(file);
    }

    public Set<FileGroup> getDependencies() {
        final Set<FileGroup> result = new LinkedHashSet<>(dependencies);
        for (final FileGroup group : includedGroups) {
            result.addAll(group.getDependencies());
        }
        for (final ReferencedFile file : files.values()) {
            result.addAll(file.getDependencies());
        }
        result.removeAll(skipDeps);
        result.remove(this);
        return Collections.unmodifiableSet(result);
    }

    public Set<ReferencedFile> getFiles() {
        final Set<ReferencedFile> allFiles = new LinkedHashSet<>(files.values());
        for (final FileGroup group : includedGroups) {
            allFiles.addAll(group.getFiles());
        }

        final Set<ReferencedFile> result = new LinkedHashSet<>(allFiles);

        for (final ReferencedFile file : allFiles) {
            if (skipFiles.contains(file.getFile())) {
                result.remove(file);
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public Set<String> getLibPaths() {
        final Set<String> result = new LinkedHashSet<>(libPaths);
        for (final FileGroup group : includedGroups) {
            result.addAll(group.getLibPaths());
        }

        return Collections.unmodifiableSet(result);
    }

    public String getName() {
        return name;
    }

    public void includeGroup(final FileGroup group) {
        includedGroups.add(group);
    }

    private final Map<File, ReferencedFile> files = new LinkedHashMap<>();

    private final String name;

    private final Set<String> libPaths = new LinkedHashSet<>();

    private final Set<FileGroup> dependencies = new LinkedHashSet<>();

    private final Set<FileGroup> includedGroups = new LinkedHashSet<>();

    private final Set<File> skipFiles = new LinkedHashSet<>();
    private final Set<String> skipDeps = new LinkedHashSet<>();

    private BuildSet parent;

    public BuildSet getParent() {
        return parent;
    }

    public void setParent(final BuildSet parent) {
        this.parent = parent;
    }
}
