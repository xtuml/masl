/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.ParameterNameExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.HashCode;

import java.util.List;

public class ParameterDefinition extends Name implements org.xtuml.masl.metamodel.common.ParameterDefinition {

    private final ParameterModeType mode;
    private final BasicType type;

    public static ParameterDefinition create(final String name, final ParameterModeType mode, final BasicType type) {
        if (name == null || mode == null || type == null) {
            return null;
        }

        return new ParameterDefinition(name, mode, type);
    }

    private ParameterDefinition(final String name, final ParameterModeType mode, final BasicType type) {
        super(name);
        this.mode = mode;
        this.type = type;
    }

    @Override
    public Mode getMode() {
        return mode.getMode();
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName() + "\t: " + mode + "\t" + type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != ParameterDefinition.class) {
            return false;
        } else {
            final ParameterDefinition rhs = (ParameterDefinition) obj;
            return getName().equals(rhs.getName()) && mode.equals(rhs.mode) && type.equals(rhs.type);
        }

    }

    @Override
    public int hashCode() {

        return HashCode.makeHash(getName(), mode, type);
    }

    @Override
    public ParameterNameExpression getReference(final Position position) {
        return new ParameterNameExpression(position, this);
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitParameterDefinition(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
