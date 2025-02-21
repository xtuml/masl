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
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectService;

import java.util.List;

public class ObjectServiceInvocation extends ServiceInvocation<ObjectService>
        implements org.xtuml.masl.metamodel.code.ObjectServiceInvocation {

    ObjectServiceInvocation(final Position position, final ObjectService service, final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    protected String getCallPrefix() {
        return getService().getParentObject().getName() + "." + getService().getName();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitObjectServiceInvocation(this);
    }

}
