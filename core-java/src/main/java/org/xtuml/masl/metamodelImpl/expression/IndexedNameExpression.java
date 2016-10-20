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
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;



public class IndexedNameExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.IndexedNameExpression
{

  private final Expression prefix;
  private final Expression index;

  public IndexedNameExpression ( final Position position, final Expression prefix, final Expression index )
  {
    super(position);
    this.prefix = prefix;
    this.index = index;
  }

  @Override
  public Expression getPrefix ()
  {
    return prefix;
  }

  @Override
  public Expression getIndex ()
  {
    return index;
  }

  @Override
  public String toString ()
  {
    return prefix + "[" + index + "]";
  }

  @Override
  public BasicType getType ()
  {
    return prefix.getType().getContainedType();
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(prefix.getFindArguments());
    params.addAll(index.getFindArguments());
    return params;
  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(prefix.getConcreteFindParameters());
    params.addAll(index.getConcreteFindParameters());
    return params;
  }


  @Override
  public int getFindAttributeCount ()
  {
    return prefix.getFindAttributeCount() + index.getFindAttributeCount();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    final Expression nameSkel = prefix.getFindSkeleton();
    final Expression indexSkel = index.getFindSkeleton();

    if ( indexSkel instanceof FindParameterExpression )
    {
      if ( prefix.getType() instanceof ArrayType )
      {
        ((FindParameterExpression)indexSkel).overrideType(((ArrayType)prefix.getType()).getRange().getType());
      }
      else
      {
        ((FindParameterExpression)indexSkel).overrideType(IntegerType.createAnonymous());
      }
    }

    return new IndexedNameExpression(getPosition(), nameSkel, indexSkel);
  }

  @Override
  public int hashCode ()
  {
    return prefix.hashCode() ^ index.hashCode();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj instanceof IndexedNameExpression )
    {

      final IndexedNameExpression obj2 = ((IndexedNameExpression)obj);
      return prefix.equals(obj2.prefix) && index.equals(obj2.index);
    }
    else
    {
      return false;
    }
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
    return v.visitIndexedNameExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(prefix, index);
  }


}
