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
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DictionaryType;



public class DictionaryAccessExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.DictionaryAccessExpression
{

  private final Expression prefix;
  private final Expression key;

  public DictionaryAccessExpression ( final Position position, final Expression prefix, final Expression index ) throws SemanticError
  {
    super(position);
    ((DictionaryType)prefix.getType().getBasicType()).getKeyType().checkAssignable(index);
    if ( prefix.getType().getBasicType().getActualType() != ActualType.DICTIONARY )
    {
      throw new SemanticError(SemanticErrorCode.ExpectedDictionaryExpression, position, prefix.getType());
    }

    this.prefix = prefix;
    this.key = index;
  }

  @Override
  public Expression getPrefix ()
  {
    return prefix;
  }

  @Override
  public Expression getKey ()
  {
    return key;
  }

  @Override
  public String toString ()
  {
    return prefix + "[" + key + "]";
  }

  @Override
  public BasicType getType ()
  {
    return ((DictionaryType)prefix.getType().getBasicType()).getValueType();
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(prefix.getFindArguments());
    params.addAll(key.getFindArguments());
    return params;
  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(prefix.getConcreteFindParameters());
    params.addAll(key.getConcreteFindParameters());
    return params;
  }


  @Override
  public int getFindAttributeCount ()
  {
    return prefix.getFindAttributeCount() + key.getFindAttributeCount();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    try
    {
      return new DictionaryAccessExpression(getPosition(), prefix.getFindSkeleton(), key.getFindSkeleton());
    }
    catch ( final SemanticError e )
    {
      assert false;
      return null;
    }
  }

  @Override
  public int hashCode ()
  {
    return prefix.hashCode() ^ key.hashCode();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj instanceof DictionaryAccessExpression )
    {

      final DictionaryAccessExpression obj2 = ((DictionaryAccessExpression)obj);
      return prefix.equals(obj2.prefix) && key.equals(obj2.key);
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
    return v.visitDictionaryAccessExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(prefix, key);
  }


}
