/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.FieldAccess;
import org.xtuml.masl.javagen.ast.types.Type;

public interface Field extends TypeMember, ASTNode {

    String getName();

    Modifiers getModifiers();

    Type getType();

    Type setType(Type type);

    void setName(String name);

    FieldAccess asExpression();

    void setVisibility(Visibility visibility);

    Visibility getVisibility();

    void setStatic();

    void setFinal();

    void setTransient();

    void setVolatile();

    boolean isStatic();

    boolean isFinal();

    boolean isVolatile();

    boolean isTransient();

    Expression getInitialValue();

    Expression setInitialValue(Expression initialValue);

}
