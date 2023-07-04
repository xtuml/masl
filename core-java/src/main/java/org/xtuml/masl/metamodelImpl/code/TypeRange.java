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
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class TypeRange extends LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec.TypeRange {

    public TypeRange(final String loopVariable, final boolean reverse, final BasicType type) {
        super(loopVariable, reverse, type);
        this.type = type;
    }

    @Override
    public BasicType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return super.toString() + " " + type + "'elements";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLoopTypeRange(this);
    }

    private final BasicType type;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(type);
    }

}
