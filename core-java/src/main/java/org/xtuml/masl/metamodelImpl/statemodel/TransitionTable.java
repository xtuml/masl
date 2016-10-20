//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public class TransitionTable extends Positioned
    implements org.xtuml.masl.metamodel.statemodel.TransitionTable
{

  private final boolean             isAssigner;
  private final List<TransitionRow> rows;
  private final PragmaList          pragmas;

  public static void create ( final Position position,
                              final ObjectDeclaration object,
                              final boolean isAssigner,
                              final List<TransitionRow> rows,
                              final PragmaList pragmas )
  {
    if ( object == null )
    {
      return;
    }

    try
    {
      object.addTransitionTable(new TransitionTable(position, object, isAssigner, rows, pragmas));
    }
    catch ( final SemanticError e )
    {
      e.report();
    }

  }


  private TransitionTable ( final Position position,
                            final ObjectDeclaration object,
                            final boolean isAssigner,
                            final List<TransitionRow> rows,
                            final PragmaList pragmas ) throws SemanticError
  {
    super(position);
    this.pragmas = pragmas;
    this.isAssigner = isAssigner;
    this.rows = rows;
    this.parentObject = object;

    for ( final State state : object.getStates() )
    {
      boolean found = false;
      for ( final TransitionRow row : rows )
      {
        if ( row.getInitialState() == state )
        {
          found = true;
          break;
        }
      }
      if ( !found )
      {
        throw new SemanticError(SemanticErrorCode.NoRowForState, position, state.getName());
      }
    }
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public boolean isAssigner ()
  {
    return isAssigner;
  }

  @Override
  public List<TransitionRow> getRows ()
  {
    return Collections.unmodifiableList(rows);
  }

  private final ObjectDeclaration parentObject;

  public ObjectDeclaration getParentObject ()
  {
    return parentObject;
  }

  @Override
  public String toString ()
  {
    return (isAssigner ? "assigner " : "")
           + "transition is\n"
           + org.xtuml.masl.utils.TextUtils.indentText("  ",
                                                      org.xtuml.masl.utils.TextUtils.formatList(
                                                                                               rows, "", "\n", ""))
           + "end transition;\n" + pragmas;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTransitionTable(this, p);
  }

}
