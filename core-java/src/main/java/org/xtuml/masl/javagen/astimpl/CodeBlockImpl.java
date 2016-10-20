//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.BlockStatement;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.StatementGroup;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;


class CodeBlockImpl extends StatementImpl
    implements CodeBlock, Scoped
{

  private final class CBScope
      extends Scope
  {

    CBScope ()
    {
      super(CodeBlockImpl.this);
    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final FieldAccessImpl fieldAccess,
                                          final boolean visible,
                                          boolean shadowed )
    {
      StatementImpl curStatement = fieldAccess.getEnclosingStatement();
      while ( curStatement.getEnclosingStatement() != CodeBlockImpl.this )
      {
        // Assume we are in the correct hierarchy... null access if not!
        curStatement = curStatement.getEnclosingStatement();
      }

      for ( final StatementImpl statement : statements )
      {
        if ( statement == curStatement )
        {
          // Not affected by declarations after the current statement
          break;
        }

        if ( statement instanceof VariableDeclarationStatementImpl &&
             ((VariableDeclarationStatementImpl)statement).getLocalVariable().getName().equals(fieldAccess.getField()
                                                                                                                  .getName()) )
        {
          shadowed = true;
        }
      }
      return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
    }


    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final EnumConstantAccessImpl enumAccess,
                                          final boolean visible,
                                          boolean shadowed )
    {
      StatementImpl curStatement = enumAccess.getEnclosingStatement();
      while ( curStatement.getEnclosingStatement() != CodeBlockImpl.this )
      {
        // Assume we are in the correct hierarchy... null access if not!
        curStatement = curStatement.getEnclosingStatement();
      }

      for ( final StatementImpl statement : statements )
      {
        if ( statement == curStatement )
        {
          // Not affected by declarations after the current statement
          break;
        }

        if ( statement instanceof VariableDeclarationStatementImpl &&
             ((VariableDeclarationStatementImpl)statement).getLocalVariable().getName().equals(enumAccess.getConstant()
                                                                                                                .getName()) )
        {
          shadowed = true;
        }
      }
      return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
    }
  }


  class StatementGroupImpl
      implements StatementGroup, StatementGroupMember
  {

    @Override
    public StatementGroupImpl addGroup ()
    {
      return addGroup(new StatementGroupImpl());
    }

    @Override
    public StatementImpl addStatement ( final BlockStatement statement )
    {
      statements.add((StatementImpl)statement);
      groupMembers.add((StatementImpl)statement);
      return (StatementImpl)statement;
    }

    @Override
    public StatementImpl addStatement ( final StatementExpression expression )
    {
      return addStatement(expression.asStatement());
    }

    @Override
    public StatementImpl addStatement ( final LocalVariable declaration )
    {
      return addStatement(declaration.asStatement());
    }

    @Override
    public StatementImpl addStatement ( final TypeDeclaration declaration )
    {
      return addStatement(declaration.asStatement());
    }

    private StatementGroupImpl addGroup ( final StatementGroupImpl group )
    {
      groupMembers.add(group);
      return group;
    }

    private List<StatementImpl> getStatements ()
    {
      final List<StatementImpl> result = new ArrayList<StatementImpl>();
      for ( final StatementGroupMember member : groupMembers )
      {
        if ( member instanceof StatementGroupImpl )
        {
          result.addAll(((StatementGroupImpl)member).getStatements());
        }
        else
        {
          result.add((StatementImpl)member);
        }
      }
      return Collections.unmodifiableList(result);
    }

    private final List<StatementGroupMember> groupMembers = new ArrayList<StatementGroupMember>();
  }

  private final StatementGroupImpl mainGroup = new StatementGroupImpl();

  CodeBlockImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  @Override
  public StatementImpl addStatement ( final BlockStatement statement )
  {
    return mainGroup.addStatement(statement);
  }

  @Override
  public List<StatementImpl> getStatements ()
  {
    return mainGroup.getStatements();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCodeBlock(this, p);
  }

  private final Scope               scope      = new CBScope();

  private final List<StatementImpl> statements = new ChildNodeList<StatementImpl>(this);

  @Override
  public Scope getScope ()
  {
    return scope;
  }

  @Override
  public StatementImpl addStatement ( final StatementExpression expression )
  {
    return mainGroup.addStatement(expression);
  }

  @Override
  public StatementImpl addStatement ( final LocalVariable declaration )
  {
    return mainGroup.addStatement(declaration);
  }

  @Override
  public StatementImpl addStatement ( final TypeDeclaration declaration )
  {
    return mainGroup.addStatement(declaration);
  }

  @Override
  public StatementGroup addGroup ()
  {
    return mainGroup.addGroup();
  }

}
