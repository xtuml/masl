//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.code.VariableDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;



public class VariableNameExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.VariableNameExpression
{

  public VariableNameExpression ( final Position position, final VariableDefinition definition )
  {
    super(position);
    this.definition = definition;
  }

  @Override
  public BasicType getType ()
  {
    return definition.getType();
  }

  @Override
  public String toString ()
  {
    return definition.getName();
  }

  private final VariableDefinition definition;

  @Override
  public VariableDefinition getVariable ()
  {
    return definition;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof VariableNameExpression) )
    {
      return false;
    }
    else
    {
      final VariableNameExpression obj2 = (VariableNameExpression)obj;

      return definition.equals(obj2.definition);
    }
  }

  @Override
  public int hashCode ()
  {

    return definition.hashCode();
  }

  @Override
  public void checkWriteableInner ( final Position position ) throws SemanticError
  {
    if ( definition.isReadonly() )
    {
      throw new SemanticError(SemanticErrorCode.AssignToReadOnly, position, definition.getName());
    }
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitVariableNameExpression(this, p);
  }

}
