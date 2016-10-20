//
// File: CodeTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Comment;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.metamodel.code.AssignmentStatement;
import org.xtuml.masl.metamodel.code.CancelTimerStatement;
import org.xtuml.masl.metamodel.code.CaseStatement;
import org.xtuml.masl.metamodel.code.DelayStatement;
import org.xtuml.masl.metamodel.code.DeleteStatement;
import org.xtuml.masl.metamodel.code.DomainServiceInvocation;
import org.xtuml.masl.metamodel.code.EraseStatement;
import org.xtuml.masl.metamodel.code.ExitStatement;
import org.xtuml.masl.metamodel.code.GenerateStatement;
import org.xtuml.masl.metamodel.code.IOStreamStatement;
import org.xtuml.masl.metamodel.code.InstanceServiceInvocation;
import org.xtuml.masl.metamodel.code.LinkUnlinkStatement;
import org.xtuml.masl.metamodel.code.ObjectServiceInvocation;
import org.xtuml.masl.metamodel.code.PragmaStatement;
import org.xtuml.masl.metamodel.code.RaiseStatement;
import org.xtuml.masl.metamodel.code.ReturnStatement;
import org.xtuml.masl.metamodel.code.ScheduleStatement;
import org.xtuml.masl.metamodel.code.TerminatorServiceInvocation;
import org.xtuml.masl.translate.main.Scope;



public abstract class CodeTranslator
{

  public static CodeTranslator createTranslator ( final org.xtuml.masl.metamodel.code.Statement statement, final Scope parentScope )
  {
    return createTranslator(statement, parentScope, null);
  }

  private static CodeTranslator createTranslator ( final org.xtuml.masl.metamodel.code.Statement statement,
                                                   final Scope parentScope,
                                                   final CodeTranslator parentTranslator )
  {
    if ( statement instanceof org.xtuml.masl.metamodel.code.CodeBlock )
    {
      return new CodeBlockTranslator((org.xtuml.masl.metamodel.code.CodeBlock)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof AssignmentStatement )
    {
      return new AssignmentTranslator((AssignmentStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof DeleteStatement )
    {
      return new DeleteTranslator((DeleteStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof EraseStatement )
    {
      return new EraseTranslator((EraseStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof org.xtuml.masl.metamodel.code.IfStatement )
    {
      return new IfTranslator((org.xtuml.masl.metamodel.code.IfStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof org.xtuml.masl.metamodel.code.WhileStatement )
    {
      return new WhileTranslator((org.xtuml.masl.metamodel.code.WhileStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof org.xtuml.masl.metamodel.code.ForStatement )
    {
      return new ForTranslator((org.xtuml.masl.metamodel.code.ForStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof LinkUnlinkStatement )
    {
      return new LinkUnlinkTranslator((LinkUnlinkStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof DomainServiceInvocation )
    {
      return new ServiceInvocationTranslator((DomainServiceInvocation)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof TerminatorServiceInvocation )
    {
      return new ServiceInvocationTranslator((TerminatorServiceInvocation)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof ObjectServiceInvocation )
    {
      return new ServiceInvocationTranslator((ObjectServiceInvocation)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof InstanceServiceInvocation )
    {
      return new ServiceInvocationTranslator((InstanceServiceInvocation)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof IOStreamStatement )
    {
      return new IOStreamTranslator((IOStreamStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof GenerateStatement )
    {
      return new GenerateTranslator((GenerateStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof ReturnStatement )
    {
      return new ReturnTranslator((ReturnStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof DelayStatement )
    {
      return new DelayTranslator((DelayStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof ExitStatement )
    {
      return new ExitTranslator((ExitStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof CaseStatement )
    {
      return new CaseTranslator((CaseStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof PragmaStatement )
    {
      return new PragmaTranslator((PragmaStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof RaiseStatement )
    {
      return new RaiseTranslator((RaiseStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof ScheduleStatement )
    {
      return new ScheduleStatementTranslator((ScheduleStatement)statement, parentScope, parentTranslator);
    }
    else if ( statement instanceof CancelTimerStatement )
    {
      return new CancelTimerStatementTranslator((CancelTimerStatement)statement, parentScope, parentTranslator);
    }


    throw new IllegalArgumentException("Unrecognised Statement '" + statement + "'");
  }

  protected CodeTranslator ( final org.xtuml.masl.metamodel.code.Statement maslStatement,
                             final Scope parentScope,
                             final CodeTranslator parentTranslator )
  {
    this.maslStatement = maslStatement;
    this.parentTranslator = parentTranslator;
    scope = new Scope(parentScope);
    fullCode = new CodeBlock(Comment.createComment(maslStatement.toAbbreviatedString(), false));

    fullCode.appendStatement(preamble);
    fullCode.appendStatement(code);
    fullCode.appendStatement(postamble);

  }


  public List<CodeTranslator> getChildTranslators ()
  {
    return childTranslators;
  }

  public CodeBlock getFullCode ()
  {
    return fullCode;
  }

  public org.xtuml.masl.metamodel.code.Statement getMaslStatement ()
  {
    return maslStatement;
  }

  public StatementGroup getPostamble ()
  {
    return postamble;
  }

  public StatementGroup getPreamble ()
  {
    return preamble;
  }

  CodeTranslator createChildTranslator ( final org.xtuml.masl.metamodel.code.Statement statement )
  {
    final CodeTranslator child = createTranslator(statement, scope, this);
    childTranslators.add(child);
    return child;
  }

  CodeTranslator getParentTranslator ()
  {
    return parentTranslator;
  }

  private final CodeTranslator parentTranslator;

  public StatementGroup getCode ()
  {
    return code;
  }

  protected Scope getScope ()
  {
    return scope;
  }

  private final Scope                                  scope;

  private final List<CodeTranslator>                   childTranslators = new ArrayList<CodeTranslator>();

  private final StatementGroup                         code             = new StatementGroup();
  private final CodeBlock                              fullCode;

  private final org.xtuml.masl.metamodel.code.Statement maslStatement;
  private final StatementGroup                         postamble        = new StatementGroup();
  private final StatementGroup                         preamble         = new StatementGroup();
}
