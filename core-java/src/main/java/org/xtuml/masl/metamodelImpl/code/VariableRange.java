//
// File: VariableRange.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;


public class VariableRange extends LoopSpec
    implements org.xtuml.masl.metamodel.code.LoopSpec.VariableRange
{

  
  public VariableRange ( final String loopVariable, final boolean reverse, final Expression variable )
  {
    super(loopVariable, reverse, getRangeType(variable));
    this.variable = variable;
  }

  public static BasicType getRangeType ( final Expression variable )
  {
    if ( variable.getType().getBasicType() instanceof ArrayType )
    {
      return ((ArrayType)variable.getType().getBasicType()).getRange().getType();
    }
    else
    {
      return IntegerType.createAnonymous();
    }
  }

  
  @Override
  public Expression getVariable ()
  {
    return this.variable;
  }

  @Override
  public String toString ()
  {
    return super.toString() + " " + variable + "'range";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLoopVariableRange(this, p);
  }

  private final Expression variable;

}
