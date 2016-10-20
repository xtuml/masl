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
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;
import org.xtuml.masl.utils.HashCode;
import org.xtuml.masl.utils.TextUtils;



public class InstanceOrderingExpression extends OrderingExpression
    implements org.xtuml.masl.metamodel.expression.InstanceOrderingExpression
{

  public static class Component extends OrderingExpression.Component
      implements org.xtuml.masl.metamodel.expression.InstanceOrderingExpression.Component
  {

    Component ( final boolean reverse, final AttributeDeclaration attribute )
    {
      super(reverse);
      this.attribute = attribute;
    }

    @Override
    public AttributeDeclaration getAttribute ()
    {
      return attribute;
    }

    private final AttributeDeclaration attribute;

    @Override
    public String toString ()
    {
      return (isReverse() ? "reverse " : " ") + attribute.getName();
    }

    @Override
    public int hashCode ()
    {
      return HashCode.makeHash(isReverse(), attribute);
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

        return isReverse() == obj2.isReverse() && attribute == obj2.attribute;
      }
    }

  }

  public InstanceOrderingExpression ( final Position position,
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
    if ( !(obj instanceof InstanceOrderingExpression) )
    {
      return false;
    }
    else
    {
      final InstanceOrderingExpression obj2 = (InstanceOrderingExpression)obj;

      return getCollection().equals(obj2.getCollection()) && isReverse() == obj2.isReverse() && order.equals(obj2.order);
    }
  }


  @Override
  public List<InstanceOrderingExpression.Component> getOrder ()
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

    assert containedType instanceof InstanceType;
    final ObjectDeclaration contained = ((InstanceType)containedType).getObjectDeclaration();
    final AttributeDeclaration att = contained.getAttribute(component.getName());
    order.add(new Component(component.isReverse(), att));

  }

  private final List<Component> order;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitInstanceOrderingExpression(this, p);
  }

}
