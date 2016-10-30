//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.HashCode;


public class FindParameterExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.FindParameterExpression
{

  private BasicType type;
  private String    name;

  public FindParameterExpression ( final Position position, final BasicType type )
  {
    super(position);
    this.type = type;
    this.name = null;
  }


  @Override
  public String toString ()
  {
    return name;
  }

  @Override
  public BasicType getType ()
  {
    return type;
  }

  public void overrideType ( final BasicType type )
  {
    this.type = type;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof FindParameterExpression) )
    {
      return false;
    }
    else
    {
      final FindParameterExpression fp = (FindParameterExpression)obj;
      return type.equals(fp.type) && name.equals(fp.name);
    }
  }

  @Override
  public int hashCode ()
  {
    return HashCode.combineHashes(name.hashCode(), type.hashCode());
  }

  @Override
  public String getName ()
  {
    return name;
  }

  public void setName ( final String name )
  {
    this.name = name;
  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    return Collections.singletonList(this);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitFindParameterExpression(this, p);
  }

}
