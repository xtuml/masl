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
import org.xtuml.masl.metamodel.type.DictionaryType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.ArrayType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RangeType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.StringType;



public class SliceExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.SliceExpression
{

  public static Expression create ( final Position position, final Expression prefix, final Expression range )
  {
    if ( prefix == null || range == null )
    {
      return null;
    }
    try
    {
      if ( prefix.getType().getBasicType() instanceof SequenceType
           || prefix.getType().getBasicType() instanceof ArrayType
           || prefix.getType().getBasicType() instanceof StringType )
      {
        if ( range instanceof RangeExpression )
        {
          RangeType.createAnonymous(IntegerType.createAnonymous()).checkAssignable(range);
          return new SliceExpression(position, prefix, (RangeExpression)range);
        }
        else
        {
          IntegerType.createAnonymous().checkAssignable(range);
          return new IndexedNameExpression(position, prefix, range);
        }
      }
      else if ( prefix.getType().getBasicType() instanceof DictionaryType )
      {
        return new DictionaryAccessExpression(position, prefix, range);
      }
      else
      {
        throw new SemanticError(SemanticErrorCode.IndexNotValid, position, prefix.getType());
      }
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public SliceExpression ( final Position position, final Expression prefix, final RangeExpression range )
  {
    super(position);
    this.prefix = prefix;
    this.range = range;
  }

  @Override
  public Expression getPrefix ()
  {
    return prefix;
  }

  @Override
  public RangeExpression getRange ()
  {
    return range;
  }

  @Override
  public BasicType getType ()
  {
    if ( prefix.getType().getBasicType() instanceof ArrayType )
    {
      return SequenceType.createAnonymous(((ArrayType)prefix.getType()).getContainedType());
    }
    else
    {
      return prefix.getType();
    }
  }

  @Override
  public String toString ()
  {
    return prefix + "[" + range + "]";
  }

  private final Expression      prefix;
  private final RangeExpression range;

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(prefix.getFindArguments());
    if ( range instanceof MinMaxRange )
    {
      params.addAll(getRange().getMin().getFindArguments());
      params.addAll(getRange().getMax().getFindArguments());
    }
    return params;
  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(prefix.getConcreteFindParameters());
    if ( range instanceof MinMaxRange )
    {
      params.addAll(getRange().getMin().getConcreteFindParameters());
      params.addAll(getRange().getMax().getConcreteFindParameters());
    }
    return params;
  }


  @Override
  public int getFindAttributeCount ()
  {
    return prefix.getFindAttributeCount()
           + getRange().getMin().getFindAttributeCount()
           + getRange().getMax().getFindAttributeCount();
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    if ( range instanceof MinMaxRange )
    {
      final Expression minSkel = getRange().getMin().getFindSkeleton();
      final Expression maxSkel = getRange().getMax().getFindSkeleton();

      if ( minSkel instanceof FindParameterExpression )
      {
        if ( prefix.getType() instanceof ArrayType )
        {
          ((FindParameterExpression)minSkel).overrideType(((ArrayType)prefix.getType()).getRange().getType());
        }
        else
        {
          ((FindParameterExpression)minSkel).overrideType(IntegerType.createAnonymous());
        }
      }

      if ( maxSkel instanceof FindParameterExpression )
      {
        if ( prefix.getType() instanceof ArrayType )
        {
          ((FindParameterExpression)maxSkel).overrideType(((ArrayType)prefix.getType()).getRange().getType());
        }
        else
        {
          ((FindParameterExpression)maxSkel).overrideType(IntegerType.createAnonymous());
        }
      }

      return new SliceExpression(null, prefix.getFindSkeleton(), new MinMaxRange(minSkel, maxSkel));
    }
    else
    {
      return new SliceExpression(null, prefix.getFindSkeleton(), range);
    }
  }


  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof SliceExpression) )
    {
      return false;
    }
    else
    {
      final SliceExpression obj2 = (SliceExpression)obj;

      return prefix.equals(obj2.prefix) && range.equals(obj2.range);
    }
  }

  @Override
  public int hashCode ()
  {

    return prefix.hashCode() ^ range.hashCode();
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
    return v.visitSliceExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(prefix, range);
  }


}
