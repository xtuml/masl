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
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InternalType;

import java.util.List;

public class TerminatorNameExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.TerminatorNameExpression {

    public static TerminatorNameExpression create(final Domain.Reference domainRef, final String termName) {
        if (domainRef == null || termName == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(termName) : domainRef.getPosition();
            return domainRef.getDomain().getTerminator(termName).getReference(position);

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public TerminatorNameExpression(final Position position, final DomainTerminator terminator) {
        super(position);
        this.terminator = terminator;
    }

    @Override
    public DomainTerminator getTerminator() {
        return terminator;
    }

    private final DomainTerminator terminator;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TerminatorNameExpression term)) {
            return false;
        } else {

            return terminator.equals(term.terminator);
        }
    }

    @Override
    public int hashCode() {
        return terminator.hashCode();
    }

    @Override
    public String toString() {
        return terminator.getName();
    }

    @Override
    public BasicType getType() {
        return InternalType.TERMINATOR;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTerminatorNameExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
