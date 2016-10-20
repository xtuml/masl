//
// File: CodeBlock.java
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


public final class CodeBlock extends Statement
    implements org.xtuml.masl.metamodel.code.CodeBlock
{

  public CodeBlock ( final Position position, final boolean topLevel )
  {
    super(position);
    this.topLevel = topLevel;
    this.variables = new ArrayList<VariableDefinition>();
    this.statements = new ArrayList<Statement>();
    this.exceptionHandlers = new ArrayList<ExceptionHandler>();
  }

  public void addExceptionHandler ( final ExceptionHandler handler )
  {
    if ( handler != null )
    {
      exceptionHandlers.add(handler);
    }
  }

  public void addStatement ( final Statement statement )
  {
    if ( statement != null )
    {
      statements.add(statement);
    }
  }

  public void addVariableDefinition ( final VariableDefinition variable )
  {
    try
    {
      if ( variable != null )
      {
        nameLookup.addName(variable);
        variables.add(variable);
      }
    }
    catch ( final SemanticError e )
    {
      e.report();
    }
  }


  @Override
  public List<ExceptionHandler> getExceptionHandlers ()
  {
    return Collections.unmodifiableList(exceptionHandlers);
  }

  @Override
  public List<Statement> getStatements ()
  {
    return Collections.unmodifiableList(statements);
  }

  @Override
  public List<Statement> getChildStatements ()
  {
    final List<Statement> result = new ArrayList<Statement>();
    result.addAll(statements);
    for ( final ExceptionHandler handler : exceptionHandlers )
    {
      result.addAll(handler.getCode());
    }
    return Collections.unmodifiableList(result);
  }

  @Override
  public List<VariableDefinition> getVariables ()
  {
    return Collections.unmodifiableList(variables);
  }

  @Override
  public String toAbbreviatedString ()
  {
    return (variables.size() > 0 && !topLevel ? "declare ...\n" : "")
           + "begin ...\n"
           + (exceptionHandlers.size() > 0 ? "exception ...\n" : "")
           + "end;";
  }

  @Override
  public String toString ()
  {
    return (variables.size() > 0 && !topLevel ? "declare\n" : "")
           + TextUtils.indentText("  ", TextUtils.formatList(variables, "", "", "\n", "", ""))
           + "begin\n"
           + TextUtils.indentText("  ", TextUtils.formatList(statements, "", "", "\n", "", ""))
           + (exceptionHandlers.size() > 0 ? "exception\n" : "")
           + TextUtils.indentText("  ", TextUtils.formatList(exceptionHandlers, "", "", "\n", "", ""))
           + "end;";
  }

  public NameLookup getNameLookup ()
  {
    return nameLookup;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCodeBlock(this, p);
  }


  private final NameLookup               nameLookup = new NameLookup();

  private final boolean                  topLevel;

  private final List<ExceptionHandler>   exceptionHandlers;


  private final List<Statement>          statements;

  private final List<VariableDefinition> variables;

}
