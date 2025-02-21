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
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;

import java.util.List;

public class TerminatorFunctionInvocation extends FunctionInvocation<DomainTerminatorService>
        implements org.xtuml.masl.metamodel.expression.TerminatorFunctionInvocation {

    public TerminatorFunctionInvocation(final Position position,
                                        final DomainTerminatorService service,
                                        final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    protected String getCallPrefix() {
        return getService().getTerminator().getName() + "~>" + getService().getName();
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new TerminatorFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TerminatorFunctionInvocation obj2)) {
            return false;
        } else {

            return getService().equals(getService()) && getArguments().equals(obj2.getArguments());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTerminatorFunctionInvocation(this);
    }
}
