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

import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.List;

public interface MethodInvocation extends StatementExpression {

    Expression addArgument(Expression arg);

    ReferenceType addTypeArgument(ReferenceType typeArg);

    List<? extends Expression> getArguments();

    Expression getInstance();

    Method getMethod();

    List<? extends ReferenceType> getTypeArguments();

    Expression setInstance(Expression instance);

    Method setMethod(Method method);

    Qualifier getQualifier();

    void forceQualifier();

    boolean isSuper();

    void setSuper();

}
