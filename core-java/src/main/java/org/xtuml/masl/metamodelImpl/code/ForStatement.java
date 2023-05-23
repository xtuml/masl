//
// File: WhileStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.utils.TextUtils;


public class ForStatement extends Statement
    implements org.xtuml.masl.metamodel.code.ForStatement
{


  public static ForStatement create ( final Position position, final LoopSpec loopSpec )
  {
    if ( loopSpec == null )
    {
      return null;
    }

    try
    {
      return new ForStatement(position, loopSpec);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public ForStatement ( final Position position, final LoopSpec loopSpec ) throws SemanticError
  {
    super(position);
    this.loopSpec = loopSpec;
    nameLookup.addName(loopSpec.getLoopVariableDef());
    this.statements = new ArrayList<Statement>();
  }

  private final NameLookup nameLookup = new NameLookup();

  public NameLookup getNameLookup ()
  {
    return nameLookup;
  }

  public void addStatement ( final Statement statement )
  {
    if ( statement != null )
    {
      statements.add(statement);
      statement.setParentStatement(this);
    }
  }

  @Override
  public LoopSpec getLoopSpec ()
  {
    return this.loopSpec;
  }

  @Override
  public List<Statement> getStatements ()
  {
    return Collections.unmodifiableList(statements);
  }

  @Override
  public List<Statement> getChildStatements ()
  {
    return Collections.unmodifiableList(statements);
  }


  @Override
  public String toAbbreviatedString ()
  {
    return "for " + loopSpec + " loop ...";
  }

  @Override
  public String toString ()
  {
    return "for "
           + loopSpec
           + " loop\n"
           + TextUtils.indentText("  ", TextUtils.formatList(statements, "", "", "\n", "", ""))
           + "end loop;";
  }

  private final LoopSpec        loopSpec;

  private final List<Statement> statements;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitForStatement(this, p);
  }


}
