//
// File: CreateExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.TextUtils;



public class CreateExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.CreateExpression
{

  public static abstract class CreateAggregateValue extends Positioned
  {

    CreateAggregateValue ( final Position position )
    {
      super(position);
    }
  }

  public static class CurrentState extends CreateAggregateValue
  {

    public static CurrentState create ( final ObjectNameExpression objectName, final String stateName )
    {
      if ( objectName == null || stateName == null )
      {
        return null;
      }
      try
      {
        return new CurrentState(Position.getPosition(stateName), objectName.getObject().getState(stateName));
      }
      catch ( final SemanticError e )
      {
        e.report();
        return null;
      }
    }


    
    public CurrentState ( final Position position, final State state )
    {
      super(position);
      this.state = state;
    }

    
    public State getState ()
    {
      return this.state;
    }

    @Override
    public String toString ()
    {
      return "Current_State\t=> " + state.getName();
    }

    private final State state;
  }

  public static class NormalAttribute extends CreateAggregateValue
      implements org.xtuml.masl.metamodel.expression.CreateExpression.AttributeValue
  {

    public static NormalAttribute create ( final ObjectNameExpression objectName, final String attributeName, final Expression value )
    {
      if ( objectName == null || attributeName == null || value == null )
      {
        return null;
      }
      try
      {
        return new NormalAttribute(Position.getPosition(attributeName), objectName.getObject().getAttribute(attributeName), value);
      }
      catch ( final SemanticError e )
      {
        e.report();
        return null;
      }
    }


    
    public NormalAttribute ( final Position position, final AttributeDeclaration attribute, final Expression value ) throws SemanticError
    {
      super(position);
      this.attribute = attribute;
      this.value = value.resolve(attribute.getType());

      if ( attribute.isUnique() )
      {
        throw new SemanticError(SemanticErrorCode.AssignToUnique,
                                position,
                                attribute.getName());
      }
      else if ( attribute.isReferential() && !attribute.isIdentifier() )
      {
        throw new SemanticError(SemanticErrorCode.AssignToReferential, position, attribute.getName());
      }
      else if ( attribute.getType().getBasicType().getActualType() == ActualType.TIMER )
      {
        throw new SemanticError(SemanticErrorCode.CannotWriteToAttributeType, position, attribute.getName(), attribute.getType()
                                                                                                                      .toString());
      }


      attribute.getType().checkAssignable(value);
    }

    
    @Override
    public AttributeDeclaration getAttribute ()
    {
      return this.attribute;
    }

    
    @Override
    public Expression getValue ()
    {
      return this.value;
    }

    @Override
    public String toString ()
    {
      return attribute.getName() + "\t=> " + value;
    }

    private final AttributeDeclaration attribute;

    private final Expression           value;

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitCreateAttributeValue(this, p);
    }

  }

  public static CreateExpression create ( final Position position,
                                          final ObjectNameExpression object,
                                          final List<CreateAggregateValue> aggregate )
  {
    if ( object == null || aggregate == null )
    {
      return null;
    }
    return new CreateExpression(position, object.getObject(), aggregate);
  }


  
  private CreateExpression ( final Position position, final ObjectDeclaration object, final List<CreateAggregateValue> aggregate )
  {
    super(position);
    this.object = object;
    this.aggregate = new ArrayList<NormalAttribute>();
    State state = null;
    final Set<AttributeDeclaration> requiredIdentifiers = new HashSet<AttributeDeclaration>();
    for ( final AttributeDeclaration attribute : object.getAttributes() )
    {
      if ( attribute.isIdentifier() && !attribute.isUnique() && attribute.getDefault() == null )
      {
        requiredIdentifiers.add(attribute);
      }
    }


    for ( final CreateAggregateValue value : aggregate )
    {
      if ( value instanceof NormalAttribute )
      {
        this.aggregate.add((NormalAttribute)value);
        requiredIdentifiers.remove(((NormalAttribute)value).getAttribute());
      }
      else if ( value instanceof CurrentState )
      {
        state = ((CurrentState)value).getState();
      }
    }
    this.state = state;

    for ( final AttributeDeclaration att : requiredIdentifiers )
    {
      new SemanticError(SemanticErrorCode.CreateIdentifierMissing, position, object.getName(), att.getName()).report();
    }

    if ( object.hasCurrentState() && state == null )
    {
      new SemanticError(SemanticErrorCode.CreateInitialStateMissing, position, object.getName()).report();
    }

  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj instanceof CreateExpression )
    {

      final CreateExpression obj2 = ((CreateExpression)obj);
      return object.equals(obj2.object) && aggregate.equals(obj2.aggregate);
    }
    else
    {
      return false;
    }
  }


  
  @Override
  public List<NormalAttribute> getAggregate ()
  {
    return Collections.unmodifiableList(aggregate);
  }

  @Override
  public State getState ()
  {
    return state;
  }

  /**
   * 
   * @return
   * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
   */
  @Override
  public BasicType getType ()
  {
    return object.getType();
  }

  @Override
  public int hashCode ()
  {
    return object.hashCode() ^ aggregate.hashCode();
  }

  @Override
  public String toString ()
  {
    return TextUtils.alignTabs("create "
                               + object.getName()
                               + " ("
                               + TextUtils.formatList(aggregate, "\n          ", "\t", "", ",\n", " ")
                               + (state != null ? ",\nCurrent_State\t=> " + state.getName() : "")
                               + ")");
  }

  private final List<NormalAttribute> aggregate;

  private final ObjectDeclaration     object;

  private final State                 state;

  @Override
  public ObjectDeclaration getObject ()
  {
    return object;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCreateExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    final List<Expression> result = new ArrayList<Expression>();
    for ( final NormalAttribute att : aggregate )
    {
      result.add(att.getValue());
    }
    return result;
  }
}
