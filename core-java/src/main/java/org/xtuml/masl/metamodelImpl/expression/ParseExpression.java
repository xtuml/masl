//
// File: SplitExpression.java
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
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;
import org.xtuml.masl.utils.HashCode;


public class ParseExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.ParseExpression
{

  ParseExpression ( final Position position, final Expression lhs, final Expression argument ) throws SemanticError
  {
    super(position);
    this.argument = argument;
    if ( !(lhs instanceof TypeNameExpression) )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicRequiresType, position, "parse", lhs.getType());
    }
    StringType.createAnonymous().checkAssignable(argument);
    this.type = ((TypeNameExpression)lhs).getReferencedType();
    this.base = null;

    if ( !IntegerType.createAnonymous().isAssignableFrom(type)
         && !RealType.createAnonymous().isAssignableFrom(type)
         && !BooleanType.createAnonymous().isAssignableFrom(type)
         && !DurationType.createAnonymous().isAssignableFrom(type)
         && !TimestampType.createAnonymous().isAssignableFrom(type) )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, "parse", lhs);
    }

  }

  ParseExpression ( final Position position, final Expression lhs, final Expression argument, final Expression base ) throws SemanticError
  {
    super(position);
    this.argument = argument;
    if ( !(lhs instanceof TypeNameExpression) )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicRequiresType, position, "parse", lhs.getType());
    }

    IntegerType.createAnonymous().checkAssignable(base);
    StringType.createAnonymous().checkAssignable(argument);
    this.type = ((TypeNameExpression)lhs).getReferencedType();

    // Based conversion only valid for integers
    if ( !IntegerType.createAnonymous().isAssignableFrom(type) )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicNotValidParam, position, "parse", lhs, 2);
    }

    this.base = base;
  }

  private ParseExpression ( final Position position, final BasicType lhs, final Expression argument, final Expression base )
  {
    super(position);
    this.argument = argument;
    this.type = lhs;
    this.base = base;
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
        final ParseExpression obj2 = ((ParseExpression)obj);
        return type.equals(obj2.type) && argument == obj2.argument && base == obj2.base;
      }
      else
      {
        return false;
      }
    }
    return false;
  }

  @Override
  public Expression getArgument ()
  {
    return argument;
  }

  @Override
  public Expression getBase ()
  {
    return base;
  }

  @Override
  public int getFindAttributeCount ()
  {
    return argument.getFindAttributeCount() + (base == null ? 0 : base.getFindAttributeCount());
  }


  @Override
  public Expression getFindSkeletonInner ()
  {
    return new ParseExpression(getPosition(), type, argument.getFindSkeleton(), base == null ? base : base.getFindSkeleton());
  }

  @Override
  public BasicType getType ()
  {
    return type;
  }

  @Override
  public int hashCode ()
  {
    if ( base == null )
    {
      return HashCode.combineHashes(type.hashCode(), argument.hashCode());
    }
    else
    {
      return HashCode.combineHashes(type.hashCode(), argument.hashCode(), base.hashCode());
    }
  }

  @Override
  public String toString ()
  {
    return type + "'parse(" + argument + (base == null ? "" : ", " + base) + ")";
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.addAll(argument.getFindArguments());
    if ( base != null )
    {
      params.addAll(base.getFindArguments());
    }
    return params;

  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    params.addAll(argument.getConcreteFindParameters());
    if ( base != null )
    {
      params.addAll(base.getConcreteFindParameters());
    }

    return params;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitParseExpression(this, p);
  }

  private final BasicType  type;
  private final Expression argument;
  private final Expression base;

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(argument, base);
  }

}
