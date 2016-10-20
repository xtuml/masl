//
// File: ObjectNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.EventType;


public class EventExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.EventExpression
{

  public static EventExpression create ( final ObjectNameExpression objectName, final String eventName )
  {
    if ( eventName == null )
    {
      return null;
    }

    try
    {
      if ( objectName == null )
      {
        throw new SemanticError(SemanticErrorCode.NoObjectForEvent, Position.getPosition(eventName), eventName);
      }
      final Position position = objectName.getPosition() == null ? Position.getPosition(eventName) : objectName.getPosition();
      return objectName.getObject().getEvent(eventName).getReference(position);

    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  public EventExpression ( final Position position, final EventDeclaration event )
  {
    super(position);
    this.event = event;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof EventExpression) )
    {
      return false;
    }
    else
    {
      final EventExpression obj2 = (EventExpression)obj;

      return event.equals(obj2.event);
    }
  }

  @Override
  public EventDeclaration getEvent ()
  {
    return event;
  }

  @Override
  public BasicType getType ()
  {
    return EventType.createAnonymous();
  }

  @Override
  public int hashCode ()
  {
    return event.hashCode();
  }

  @Override
  public String toString ()
  {
    return event.getParentObject().getName() + "." + event.getName();
  }

  private final EventDeclaration event;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEventExpression(this, p);
  }

}
