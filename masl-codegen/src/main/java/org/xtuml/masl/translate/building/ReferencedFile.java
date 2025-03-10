/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.building;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class ReferencedFile {

    private final File file;
    private final FileGroup parent;

    public ReferencedFile(final FileGroup parent, final String filename) {
        this(parent, new File(filename));
    }

    public ReferencedFile(final FileGroup parent, final File file) {
        this.parent = parent;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public FileGroup getParent() {
        return parent;
    }

    public Set<FileGroup> getDependencies() {
        return Collections.emptySet();
    }

    public boolean isPublicHeader() {
        return false;
    }
}
