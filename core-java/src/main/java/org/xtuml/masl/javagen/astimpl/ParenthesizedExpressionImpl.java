//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.ParenthesizedExpression;


class ParenthesizedExpressionImpl extends ExpressionImpl
    implements ParenthesizedExpression
{

  ParenthesizedExpressionImpl ( final ASTImpl ast, final Expression expression )
  {
    super(ast);
    setExpression(expression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitParenthesizedExpression(this, p);
  }

  @Override
  public ExpressionImpl getExpression ()
  {
    return expression.get();
  }

  @Override
  public ExpressionImpl setExpression ( final Expression expression )
  {
    this.expression.set((ExpressionImpl)expression);
    return (ExpressionImpl)expression;
  }

  @Override
  protected int getPrecedence ()
  {
    return Integer.MAX_VALUE;
  }


  private final ChildNode<ExpressionImpl> expression = new ChildNode<ExpressionImpl>(this);

}
