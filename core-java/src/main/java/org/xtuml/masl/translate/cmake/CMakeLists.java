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
