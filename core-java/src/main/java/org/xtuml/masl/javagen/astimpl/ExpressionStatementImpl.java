//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;


public class ExpressionStatementImpl extends StatementImpl
    implements org.xtuml.masl.javagen.ast.code.ExpressionStatement
{

  ExpressionStatementImpl ( final ASTImpl ast, final StatementExpression expression )
  {
    super(ast);
    setExpression(expression);
  }

  @Override
  public ExpressionImpl getExpression ()
  {
    return expression.get();
  }

  @Override
  public void setExpression ( final StatementExpression expression )
  {
    this.expression.set((ExpressionImpl)expression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitExpressionStatement(this, p);
  }

  private final ChildNode<ExpressionImpl> expression = new ChildNode<ExpressionImpl>(this);

}
