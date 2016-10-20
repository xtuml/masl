//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;



public class ParameterNameExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.ParameterNameExpression
{

  private final ParameterDefinition param;

  public ParameterNameExpression ( final Position position, final ParameterDefinition param )
  {
    super(position);
    this.param = param;
  }

  @Override
  public String toString ()
  {
    return param.getName();
  }

  @Override
  public ParameterDefinition getParameter ()
  {
    return param;
  }

  @Override
  public BasicType getType ()
  {
    return param.getType();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof ParameterNameExpression) )
    {
      return false;
    }
    else
    {
      final ParameterNameExpression obj2 = (ParameterNameExpression)obj;

      return param.equals(obj2.param);
    }
  }

  @Override
  public int hashCode ()
  {

    return param.hashCode();
  }

  @Override
  public void checkWriteableInner ( final Position position ) throws SemanticError
  {
    if ( param.getMode() == ParameterDefinition.Mode.IN )
    {
      throw new SemanticError(SemanticErrorCode.AssignToInParameter, position);
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitParameterNameExpression(this, p);
  }


}
