//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.UnaryExpression;


class UnaryExpressionImpl extends ExpressionImpl
    implements UnaryExpression
{

  UnaryExpressionImpl ( final ASTImpl ast, final Operator operator, final Expression expression )
  {
    super(ast);
    setOperator(operator);
    setExpression(expression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitUnaryExpression(this, p);
  }

  @Override
  public ExpressionImpl getExpression ()
  {
    return expression.get();
  }

  @Override
  public Operator getOperator ()
  {
    return operator;
  }

  @Override
  public ExpressionImpl setExpression ( Expression expression )
  {
    if ( ((ExpressionImpl)expression).getPrecedence() < getPrecedence() )
    {
      expression = getAST().createParenthesizedExpression(expression);
    }
    this.expression.set((ExpressionImpl)expression);
    return (ExpressionImpl)expression;
  }

  @Override
  public void setOperator ( final Operator operator )
  {
    this.operator = operator;
  }


  @Override
  protected int getPrecedence ()
  {
    return 14; // Java in a Nutshell Operator Summary Table
  }

  private final ChildNode<ExpressionImpl> expression = new ChildNode<ExpressionImpl>(this);

  Operator                                operator;
}
