//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Modifiers;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;
import org.xtuml.masl.javagen.ast.types.Type;


public interface Variable
    extends ASTNode
{

  Modifiers getModifiers ();

  Type getType ();

  String getName ();

  void setName ( String name );

  void setType ( Type type );

  VariableAccess asExpression ();

  void setFinal ();

  boolean isFinal ();


}
