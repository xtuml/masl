/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;

import java.util.List;

public abstract class ExceptionReference extends Positioned
        implements org.xtuml.masl.metamodel.exception.ExceptionReference {

    public static ExceptionReference create(final Domain.Reference domainRef,
                                            final boolean allowBuiltin,
                                            final String name) {
        if (domainRef == null || name == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(name) : domainRef.getPosition();

            if (allowBuiltin) {
                final ExceptionDeclaration exception = domainRef.getDomain().findException(name);
                if (exception == null) {
                    return BuiltinException.create(position, name);
                } else {
                    return exception.getReference(position);
                }
            } else {
                return domainRef.getDomain().getException(name).getReference(position);
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitExceptionReference(this);
    }

    public ExceptionReference(final Position position) {
        super(position);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
