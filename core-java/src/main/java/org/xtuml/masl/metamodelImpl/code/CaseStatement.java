//
// File: IfCondition.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.utils.TextUtils;


public class CaseStatement extends Statement
    implements org.xtuml.masl.metamodel.code.CaseStatement
{

  public static Alternative createAlternative ( final Position position,
                                                final List<Expression> conditions,
                                                final List<Statement> statements )
  {
    return new Alternative(position, conditions, statements);
  }

  public static Alternative createOther ( final Position position, final List<Statement> statements )
  {
    return new Alternative(position, null, statements);
  }

  public static class Alternative extends Positioned
      implements org.xtuml.masl.metamodel.code.CaseStatement.Alternative
  {

    private final List<Expression> conditions;
    private final List<Statement>  statements;

    private Alternative ( final Position position, final List<Expression> conditions, final List<Statement> statements )
    {
      super(position);
      this.conditions = conditions;
      this.statements = statements;
    }

    @Override
    public List<Expression> getConditions ()
    {
      return conditions == null ? null : Collections.unmodifiableList(conditions);
    }

    @Override
    public List<Statement> getStatements ()
    {
      return Collections.unmodifiableList(statements);
    }

    public String toAbbreviatedString ()
    {
      if ( conditions == null )
      {
        return "\n  when others => ...";
      }
      else
      {
        return TextUtils.formatList(conditions, "\n  when ", "", "", " |\n       ", " => ...");
      }
    }

    private void checkConditions ( final Expression discriminator ) throws SemanticError
    {
      if ( conditions != null )
      {
        for ( final ListIterator<Expression> it = conditions.listIterator(); it.hasNext(); )
        {
          final Expression cond = it.next();
          discriminator.getType().checkAssignable(cond);
          it.set(cond.resolve(discriminator.getType()));
        }
      }
    }

    @Override
    public String toString ()
    {
      return (conditions == null ? "\n  when others =>\n" : TextUtils.formatList(conditions,
                                                                                 "\n  when ",
                                                                                 " |\n       ",
                                                                                 " =>\n"))
             + TextUtils.indentText("    ", TextUtils.formatList(statements, "", "\n", ""));
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitCaseAlternative(this, p);
    }

  }

  private final List<Alternative> alternatives;
  private final Expression        discriminator;


  public static CaseStatement create ( final Position position, final Expression discriminator, final List<Alternative> alternatives )
  {
    if ( discriminator == null || alternatives == null )
    {
      return null;
    }

    try
    {
      return new CaseStatement(position, discriminator, alternatives);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  private CaseStatement ( final Position position, final Expression discriminator, final List<Alternative> alternatives ) throws SemanticError
  {
    super(position);

    for ( final Alternative alt : alternatives )
    {
      alt.checkConditions(discriminator);
    }

    this.discriminator = discriminator;
    this.alternatives = alternatives;
  }

  @Override
  public List<Alternative> getAlternatives ()
  {
    return Collections.unmodifiableList(alternatives);
  }

  @Override
  public List<Statement> getChildStatements ()
  {
    final List<Statement> result = new ArrayList<Statement>();

    for ( final Alternative alt : alternatives )
    {
      result.addAll(alt.getStatements());
    }

    return Collections.unmodifiableList(result);
  }

  @Override
  public Expression getDiscriminator ()
  {
    return this.discriminator;
  }

  @Override
  public String toAbbreviatedString ()
  {
    final List<String> alts = new ArrayList<String>();
    for ( final Alternative alternative : alternatives )
    {
      alts.add(alternative.toAbbreviatedString());
    }
    return "case " + discriminator + " is" + TextUtils.formatList(alts, "", "", "");
  }

  @Override
  public String toString ()
  {
    return "case " + discriminator + " is" + TextUtils.formatList(alternatives, "", "", "\nend case;");
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCaseStatement(this, p);
  }

}
