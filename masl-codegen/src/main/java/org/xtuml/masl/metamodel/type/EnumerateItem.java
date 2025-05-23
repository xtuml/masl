/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.expression.Expression;

public interface EnumerateItem extends ASTNode {

    EnumerateType getEnumerate();

    String getName();

    Expression getValue();

    String getComment();
}
