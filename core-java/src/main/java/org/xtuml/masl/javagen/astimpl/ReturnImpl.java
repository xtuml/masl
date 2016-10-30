//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Return;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class ReturnImpl extends StatementImpl
    implements Return
{

  public ReturnImpl ( final ASTImpl ast, final ExpressionImpl thrownExpression )
  {
    super(ast);
    setReturnValue(thrownExpression);
  }

  public ReturnImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitReturn(this, p);
  }

  @Override
  public ExpressionImpl getReturnValue ()
  {
    return returnValue.get();
  }

  @Override
  public void setReturnValue ( final Expression returnValue )
  {
    this.returnValue.set((ExpressionImpl)returnValue);
  }


  private final ChildNode<ExpressionImpl> returnValue = new ChildNode<ExpressionImpl>(this);


}
