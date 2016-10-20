//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.EventExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public class TransitionOption extends Positioned
    implements org.xtuml.masl.metamodel.statemodel.TransitionOption
{

  private final EventDeclaration event;
  private final TransitionType   type;
  private final State            destinationState;

  public static TransitionOption create ( final EventExpression event,
                                          final ObjectDeclaration object,
                                          final TransitionType type,
                                          final String destinationState )
  {
    if ( event == null || object == null || type == null )
    {
      return null;
    }

    try
    {
      return new TransitionOption(event, type, destinationState == null ? null : object.getState(destinationState));
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  private TransitionOption ( final EventExpression event, final TransitionType type, final State destinationState )
  {
    super(event.getPosition());
    this.event = event.getEvent();
    this.type = type;
    this.destinationState = destinationState;
  }

  @Override
  public EventDeclaration getEvent ()
  {
    return event;
  }

  @Override
  public org.xtuml.masl.metamodel.statemodel.TransitionType getType ()
  {
    return type.getType();
  }

  @Override
  public State getDestinationState ()
  {
    return destinationState;
  }


  @Override
  public String toString ()
  {
    return event.getParentObject().getName() + "." + event.getName() + "\t=> "
           + (destinationState != null ? destinationState.getName() : type.toString());
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTransitionOption(this, p);
  }


}
