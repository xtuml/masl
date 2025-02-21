/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;

public interface LoopSpec extends ASTNode {

    interface FromToRange extends LoopSpec {

        Expression getFrom();

        Expression getTo();
    }

    interface VariableElements extends LoopSpec {

        Expression getVariable();
    }

    interface VariableRange extends LoopSpec {

        Expression getVariable();
    }

    interface TypeRange extends LoopSpec {

        BasicType getType();
    }

    VariableDefinition getLoopVariableDef();

    boolean isReverse();
}
