//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;



public class FindAttributeNameExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.FindAttributeNameExpression
{

  public static FindAttributeNameExpression create ( final ObjectDeclaration parentObject, final String name )
  {
    if ( parentObject == null )
    {
      return null;
    }

    try
    {
      return new FindAttributeNameExpression(Position.getPosition(name), parentObject.getAttribute(name));
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  private final AttributeDeclaration attribute;

  private FindAttributeNameExpression ( final Position position, final AttributeDeclaration attribute )
  {
    super(position);
    this.attribute = attribute;
  }

  @Override
  public AttributeDeclaration getAttribute ()
  {
    return attribute;
  }

  @Override
  public String toString ()
  {
    return attribute.getName();
  }

  public AttributeDeclaration getDeclaration ()
  {
    return attribute;
  }

  @Override
  public BasicType getType ()
  {
    return getDeclaration().getType();
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    return Collections.emptyList();
  }

  @Override
  public int getFindAttributeCount ()
  {
    return 1;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof FindAttributeNameExpression) )
    {
      return false;
    }
    else
    {
      final FindAttributeNameExpression obj2 = (FindAttributeNameExpression)obj;

      return attribute.equals(obj2.attribute);
    }
  }

  @Override
  public int hashCode ()
  {

    return attribute.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitFindAttributeNameExpression(this, p);
  }


}
