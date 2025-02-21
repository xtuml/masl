/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.types.Type;

import java.util.List;

public interface NewArray extends Expression {

    Type getType();

    List<? extends Expression> getDimensionSizes();

    int getNoDimensions();

    ArrayInitializer getInitialValue();

    void setNoDimensions(int noDimensions);

    ArrayInitializer setInitialValue(ArrayInitializer initialValue);

    Type setType(final Type type);

    Expression addDimensionSize(Expression dimensionSize);
}
