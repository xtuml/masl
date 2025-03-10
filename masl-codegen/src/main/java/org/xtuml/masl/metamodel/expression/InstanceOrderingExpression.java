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

import org.xtuml.masl.metamodel.object.AttributeDeclaration;

import java.util.List;

public interface InstanceOrderingExpression extends Expression {

    Expression getCollection();

    boolean isReverse();

    List<? extends Component> getOrder();

    interface Component {

        boolean isReverse();

        AttributeDeclaration getAttribute();
    }
}
