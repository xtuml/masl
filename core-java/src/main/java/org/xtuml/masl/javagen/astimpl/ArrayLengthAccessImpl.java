//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ArrayLengthAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class ArrayLengthAccessImpl extends ExpressionImpl
    implements ArrayLengthAccess
{

  ArrayLengthAccessImpl ( final ASTImpl ast, final Expression instance )
  {
    super(ast);
    setInstance(instance);
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitArrayLengthAccess(this, p);
  }


  @Override
  public ExpressionImpl getInstance ()
  {
    return instance.get();
  }

  @Override
  public ExpressionImpl setInstance ( Expression instance )
  {
    if ( ((ExpressionImpl)instance).getPrecedence() < getPrecedence() )
    {
      instance = getAST().createParenthesizedExpression(instance);
    }
    this.instance.set((ExpressionImpl)instance);
    return (ExpressionImpl)instance;
  }

  @Override
  protected int getPrecedence ()
  {
    return 15;
  }

  private final ChildNode<ExpressionImpl> instance = new ChildNode<ExpressionImpl>(this);


}
