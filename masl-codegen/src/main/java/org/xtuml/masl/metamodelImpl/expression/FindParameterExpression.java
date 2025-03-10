/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.HashCode;

import java.util.Collections;
import java.util.List;

public class FindParameterExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.FindParameterExpression {

    private BasicType type;
    private String name;

    public FindParameterExpression(final Position position, final BasicType type) {
        super(position);
        this.type = type;
        this.name = null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public BasicType getType() {
        return type;
    }

    public void overrideType(final BasicType type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FindParameterExpression fp)) {
            return false;
        } else {
            return type.equals(fp.type) && name.equals(fp.name);
        }
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(name.hashCode(), type.hashCode());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return Collections.singletonList(this);
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitFindParameterExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
