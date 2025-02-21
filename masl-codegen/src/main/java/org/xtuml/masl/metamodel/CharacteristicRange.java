/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel;

import org.xtuml.masl.metamodel.expression.RangeExpression;
import org.xtuml.masl.metamodelImpl.expression.TypeNameExpression;

public interface CharacteristicRange extends RangeExpression {

    TypeNameExpression getTypeName();
}
