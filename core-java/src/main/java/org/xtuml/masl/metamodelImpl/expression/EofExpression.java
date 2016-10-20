//
// File: ElementsExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.metamodelImpl.type.DeviceType;


public class EofExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.EofExpression
{

  EofExpression ( final Position position, final Expression device ) throws SemanticError
  {
    super(position);

    if ( !DeviceType.createAnonymous().isAssignableFrom(device) )
    {
      throw new SemanticError(SemanticErrorCode.ExpectedDeviceExpression, position, device.getType());
    }

    this.device = device;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( obj != null )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj.getClass() == getClass() )
      {
        final EofExpression obj2 = ((EofExpression)obj);
        return device.equals(obj2.device);
      }
      else
      {
        return false;
      }
    }
    return false;
  }


  @Override
  public BasicType getType ()
  {
    return BooleanType.createAnonymous();
  }

  @Override
  public int hashCode ()
  {
    return device.hashCode();
  }

  @Override
  public int getFindAttributeCount ()
  {
    return device.getFindAttributeCount();
  }


  @Override
  public Expression getFindSkeletonInner ()
  {
    try
    {
      return new EofExpression(getPosition(), device.getFindSkeleton());
    }
    catch ( final SemanticError e )
    {
      e.printStackTrace();
      assert false;
      return null;
    }

  }


  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(device.getFindArguments());
    return params;

  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(device.getConcreteFindParameters());
    return params;
  }

  private final Expression device;

  @Override
  public Expression getDevice ()
  {
    return device;
  }

  @Override
  public String toString ()
  {
    return device + "'eof";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEofExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.asList(device);
  }


}
