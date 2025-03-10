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

import org.xtuml.masl.cppgen.TextFile;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CMakeLists extends TextFile {

    public CMakeLists() {
        this("CMakeLists.txt");
    }

    public CMakeLists(final String filename) {
        super(null, filename);
    }

    @Override
    public void writeCode(final Writer writer) throws IOException {
        super.writeCode(writer);

        items.writeCode(writer, "");
    }

    public void add(final CMakeListsItem item) {
        items.add(item);
    }

    public Iterator<CMakeListsItem> iterator() {
        return items.iterator();
    }

    private final Section items = new Section();

}
