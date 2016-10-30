//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import java.util.List;

import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.types.ReferenceType;


public interface MethodInvocation
    extends StatementExpression
{

  Expression addArgument ( Expression arg );

  ReferenceType addTypeArgument ( ReferenceType typeArg );

  List<? extends Expression> getArguments ();

  Expression getInstance ();

  Method getMethod ();

  List<? extends ReferenceType> getTypeArguments ();

  Expression setInstance ( Expression instance );

  Method setMethod ( Method method );

  Qualifier getQualifier ();

  void forceQualifier ();

  boolean isSuper ();

  void setSuper ();

}
