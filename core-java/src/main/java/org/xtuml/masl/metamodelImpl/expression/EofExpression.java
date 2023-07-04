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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.metamodelImpl.type.DeviceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EofExpression extends Expression implements org.xtuml.masl.metamodel.expression.EofExpression {

    EofExpression(final Position position, final Expression device) throws SemanticError {
        super(position);

        if (!DeviceType.createAnonymous().isAssignableFrom(device)) {
            throw new SemanticError(SemanticErrorCode.ExpectedDeviceExpression, position, device.getType());
        }

        this.device = device;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final EofExpression obj2 = ((EofExpression) obj);
                return device.equals(obj2.device);
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public BasicType getType() {
        return BooleanType.createAnonymous();
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public int getFindAttributeCount() {
        return device.getFindAttributeCount();
    }

    @Override
    public Expression getFindSkeletonInner() {
        try {
            return new EofExpression(getPosition(), device.getFindSkeleton());
        } catch (final SemanticError e) {
            e.printStackTrace();
            assert false;
            return null;
        }

    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<Expression>();
        params.addAll(device.getFindArguments());
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
        params.addAll(device.getConcreteFindParameters());
        return params;
    }

    private final Expression device;

    @Override
    public Expression getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return device + "'eof";
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitEofExpression(this, p);
    }

    @Override
    public List<Expression> getChildExpressions() {
        return Collections.singletonList(device);
    }

}
