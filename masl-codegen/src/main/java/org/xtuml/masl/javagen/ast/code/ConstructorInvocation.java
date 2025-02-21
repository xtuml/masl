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

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.astimpl.ExpressionImpl;

import java.util.List;

public interface ConstructorInvocation extends BlockStatement {

    List<? extends Expression> getArguments();

    boolean isSuper();

    void setSuper();

    ExpressionImpl addArgument(Expression argument);

    Expression getEnclosingInstance();

    Expression setEnclosingInstance(Expression instance);

    List<? extends ReferenceType> getTypeArguments();

    ReferenceType addTypeArgument(ReferenceType typeArg);

}
