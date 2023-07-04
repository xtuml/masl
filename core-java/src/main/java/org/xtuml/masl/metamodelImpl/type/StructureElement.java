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
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.utils.TextUtils;

import java.util.List;
import java.util.Objects;

public class StructureElement extends Positioned implements org.xtuml.masl.metamodel.type.StructureElement {

    private final String name;
    private final BasicType type;
    private final Expression defaultValue;
    private final PragmaList pragmas;

    public static StructureElement create(final String name,
                                          final BasicType type,
                                          final Expression defaultValue,
                                          final PragmaList pragmas) {
        if (name == null || type == null || pragmas == null) {
            return null;
        }

        return new StructureElement(name, type, defaultValue, pragmas);
    }

    private StructureElement(final String name,
                             final BasicType type,
                             Expression defaultValue,
                             final PragmaList pragmas) {
        super(name);
        this.pragmas = pragmas;
        this.name = name;
        this.type = type;

        if (defaultValue != null) {
            try {
                type.checkAssignable(defaultValue);
            } catch (final SemanticError e) {
                e.report();
                defaultValue = null;
            }
        }
        this.defaultValue = defaultValue;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public Expression getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return (comment == null ? "" : TextUtils.textBlock("", null, "// ", comment, "", true)) +
               name +
               "\t: " +
               type +
               (defaultValue == null ? "" : "\t:= " + defaultValue) +
               ";" +
               org.xtuml.masl.utils.TextUtils.formatList(pragmas.getPragmas(), "\t", "\t", "");
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructureElement rhs)) {
            return false;
        } else {

            return super.equals(rhs) &&
                   name.equals(rhs.name) &&
                   type.equals(rhs.type) &&
                   (Objects.equals(defaultValue, rhs.defaultValue));
        }
    }

    @Override
    public int hashCode() {
        return ((super.hashCode() * 31 + name.hashCode()) * 31 + type.hashCode()) * 31 +
               (defaultValue == null ? 0 : defaultValue.hashCode());
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return comment;
    }

    private String comment;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitStructureElement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(type, defaultValue);
    }

}
