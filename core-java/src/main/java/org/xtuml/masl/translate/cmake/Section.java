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

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Section implements CMakeListsItem, Iterable<CMakeListsItem> {

    private final String rule;

    public Section() {
        this(null, null);
    }

    public Section(final String comment) {
        this(comment, null);
    }

    public Section(final String comment, final Character rule) {
        this.comment = comment;
        this.rule = rule == null ? "" : rule + TextUtils.RULED_LINE + rule + "\n";
    }

    public void add(final CMakeListsItem item) {
        items.add(item);
    }

    @Override
    public Iterator<CMakeListsItem> iterator() {
        return items.iterator();
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        if (comment != null) {
            TextUtils.textBlock(writer, indent, null, "# ", rule + comment + "\n" + rule, null, true);
            writer.write("\n\n");
        }

        for (final CMakeListsItem item : items) {
            item.writeCode(writer, indent);
            writer.write("\n");
        }

    }

    List<CMakeListsItem> items = new ArrayList<>();

    private final String comment;

}
