//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.If;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class IfImpl extends StatementImpl
    implements If
{

  public IfImpl ( final ASTImpl ast, final ExpressionImpl condition )
  {
    super(ast);
    setCondition(condition);
    setThen(ast.createCodeBlock());
  }

  @Override
  public ExpressionImpl getCondition ()
  {
    return condition.get();
  }

  @Override
  public StatementImpl getElse ()
  {
    return elseStatement.get();
  }

  @Override
  public StatementImpl getThen ()
  {
    return thenStatement.get();
  }

  @Override
  public void setCondition ( final Expression condition )
  {
    this.condition.set((ExpressionImpl)condition);
  }

  @Override
  public void setElse ( final Statement elseStatement )
  {
    this.elseStatement.set((StatementImpl)elseStatement);
  }

  @Override
  public void setThen ( final Statement thenStatement )
  {
    this.thenStatement.set((StatementImpl)thenStatement);
  }

  @Override
  public Map<ExpressionImpl, StatementImpl> getIfElseChainStatements ()
  {
    final Map<ExpressionImpl, StatementImpl> blocks = new HashMap<ExpressionImpl, StatementImpl>();
    blocks.put(getCondition(), getThen());
    IfImpl curIf = this;
    while ( curIf.getElse() instanceof If )
    {
      curIf = (IfImpl)curIf.getElse();
      blocks.put(curIf.getCondition(), curIf.getThen());
    }
    if ( curIf.getElse() != null )
    {
      blocks.put(null, curIf.getElse());
    }
    return blocks;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitIf(this, p);
  }

  private final ChildNode<ExpressionImpl> condition     = new ChildNode<ExpressionImpl>(this);
  private final ChildNode<StatementImpl>  thenStatement = new ChildNode<StatementImpl>(this);
  private final ChildNode<StatementImpl>  elseStatement = new ChildNode<StatementImpl>(this);


}
