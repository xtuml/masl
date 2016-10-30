//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;



public abstract class OrderingExpression extends Expression
{

  public static class OrderComponent
  {

    String getName ()
    {
      return name;
    }

    boolean isReverse ()
    {
      return reverse;
    }

    public OrderComponent ( final String name, final boolean reverse )
    {
      this.name = name;
      this.reverse = reverse;
    }

    private final String  name;
    private final boolean reverse;
  }


  public static OrderingExpression create ( final Position position,
                                            final Expression expression,
                                            final boolean reverse,
                                            final List<OrderComponent> components )
  {
    try
    {
      OrderingExpression result = null;
      final BasicType basicType = expression.getType().getBasicType();
      if ( basicType instanceof CollectionType )
      {
        final TypeDefinition containedType = ((CollectionType)basicType).getContainedType().getBasicType().getDefinedType();

        if ( containedType instanceof InstanceType )
        {
          result = new InstanceOrderingExpression(position, expression, reverse, components);
        }
        else
        {
          result = new StructureOrderingExpression(position, expression, reverse, components);
        }
      }
      else
      {
        throw new SemanticError(SemanticErrorCode.OrderNotCollection, position);
      }
      return result;
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public abstract static class Component
  {

    Component ( final boolean reverse )
    {
      this.reverse = reverse;
    }

    private final boolean reverse;

    public boolean isReverse ()
    {
      return reverse;
    }
  }

  public OrderingExpression ( final Position position, final Expression collection, final boolean reverse )
  {
    super(position);
    this.collection = collection;
    this.reverse = reverse;


  }

  public Expression getCollection ()
  {
    return collection;
  }

  @Override
  public BasicType getType ()
  {
    return SequenceType.createAnonymous(((CollectionType)collection.getType().getBasicType()).getContainedType());
  }

  @Override
  public int hashCode ()
  {

    return collection.hashCode() ^ (reverse ? 0 : 1);
  }

  public boolean isReverse ()
  {
    return reverse;
  }

  @Override
  public String toString ()
  {
    return collection + " " + (reverse ? "reverse_" : "") + "ordered_by ";

  }

  private final Expression collection;

  private final boolean    reverse;

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(collection);
  }

}
