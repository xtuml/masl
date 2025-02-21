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

import org.xtuml.masl.metamodel.expression.Expression;

/**
 * Represents a MASL assignment statement. e.g. <code>my_age := 37;</code>
 */
public interface AssignmentStatement extends Statement {

    /**
     * Returns an {@link org.xtuml.masl.metamodel.expression.Expression Expression}
     * representing the target of the assignment.
     *
     * @return the target to be assigned a value
     */
    Expression getTarget();

    /**
     * Returns an {@link org.xtuml.masl.metamodel.expression.Expression Expression}
     * representing the value to be assigned to the target.
     *
     * @return the value to be assigned to the target
     */
    Expression getValue();
}
