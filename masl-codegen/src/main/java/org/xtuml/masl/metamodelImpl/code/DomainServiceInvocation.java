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
import org.xtuml.masl.metamodelImpl.domain.DomainService;
import org.xtuml.masl.metamodelImpl.expression.Expression;

import java.util.List;

public class DomainServiceInvocation extends ServiceInvocation<DomainService>
        implements org.xtuml.masl.metamodel.code.DomainServiceInvocation {

    DomainServiceInvocation(final Position position, final DomainService service, final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    protected String getCallPrefix() {
        return getService().getQualifiedName();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDomainServiceInvocation(this);
    }

}
