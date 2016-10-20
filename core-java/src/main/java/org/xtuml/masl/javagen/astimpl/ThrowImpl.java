//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Throw;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class ThrowImpl extends StatementImpl
    implements Throw
{

  public ThrowImpl ( final ASTImpl ast, final ExpressionImpl thrownExpression )
  {
    super(ast);
    setThrownExpression(thrownExpression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitThrow(this, p);
  }

  @Override
  public ExpressionImpl getThrownExpression ()
  {
    return thrownExpression.get();
  }

  @Override
  public void setThrownExpression ( final Expression thrownExpression )
  {
    this.thrownExpression.set((ExpressionImpl)thrownExpression);
  }


  private final ChildNode<ExpressionImpl> thrownExpression = new ChildNode<ExpressionImpl>(this);

}
