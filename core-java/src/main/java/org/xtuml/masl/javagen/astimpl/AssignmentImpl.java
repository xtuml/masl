//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Assignment;
import org.xtuml.masl.javagen.ast.expr.Expression;


class AssignmentImpl extends ExpressionImpl
    implements Assignment
{

  AssignmentImpl ( final ASTImpl ast, final Expression target, final Operator operator, final Expression source )
  {
    super(ast);
    setTarget(target);
    setOperator(operator);
    setSource(source);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitAssignmen(this, p);
  }

  @Override
  public Operator getOperator ()
  {
    return operator;
  }

  @Override
  public ExpressionImpl getSource ()
  {
    return source.get();
  }

  @Override
  public ExpressionImpl getTarget ()
  {
    return target.get();
  }

  @Override
  public void setOperator ( final Operator operator )
  {
    this.operator = operator;
  }

  @Override
  public ExpressionImpl setSource ( Expression expression )
  {
    if ( ((ExpressionImpl)expression).getPrecedence() < getPrecedence() )
    {
      expression = getAST().createParenthesizedExpression(expression);
    }
    this.source.set((ExpressionImpl)expression);
    return (ExpressionImpl)expression;
  }

  @Override
  public ExpressionImpl setTarget ( Expression expression )
  {
    // Right Associative, so need to parenthesize lhs if equal precedence
    if ( ((ExpressionImpl)expression).getPrecedence() <= getPrecedence() )
    {
      expression = getAST().createParenthesizedExpression(expression);
    }
    this.target.set((ExpressionImpl)expression);
    return (ExpressionImpl)expression;
  }


  @Override
  protected int getPrecedence ()
  {
    // Value from Java in a Nutshell Operator Summary Table
    return 1;
  }

  private final ChildNode<ExpressionImpl> target = new ChildNode<ExpressionImpl>(this);
  private final ChildNode<ExpressionImpl> source = new ChildNode<ExpressionImpl>(this);

  Operator                                operator;
}
