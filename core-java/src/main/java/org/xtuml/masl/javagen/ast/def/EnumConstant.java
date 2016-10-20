//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;


public interface EnumConstant
    extends ASTNode
{

  String getName ();

  List<? extends Expression> getArguments ();

  TypeBody getTypeBody ();

  TypeBody setTypeBody ( TypeBody body );

  TypeBody setTypeBody ();

  Expression addArgument ( Expression arg );

  EnumConstantAccess asExpression ();

  void setName ( String name );

}
