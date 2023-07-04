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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.utils.HashCode;

import java.util.List;

public class CastExpression extends CallExpression implements org.xtuml.masl.metamodel.expression.CastExpression {

    private final Expression rhs;
    private final TypeNameExpression typeName;

    public static CastExpression create(final TypeNameExpression type, final Expression rhs) {
        try {
            return new CastExpression(type, rhs);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public CastExpression(final TypeNameExpression type, final Expression rhs) throws SemanticError {
        super(type.getPosition());
        this.rhs = rhs;
        this.typeName = type;

        if (!getType().isConvertibleFrom(rhs.getType())) {
            throw new SemanticError(SemanticErrorCode.NoConversion, rhs.getPosition(), rhs.getType(), getType());
        }
    }

    @Override
    public TypeNameExpression getTypeName() {
        return typeName;
    }

    @Override
    public Expression getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return typeName + "(" + rhs + ")";
    }

    @Override
    public BasicType getType() {
        return typeName.getReferencedType();
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        return rhs.getFindArguments();
    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        return rhs.getConcreteFindParameters();
    }

    @Override
    public int getFindAttributeCount() {
        return rhs.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return CastExpression.create(typeName, rhs.getFindSkeleton());
    }

    @Override
    public int hashCode() {
        return HashCode.makeHash(rhs, typeName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CastExpression obj2) {

            return rhs.equals(obj2.rhs) && typeName.equals(obj2.typeName);
        } else {
            return false;
        }
    }

    @Override
    public LiteralExpression evaluate() {
        if (rhs instanceof IntegerLiteral) {
            if (getType() instanceof RealType) {
                return new RealLiteral(((IntegerLiteral) rhs).getValue().doubleValue());
            } else if (getType() instanceof IntegerType) {
                return (LiteralExpression) rhs;
            } else {
                return null;
            }

        } else if (rhs instanceof RealLiteral) {
            if (getType() instanceof IntegerType) {
                return new IntegerLiteral(((RealLiteral) rhs).getValue().longValue());
            } else if (getType() instanceof RealType) {
                return (LiteralExpression) rhs;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCastExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(typeName, rhs);
    }

}
