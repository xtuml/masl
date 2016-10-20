//
// File: VariableElements.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.SequenceType;


public class VariableElements extends LoopSpec
    implements org.xtuml.masl.metamodel.code.LoopSpec.VariableElements
{

  
  public VariableElements ( final String loopVariable, final boolean reverse, final Expression variable )
  {
    super(loopVariable, reverse, ((SequenceType)variable.getType().getPrimitiveType()).getContainedType());
    this.variable = variable;
  }

  
  @Override
  public Expression getVariable ()
  {
    return this.variable;
  }


  @Override
  public String toString ()
  {
    return super.toString() + " " + variable + "'elements";
  }


  private final Expression variable;


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLoopVariableElements(this, p);
  }

}
