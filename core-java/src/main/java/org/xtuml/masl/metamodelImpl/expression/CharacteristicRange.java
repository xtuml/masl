//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class CharacteristicRange extends RangeExpression
    implements org.xtuml.masl.metamodel.CharacteristicRange
{

  private final Expression               min;
  private final Expression               max;
  private final BasicType                type;
  private final CharacteristicExpression range;
  private final TypeNameExpression       typeName;

  public CharacteristicRange ( final CharacteristicExpression range ) throws SemanticError
  {
    super(range.getPosition());
    this.range = range;
    if ( range.getCharacteristic() != CharacteristicExpression.Type.RANGE )
    {
      throw new SemanticError(SemanticErrorCode.ArrayBoundsNotRange, getPosition());
    }
    else if ( range.getLhs() instanceof TypeNameExpression )
    {
      typeName = (TypeNameExpression)range.getLhs();
      min = typeName.getReferencedType().getMinValue();
      max = typeName.getReferencedType().getMaxValue();

      if ( min == null || max == null )
      {
        throw new SemanticError(SemanticErrorCode.ArrayBoundsNotConstant, getPosition());
      }
    }
    else
    {
      throw new SemanticError(SemanticErrorCode.ArrayBoundsNotConstant, getPosition());
    }

    type = range.getType();
  }

  @Override
  public Expression getMin ()
  {
    return min;
  }

  @Override
  public Expression getMax ()
  {
    return max;
  }

  @Override
  public BasicType getType ()
  {
    return type;
  }

  @Override
  public String toString ()
  {
    return range.toString();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof CharacteristicRange) )
    {
      return false;
    }
    else
    {
      final CharacteristicRange rhs = (CharacteristicRange)obj;

      return min.equals(rhs.min) && max.equals(rhs.max);
    }
  }

  @Override
  public int hashCode ()
  {

    return min.hashCode() * 31 + max.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCharacteristicRange(this, p);
  }

  @Override
  public TypeNameExpression getTypeName ()
  {
    return typeName;
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(typeName);
  }


}
