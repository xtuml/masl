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

import org.xtuml.masl.translate.cmake.language.VariableReference;
import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class Variable implements CMakeListsItem {

    public Variable(final String name, final String... values) {
        this.name = name;
        this.values = Arrays.asList(values);
    }

    public void addValue(final String value) {
        values.add(value);
    }

    public String getName() {
        return name;
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {
        writer.write(indent + "set ( " + name);
        TextUtils.formatList(writer, values, "\n" + indent + "   ", "\n" + indent + "   ", "");
        writer.write("\n" + indent + "  )\n");
    }

    public VariableReference getReference() {
        return new VariableReference(this);
    }

    private final String name;

    private final List<String> values;
}
