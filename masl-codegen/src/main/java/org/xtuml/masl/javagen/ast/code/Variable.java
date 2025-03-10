/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Modifiers;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;
import org.xtuml.masl.javagen.ast.types.Type;

public interface Variable extends ASTNode {

    Modifiers getModifiers();

    Type getType();

    String getName();

    void setName(String name);

    void setType(Type type);

    VariableAccess asExpression();

    void setFinal();

    boolean isFinal();

}
