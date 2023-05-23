//
// File: IfStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.utils.TextUtils;



public class IfStatement extends Statement
    implements org.xtuml.masl.metamodel.code.IfStatement
{

  public static Branch createIfBranch ( final Position position, final Expression condition, final List<Statement> statements )
  {
    if ( condition == null || statements == null )
    {
      return null;
    }

    try
    {
      return new Branch(position, condition, statements);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public static Branch createElseBranch ( final Position position, final List<Statement> statements )
  {
    if ( statements == null )
    {
      return null;
    }

    try
    {
      return new Branch(position, null, statements);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  static public class Branch extends Positioned
      implements org.xtuml.masl.metamodel.code.IfStatement.Branch
  {

    private final Expression      condition;
    private final List<Statement> statements;

    
    public Branch ( final Position position, final Expression condition, final List<Statement> statements ) throws SemanticError
    {
      super(position);
      this.condition = condition;
      this.statements = statements;

      if ( condition != null && !BooleanType.createAnonymous().isAssignableFrom(condition) )
      {
        throw new SemanticError(SemanticErrorCode.ExpectedBooleanCondition, condition.getPosition(), condition.getType());
      }
    }

    
    @Override
    public Expression getCondition ()
    {
      return this.condition;
    }

    
    @Override
    public List<Statement> getStatements ()
    {
      return Collections.unmodifiableList(statements);
    }

    @Override
    public String toString ()
    {
      final String condCode = (condition == null ? "e " : "if " + condition + " then");

      return condCode + "\n" + TextUtils.indentText("  ", TextUtils.formatList(statements, "", "", "\n", "", ""));
    }

    public String toAbbreviatedString ()
    {
      if ( condition != null )
      {
        return "if " + condition + " then ...";
      }
      else
      {
        return "e ...";
      }
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitIfBranch(this, p);
    }


  }

  private final List<Branch> branches;

  public static IfStatement create ( final Position position, final List<Branch> branches )
  {
    return new IfStatement(position, branches);
  }

  private IfStatement ( final Position position, final List<Branch> branches )
  {
    super(position);
    this.branches = branches;
    branches.stream().flatMap(b -> b.getStatements().stream()).forEach(s -> s.setParentStatement(this));
  }

  @Override
  public List<Branch> getBranches ()
  {
    return Collections.unmodifiableList(branches);
  }

  @Override
  public List<Statement> getChildStatements ()
  {
    final List<Statement> result = new ArrayList<Statement>();
    for ( final Branch branch : branches )
    {
      result.addAll(branch.getStatements());
    }
    return Collections.unmodifiableList(result);
  }


  @Override
  public String toString ()
  {
    return TextUtils.formatList(branches, "", "els", "end if;");
  }

  @Override
  public String toAbbreviatedString ()
  {
    final List<String> branchAbbrev = new ArrayList<String>();
    for ( final Branch branch : branches )
    {
      branchAbbrev.add(branch.toAbbreviatedString());
    }
    return TextUtils.formatList(branchAbbrev, "", "\nels", "");
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitIfStatement(this, p);
  }

}
