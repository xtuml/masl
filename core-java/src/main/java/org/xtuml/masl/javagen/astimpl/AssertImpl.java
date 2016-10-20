//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Assert;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class AssertImpl extends StatementImpl
    implements Assert
{

  public AssertImpl ( final ASTImpl ast, final ExpressionImpl condition )
  {
    super(ast);
    setCondition(condition);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitAssert(this, p);
  }

  @Override
  public ExpressionImpl getCondition ()
  {
    return condition.get();
  }

  @Override
  public ExpressionImpl getMessage ()
  {
    return message.get();
  }


  @Override
  public void setCondition ( final Expression condition )
  {
    this.condition.set((ExpressionImpl)condition);
  }

  @Override
  public void setMessage ( final Expression message )
  {
    this.message.set((ExpressionImpl)message);
  }

  private final ChildNode<ExpressionImpl> message   = new ChildNode<ExpressionImpl>(this);
  private final ChildNode<ExpressionImpl> condition = new ChildNode<ExpressionImpl>(this);
}
