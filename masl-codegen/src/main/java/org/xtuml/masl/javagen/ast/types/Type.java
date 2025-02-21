/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.types;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.*;

public interface Type extends ASTNode {

    NewArray newArray(int noDimensions, ArrayInitializer initialValue);

    NewArray newArray(int noDimensions, Expression... dimensionSizes);

    Cast cast(Expression expression);

    ClassLiteral clazz();

}
