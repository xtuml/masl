//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
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
import org.xtuml.masl.metamodelImpl.type.StructureElement;



public class SelectedComponentExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.SelectedComponentExpression
{

  private final Expression       prefix;
  private final StructureElement component;

  public SelectedComponentExpression ( final Position position, final Expression prefix, final StructureElement component )
  {
    super(position);
    this.prefix = prefix;
    this.component = component;
  }

  @Override
  public Expression getPrefix ()
  {
    return prefix;
  }

  @Override
  public StructureElement getComponent ()
  {
    return component;
  }

  @Override
  public String toString ()
  {
    return prefix + "." + component.getName();
  }

  @Override
  public BasicType getType ()
  {
    return component.getType();
  }


  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof SelectedComponentExpression) )
    {
      return false;
    }
    else
    {
      final SelectedComponentExpression obj2 = (SelectedComponentExpression)obj;

      return prefix.equals(obj2.prefix) && component.equals(obj2.component);
    }
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(prefix.getFindArguments());
    return params;
  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(prefix.getConcreteFindParameters());
    return params;
  }


  @Override
  public int getFindAttributeCount ()
  {
    return prefix.getFindAttributeCount();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    final Expression nameSkel = prefix.getFindSkeleton();

    return new SelectedComponentExpression(getPosition(), nameSkel, component);
  }


  @Override
  public int hashCode ()
  {

    return prefix.hashCode() ^ component.hashCode();
  }

  @Override
  public void checkWriteableInner ( final Position position ) throws SemanticError
  {
    if ( prefix instanceof SelectedAttributeExpression )
    {
      throw new SemanticError(SemanticErrorCode.AttributesAreOpaque, position);
    }
    prefix.checkWriteable(position);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSelectedComponentExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(prefix);
  }

}
