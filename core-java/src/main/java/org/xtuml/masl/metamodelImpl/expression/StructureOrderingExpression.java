//
// File: Name.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.StructureElement;
import org.xtuml.masl.metamodelImpl.type.StructureType;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;
import org.xtuml.masl.utils.HashCode;
import org.xtuml.masl.utils.TextUtils;



public class StructureOrderingExpression extends OrderingExpression
    implements org.xtuml.masl.metamodel.expression.StructureOrderingExpression
{

  public static class Component extends OrderingExpression.Component
      implements org.xtuml.masl.metamodel.expression.StructureOrderingExpression.Component
  {

    Component ( final boolean reverse, final StructureElement element )
    {
      super(reverse);
      this.element = element;
    }

    @Override
    public StructureElement getElement ()
    {
      return element;
    }

    private final StructureElement element;

    @Override
    public int hashCode ()
    {
      return HashCode.makeHash(isReverse(), element);
    }

    @Override
    public boolean equals ( final Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( !(obj instanceof Component) )
      {
        return false;
      }
      else
      {
        final Component obj2 = (Component)obj;

        return isReverse() == obj2.isReverse() && element == obj2.element;
      }
    }

    @Override
    public String toString ()
    {
      return (isReverse() ? "reverse " : " ") + element.toString();
    }

  }

  public StructureOrderingExpression ( final Position position,
                                       final Expression collection,
                                       final boolean reverse,
                                       final List<OrderComponent> components )
  {
    super(position, collection, reverse);
    this.order = new ArrayList<Component>();
    for ( final OrderComponent component : components )
    {
      try
      {
        addComponent(component);
      }
      catch ( final SemanticError e )
      {
        e.report();
      }
    }

  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof StructureOrderingExpression) )
    {
      return false;
    }
    else
    {
      final StructureOrderingExpression obj2 = (StructureOrderingExpression)obj;

      return getCollection().equals(obj2.getCollection()) && isReverse() == obj2.isReverse() && order.equals(obj2.order);
    }
  }


  @Override
  public List<StructureOrderingExpression.Component> getOrder ()
  {
    return Collections.unmodifiableList(order);
  }

  @Override
  public int hashCode ()
  {

    return super.hashCode() ^ order.hashCode();
  }

  @Override
  public String toString ()
  {
    return super.toString() + " (" + TextUtils.formatList(order, "", ",", "") + ")";

  }

  public void addComponent ( final OrderComponent component ) throws SemanticError
  {
    final BasicType basicType = getCollection().getType().getBasicType();

    assert basicType instanceof CollectionType;

    final TypeDefinition containedType = ((CollectionType)basicType).getContainedType().getBasicType().getDefinedType();

    if ( containedType instanceof StructureType )
    {
      final StructureType contained = (StructureType)containedType;
      final StructureElement elt = contained.getElement(component.getName());
      order.add(new Component(component.isReverse(), elt));
    }
    else
    {
      throw new SemanticError(SemanticErrorCode.ElementNotFoundOnType,
                              Position.getPosition(component.getName()),
                              component.getName(),
                              getCollection().getType());
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitStructureOrderingExpression(this, p);
  }

  private final List<Component> order;
}
