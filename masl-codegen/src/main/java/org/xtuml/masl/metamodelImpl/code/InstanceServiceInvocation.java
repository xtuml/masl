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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectService;

import java.util.List;

public class InstanceServiceInvocation extends ServiceInvocation<ObjectService>
        implements org.xtuml.masl.metamodel.code.InstanceServiceInvocation {

    private final Expression instance;

    public InstanceServiceInvocation(final Position position,
                                     final Expression instance,
                                     final ObjectService service,
                                     final List<Expression> arguments) {
        super(position, service, arguments);
        this.instance = instance;
    }

    @Override
    public Expression getInstance() {
        return this.instance;
    }

    @Override
    protected String getCallPrefix() {
        return instance + "." + getService().getName();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitInstanceServiceInvocation(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), instance);
    }

}
