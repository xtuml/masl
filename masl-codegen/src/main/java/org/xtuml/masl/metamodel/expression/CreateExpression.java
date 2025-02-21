/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;

import java.util.List;

public interface CreateExpression extends Expression {

    interface AttributeValue extends ASTNode {

        AttributeDeclaration getAttribute();

        Expression getValue();
    }

    ObjectDeclaration getObject();

    List<? extends AttributeValue> getAggregate();

    State getState();

}
