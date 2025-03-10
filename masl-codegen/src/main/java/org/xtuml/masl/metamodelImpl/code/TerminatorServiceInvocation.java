/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class TerminatorServiceInvocation extends ServiceInvocation<DomainTerminatorService>
        implements org.xtuml.masl.metamodel.code.TerminatorServiceInvocation {

    TerminatorServiceInvocation(final Position position,
                                final DomainTerminatorService service,
                                final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    protected String getCallPrefix() {
        return getService().getTerminator().getName() + "~>" + getService().getName();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitTerminatorServiceInvocation(this);
    }

}
