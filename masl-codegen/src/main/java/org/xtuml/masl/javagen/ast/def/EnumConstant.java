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
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;

import java.util.List;

public interface EnumConstant extends ASTNode {

    String getName();

    List<? extends Expression> getArguments();

    TypeBody getTypeBody();

    TypeBody setTypeBody(TypeBody body);

    TypeBody setTypeBody();

    Expression addArgument(Expression arg);

    EnumConstantAccess asExpression();

    void setName(String name);

}
