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
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.ASTNodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class PragmaDefinition implements org.xtuml.masl.metamodel.common.PragmaDefinition {

    String name;
    List<String> values = null;

    public PragmaDefinition(final String name, final List<String> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public List<String> getValues() {
        return new ArrayList<String>(values);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "pragma " + name + " (" + org.xtuml.masl.utils.TextUtils.formatList(values, "", ",", "") + ");";
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitPragmaDefinition(this, p);
    }

}
