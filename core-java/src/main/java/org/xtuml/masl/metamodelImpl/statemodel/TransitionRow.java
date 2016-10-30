//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public class TransitionRow
    implements org.xtuml.masl.metamodel.statemodel.TransitionRow
{

  private final State                                                                      initialState;
  private final List<TransitionOption>                                                     options;
  private final Map<org.xtuml.masl.metamodel.statemodel.EventDeclaration, TransitionOption> optionLookup;
  private final PragmaList                                                                 pragmas;

  public static TransitionRow create ( final ObjectDeclaration object,
                                       final String initialState,
                                       final List<TransitionOption> options,
                                       final PragmaList pragmas )
  {
    if ( object == null )
    {
      return null;
    }

    try
    {
      return new TransitionRow(initialState == null ? null : object.getState(initialState), options, pragmas);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  private TransitionRow ( final State initialState, final List<TransitionOption> options, final PragmaList pragmas )
  {
    this.pragmas = pragmas;
    this.initialState = initialState;
    this.options = new ArrayList<TransitionOption>();
    this.optionLookup = new HashMap<org.xtuml.masl.metamodel.statemodel.EventDeclaration, TransitionOption>();

    for ( final TransitionOption option : options )
    {
      if ( option != null )
      {
        try
        {
          final TransitionOption previousDef = optionLookup.get(option.getEvent());
          if ( previousDef != null )
          {
            throw new SemanticError(SemanticErrorCode.TransitionOptionExists,
                                    option.getPosition(),
                                    option.getEvent(),
                                    previousDef
                                               .getPosition(),
                                    initialState == null ? "Non_Existent" : initialState.getName());
          }

          switch ( option.getEvent().getType() )
          {
            case CREATION:
            {
              if ( initialState != null && option.getType() != org.xtuml.masl.metamodel.statemodel.TransitionType.CANNOT_HAPPEN )
              {
                throw new SemanticError(SemanticErrorCode.CreationEventFromState, option.getPosition());
              }
            }
              break;
            case ASSIGNER:
            {
              if ( initialState == null || !initialState.isAssigner() )
              {
                throw new SemanticError(SemanticErrorCode.AssignerEventNotFromAssigner, option.getPosition());
              }
            }
              break;
            case NORMAL:
            {
              if ( initialState == null )
              {
                if ( option.getType() != org.xtuml.masl.metamodel.statemodel.TransitionType.CANNOT_HAPPEN && option.getType() != org.xtuml.masl.metamodel.statemodel.TransitionType.IGNORE )
                {
                  throw new SemanticError(SemanticErrorCode.NormalEventNotFromNormalState, option.getPosition());
                }
              }
              else if ( initialState.isAssigner() )
              {
                throw new SemanticError(SemanticErrorCode.NormalEventNotFromNormalState, option.getPosition());
              }
            }
              break;
          }

          optionLookup.put(option.getEvent(), option);
          this.options.add(option);
        }
        catch ( final SemanticError e )
        {
          e.report();
        }
      }
    }

  }

  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public State getInitialState ()
  {
    return initialState;
  }

  @Override
  public List<TransitionOption> getOptions ()
  {
    return Collections.unmodifiableList(options);
  }


  @Override
  public TransitionOption getOption ( final org.xtuml.masl.metamodel.statemodel.EventDeclaration event )
  {
    return optionLookup.get(event);
  }


  @Override
  public String toString ()
  {
    return (initialState != null ? initialState.getName() : "Non_Existant")
           + "\t(\t"
           + org.xtuml.masl.utils.TextUtils.formatList(options, "", ",\n\t\t", "")
           + " );\n"
           + pragmas;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTransitionRow(this, p);
  }

}
