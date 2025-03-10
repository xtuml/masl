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
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.List;

public class ParseExpression extends Expression implements org.xtuml.masl.metamodel.expression.ParseExpression {

    ParseExpression(final Position position, final Expression lhs, final Expression argument) throws SemanticError {
        super(position);
        this.argument = argument;
        if (!(lhs instanceof TypeNameExpression)) {
            throw new SemanticError(SemanticErrorCode.CharacteristicRequiresType, position, "parse", lhs.getType());
        }
        StringType.createAnonymous().checkAssignable(argument);
        this.type = ((TypeNameExpression) lhs).getReferencedType();
        this.base = null;

        if (!IntegerType.createAnonymous().isAssignableFrom(type) &&
            !RealType.createAnonymous().isAssignableFrom(type) &&
            !BooleanType.createAnonymous().isAssignableFrom(type) &&
            !DurationType.createAnonymous().isAssignableFrom(type) &&
            !TimestampType.createAnonymous().isAssignableFrom(type)) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, "parse", lhs);
        }

    }

    ParseExpression(final Position position,
                    final Expression lhs,
                    final Expression argument,
                    final Expression base) throws SemanticError {
        super(position);
        this.argument = argument;
        if (!(lhs instanceof TypeNameExpression)) {
            throw new SemanticError(SemanticErrorCode.CharacteristicRequiresType, position, "parse", lhs.getType());
        }

        IntegerType.createAnonymous().checkAssignable(base);
        StringType.createAnonymous().checkAssignable(argument);
        this.type = ((TypeNameExpression) lhs).getReferencedType();

        // Based conversion only valid for integers
        if (!IntegerType.createAnonymous().isAssignableFrom(type)) {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValidParam, position, "parse", lhs, 2);
        }

        this.base = base;
    }

    private ParseExpression(final Position position,
                            final BasicType lhs,
                            final Expression argument,
                            final Expression base) {
        super(position);
        this.argument = argument;
        this.type = lhs;
        this.base = base;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final ParseExpression obj2 = ((ParseExpression) obj);
                return type.equals(obj2.type) && argument == obj2.argument && base == obj2.base;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public Expression getArgument() {
        return argument;
    }

    @Override
    public Expression getBase() {
        return base;
    }

    @Override
    public int getFindAttributeCount() {
        return argument.getFindAttributeCount() + (base == null ? 0 : base.getFindAttributeCount());
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new ParseExpression(getPosition(),
                                   type,
                                   argument.getFindSkeleton(),
                                   base == null ? base : base.getFindSkeleton());
    }

    @Override
    public BasicType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        if (base == null) {
            return HashCode.combineHashes(type.hashCode(), argument.hashCode());
        } else {
            return HashCode.combineHashes(type.hashCode(), argument.hashCode(), base.hashCode());
        }
    }

    @Override
    public String toString() {
        return type + "'parse(" + argument + (base == null ? "" : ", " + base) + ")";
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(argument.getFindArguments());
        if (base != null) {
            params.addAll(base.getFindArguments());
        }
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(argument.getConcreteFindParameters());
        if (base != null) {
            params.addAll(base.getConcreteFindParameters());
        }

        return params;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitParseExpression(this);
    }

    private final BasicType type;
    private final Expression argument;
    private final Expression base;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(argument, base);
    }

}
