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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.object.ObjectService;

import java.util.List;

public class ObjectFunctionInvocation extends FunctionInvocation<ObjectService>
        implements org.xtuml.masl.metamodel.expression.ObjectFunctionInvocation {

    public ObjectFunctionInvocation(final Position position,
                                    final ObjectService service,
                                    final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    protected String getCallPrefix() {
        return getService().getParentObject().getName() + "." + getService().getName();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new ObjectFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ObjectFunctionInvocation obj2)) {
            return false;
        } else {

            return getService().equals(obj2.getService()) && getArguments().equals(obj2.getArguments());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitObjectFunctionInvocation(this);
    }

}
