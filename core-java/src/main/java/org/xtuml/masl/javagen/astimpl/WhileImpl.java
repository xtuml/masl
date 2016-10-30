//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.code.While;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class WhileImpl extends StatementImpl
    implements While
{

  public WhileImpl ( final ASTImpl ast, final ExpressionImpl condition )
  {
    super(ast);
    statement.set(ast.createCodeBlock());
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitWhile(this, p);
  }

  @Override
  public ExpressionImpl getCondition ()
  {
    return condition.get();
  }

  @Override
  public StatementImpl getStatement ()
  {
    return statement.get();
  }


  @Override
  public void setCondition ( final Expression condition )
  {
    this.condition.set((ExpressionImpl)condition);
  }

  @Override
  public void setStatement ( final Statement statement )
  {
    this.statement.set((StatementImpl)statement);
  }

  private final ChildNode<ExpressionImpl> condition = new ChildNode<ExpressionImpl>(this);


  private final ChildNode<StatementImpl>  statement = new ChildNode<StatementImpl>(this);
}
