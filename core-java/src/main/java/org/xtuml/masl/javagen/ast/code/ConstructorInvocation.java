//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.List;

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.astimpl.ExpressionImpl;


public interface ConstructorInvocation
    extends BlockStatement
{

  List<? extends Expression> getArguments ();

  boolean isSuper ();

  void setSuper ();

  ExpressionImpl addArgument ( Expression argument );

  Expression getEnclosingInstance ();

  Expression setEnclosingInstance ( Expression instance );

  List<? extends ReferenceType> getTypeArguments ();

  ReferenceType addTypeArgument ( ReferenceType typeArg );

}
