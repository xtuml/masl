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
import org.xtuml.masl.metamodelImpl.object.ObjectService;

import java.util.List;

public class InstanceFunctionInvocation extends FunctionInvocation<ObjectService>
        implements org.xtuml.masl.metamodel.expression.InstanceFunctionInvocation {

    private final Expression instance;

    public InstanceFunctionInvocation(final Position position,
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
    public Expression getFindSkeletonInner() {
        return new InstanceFunctionInvocation(getPosition(),
                                              instance.getFindSkeleton(),
                                              getService(),
                                              getFindSkeletonArguments());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InstanceFunctionInvocation obj2)) {
            return false;
        } else {

            return getService().equals(obj2.getService()) &&
                   instance.equals(obj2.instance) &&
                   getArguments().equals(obj2.getArguments());
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ instance.hashCode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitInstanceFunctionInvocation(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), instance);
    }

}
