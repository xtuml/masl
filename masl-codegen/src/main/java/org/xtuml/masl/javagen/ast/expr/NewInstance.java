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

import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.List;

public interface NewInstance extends StatementExpression {

    Expression addArgument(Expression arg);

    ReferenceType addTypeArgument(ReferenceType typeArg);

    List<? extends Expression> getArguments();

    DeclaredType getInstanceType();

    List<? extends ReferenceType> getTypeArguments();

    DeclaredType setInstanceType(DeclaredType constructor);

    TypeBody setTypeBody(TypeBody body);

    TypeBody setTypeBody();

    TypeBody getTypeBody();

    Expression getOuterInstance();

    Expression setOuterInstance(Expression instance);

}
