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
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.utils.HashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimestampDeltaExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.TimestampDeltaExpression {

    private final static Map<String, Type> typeLookup = new HashMap<>();

    static {
        typeLookup.put("add_years", Type.YEARS);
        typeLookup.put("add_months", Type.MONTHS);
    }

    TimestampDeltaExpression(final Position position,
                             final Expression lhs,
                             final String characteristic,
                             final Expression argument) throws SemanticError {
        super(position);
        this.argument = argument;
        this.lhs = lhs;
        this.characteristic = characteristic;
        this.deltaType = typeLookup.get(characteristic);

        IntegerType.createAnonymous().checkAssignable(argument);

    }

    private TimestampDeltaExpression(final Position position,
                                     final Expression lhs,
                                     final Type splitType,
                                     final String characteristic,
                                     final Expression arg) {
        super(position);
        this.characteristic = characteristic;
        this.lhs = lhs;
        this.deltaType = splitType;
        this.argument = arg;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final TimestampDeltaExpression obj2 = ((TimestampDeltaExpression) obj);
                return lhs.equals(obj2.lhs) &&
                       deltaType == obj2.deltaType &&
                       characteristic == obj2.characteristic &&
                       argument == obj2.argument;
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
    public Type getDeltaType() {
        return deltaType;
    }

    @Override
    public int getFindAttributeCount() {
        return lhs.getFindAttributeCount() + argument.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new TimestampDeltaExpression(getPosition(),
                                            lhs.getFindSkeleton(),
                                            deltaType,
                                            characteristic,
                                            argument.getFindSkeleton());
    }

    @Override
    public Expression getLhs() {
        return lhs;
    }

    @Override
    public BasicType getType() {
        return lhs.getType();
    }

    @Override
    public int hashCode() {
        return HashCode.combineHashes(lhs.hashCode(), argument.hashCode(), deltaType.hashCode());
    }

    @Override
    public String toString() {
        return lhs + "'" + characteristic + "(" + argument + ")";
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        params.addAll(lhs.getFindArguments());
        params.addAll(argument.getFindArguments());
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        params.addAll(lhs.getConcreteFindParameters());
        params.addAll(argument.getConcreteFindParameters());

        return params;

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTimestampDeltaExpression(this);
    }

    private final Expression lhs;
    private final Expression argument;
    private final Type deltaType;
    private final String characteristic;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs, argument);
    }

}
