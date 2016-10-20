//
// File: StateMachineTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.BreakStatement;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Comment;
import org.xtuml.masl.cppgen.EnumerationType;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.SwitchStatement;
import org.xtuml.masl.cppgen.ThrowStatement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.EnumerationType.Enumerator;
import org.xtuml.masl.cppgen.SwitchStatement.CaseCondition;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.statemodel.TransitionOption;
import org.xtuml.masl.metamodel.statemodel.TransitionRow;
import org.xtuml.masl.metamodel.statemodel.TransitionTable;
import org.xtuml.masl.metamodel.statemodel.TransitionType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;



public class StateMachineTranslator
{

  final static class StateTransition
  {

    StateTransition ( final EnumerationType.Enumerator from, final EnumerationType.Enumerator to, final Function stateFn )
    {
      this.from = from;
      this.to = to;
      this.stateFn = stateFn;
    }

    EnumerationType.Enumerator from;
    EnumerationType.Enumerator to;
    Function                   stateFn;
  }

  
  public StateMachineTranslator ( final TransitionTable fsm, final ObjectTranslator translator )
  {
    this.translator = translator;
    this.fsm = fsm;
    isAssigner = fsm.isAssigner();

    for ( final TransitionRow row : fsm.getRows() )
    {
      final State fromState = row.getInitialState();
      if ( fromState != null )
      {
        states.add(fromState);
      }
    }

    createStateEnumeration();
    createStateFunctions();

    rejigTable();

    if ( states.size() > 0 )
    {
      getCurrentState = translator.getMain().addGetCurrentState(isAssigner, stateEnum);
      setCurrentState = translator.getMain().addSetCurrentState(isAssigner, stateEnum);
    }
    else
    {
      getCurrentState = null;
      setCurrentState = null;
    }

  }

  public Function getGetCurrentState ()
  {
    return getCurrentState;
  }

  public Function getSetCurrentState ()
  {
    return setCurrentState;
  }

  public Expression getState ( final State state )
  {
    return stateEnums.get(state).asExpression();
  }

  public EnumerationType getStateEnum ()
  {
    return stateEnum;
  }

  
  public boolean isAssigner ()
  {
    return this.isAssigner;
  }

  Statement getProcessEventCode ( final EventDeclaration event, final List<Expression> stateArgs )
  {
    final Expression domainId = translator.getDomainTranslator().getDomainId();
    final Expression objectId = translator.getObjectId();
    final Expression eventId = translator.getEventId(event);

    final Expression eventName = Architecture.formatEventName(domainId, objectId, eventId);

    Literal.createStringLiteral(" not allowed from state ");

    if ( states.size() == 0 )
    {
      // No states, so just ignore everything - probably a polymorphic
      // superclass
      return Comment.createComment("Ignore");
    }
    else if ( event.getType() == EventDeclaration.Type.CREATION )
    {
      Statement result = new ThrowStatement(Std.OutOfRangeError.callConstructor(new BinaryExpression(eventName,
                                                                                                     BinaryOperator.PLUS,
                                                                                                     Literal.createStringLiteral(" not allowed from non existent state"))));

      for ( final StateTransition transition : transitions.get(event) )
      {
        if ( transition.stateFn == null )
        {
          result = Comment.createComment("Ignore");
        }
        else
        {
          result = new ExpressionStatement(transition.stateFn.asFunctionCall(stateArgs));
        }
      }
      return result;
    }
    else
    {
      final List<CaseCondition> cases = new ArrayList<CaseCondition>();
      for ( final StateTransition transition : transitions.get(event) )
      {
        // Shouldn't be necessary to check this, as there should be no valid
        // non-creation events from a creation state!
        if ( transition.from != null )
        {
          if ( transition.stateFn == null )
          {
            final StatementGroup ignore = new StatementGroup();
            ignore.appendStatement(Comment.createInlineComment("Ignore"));
            ignore.appendStatement(new BreakStatement());
            cases.add(new CaseCondition(transition.from.asExpression(), ignore));
          }
          else
          {
            final CodeBlock action = new CodeBlock();
            action.appendStatement(new ExpressionStatement(transition.stateFn.asFunctionCall(stateArgs)));
            if ( isAssigner )
            {
              action.appendStatement(Architecture.transitioningAssignerState(translator.getDomainTranslator().getDomainId(),
                                                                             translator.getObjectId(),
                                                                             getCurrentState.asFunctionCall(),
                                                                             transition.to.asExpression()).asStatement());
            }
            else
            {
              action.appendStatement(Architecture.transitioningState(translator.getDomainTranslator().getDomainId(),
                                                                     translator.getObjectId(),
                                                                     translator.getMain().getArchitectureId(),
                                                                     getCurrentState.asFunctionCall(),
                                                                     transition.to.asExpression()).asStatement());
            }
            action.appendStatement(new ExpressionStatement(setCurrentState.asFunctionCall(transition.to.asExpression())));
            action.appendStatement(new BreakStatement());
            cases.add(new CaseCondition(transition.from.asExpression(), action));
          }
        }

      }
      final Expression stateName = Architecture.formatStateActionName(domainId, objectId, getCurrentState.asFunctionCall());
      final Expression objectName = Architecture.formatObjectName(domainId, objectId);
      Expression message = Literal.createStringLiteral("Event ");
      message = new BinaryExpression(message, BinaryOperator.PLUS, eventName);

      if ( !isAssigner )
      {
        Expression identifier = null;
        for ( final AttributeDeclaration att : translator.getObjectDeclaration().getAttributes() )
        {
          if ( att.isPreferredIdentifier() )
          {
            final Expression attText = Boost.lexicalCast(new TypeUsage(Std.string), translator.getAttributeGetter(att)
                                                                                              .asFunctionCall());
            if ( identifier == null )
            {
              identifier = attText;
            }
            else
            {
              identifier = new BinaryExpression(new BinaryExpression(identifier,
                                                                     BinaryOperator.PLUS,
                                                                     Literal
                                                                            .createStringLiteral(",")),
                                                BinaryOperator.PLUS,
                                                attText);
            }
          }
        }
        message = new BinaryExpression(message, BinaryOperator.PLUS, Literal.createStringLiteral(" sent to "));
        message = new BinaryExpression(message, BinaryOperator.PLUS, objectName);
        message = new BinaryExpression(message, BinaryOperator.PLUS, Literal.createStringLiteral("("));
        message = new BinaryExpression(message, BinaryOperator.PLUS, identifier);
        message = new BinaryExpression(message, BinaryOperator.PLUS, Literal.createStringLiteral(")"));
      }
      message = new BinaryExpression(message, BinaryOperator.PLUS, Literal.createStringLiteral(" cannot happen in state "));
      message = new BinaryExpression(message, BinaryOperator.PLUS, stateName);

      final ThrowStatement throwStatement = new ThrowStatement(Std.OutOfRangeError.callConstructor(message));

      return new SwitchStatement(getCurrentState.asFunctionCall(), cases, throwStatement);
    }

  }

  public List<State> getStates ()
  {
    return states;
  }

  void translateCode ()
  {
    for ( final State state : states )
    {
      translator.getStateActionTranslator(state).translate();
    }
  }

  private void createStateEnumeration ()
  {
    if ( states.size() > 0 )
    {
      stateEnum = translator.getMain().addStateEnum(isAssigner);
      for ( final State state : states )
      {
        stateEnums.put(state, stateEnum.addEnumerator(Mangler.mangleName(state), null));
      }
    }
  }

  
  private void createStateFunctions ()
  {
    for ( final State state : states )
    {
      final Enumerator stateId = translator.getStatesEnum().addEnumerator("stateId_" + Mangler.mangleName(state), null);

      final StateActionTranslator stateTranslator = new StateActionTranslator(translator, state, stateId.asExpression());
      translator.addStateActionTranslator(state, stateTranslator);
    }
  }

  /**


   * @return
   */
  private void rejigTable ()
  {
    // Rejig the state model meta data to make it event centric, rather than
    // start state centric.
    for ( final TransitionRow row : fsm.getRows() )
    {
      final State fromState = row.getInitialState();

      for ( final TransitionOption option : row.getOptions() )
      {
        final State toState = option.getDestinationState();
        final EventDeclaration event = option.getEvent();

        Collection<StateTransition> stateTransitions = transitions.get(event);

        if ( stateTransitions == null )
        {
          stateTransitions = new ArrayList<StateTransition>();
          transitions.put(event, stateTransitions);
        }

        if ( option.getType() != TransitionType.CANNOT_HAPPEN )
        {
          final EnumerationType.Enumerator fromEnum = stateEnums.get(fromState);
          final EnumerationType.Enumerator toEnum = stateEnums.get(toState);
          final Function stateFn = option.getType() == TransitionType.IGNORE ? null : translator.getStateActionTranslator(toState)
                                                                                                .getFunction();
          stateTransitions.add(new StateTransition(fromEnum, toEnum, stateFn));
        }
      }
    }
  }


  private final TransitionTable                                    fsm;

  private final Function                                           getCurrentState;
  private final Function                                           setCurrentState;

  private boolean                                                  isAssigner  = false;

  private EnumerationType                                          stateEnum   = null;
  private final Map<State, Enumerator>                             stateEnums  = new HashMap<State, Enumerator>();

  private final List<State>                                        states      = new ArrayList<State>();
  private final Map<EventDeclaration, Collection<StateTransition>> transitions = new HashMap<EventDeclaration, Collection<StateTransition>>();

  private final ObjectTranslator                                   translator;


}
