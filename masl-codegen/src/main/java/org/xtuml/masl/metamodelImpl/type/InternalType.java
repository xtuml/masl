/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;

import java.util.List;

public class InternalType extends BasicType {

    public final static InternalType OBJECT = new InternalType("object");
    public final static InternalType SERVICE = new InternalType("service");
    public final static InternalType TYPE = new InternalType("type");
    public final static InternalType AMBIGUOUS_ENUM = new InternalType("enum");
    public final static InternalType STREAM_MODIFIER = new InternalType("stream_modifier");
    public static final InternalType TERMINATOR = new InternalType("terminator");
    public static final InternalType SPLIT = new InternalType("split");
    public static final InternalType CHARACTERISTIC = new InternalType("characteristic");

    private InternalType(final String name) {
        super(null, true);
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public InternalType getBasicType() {
        return this;
    }

    @Override
    public InternalType getPrimitiveType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return null;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
