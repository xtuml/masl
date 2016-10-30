//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Cast;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;


class CastImpl extends ExpressionImpl
    implements Cast
{

  CastImpl ( final ASTImpl ast, final Type type, final Expression expression )
  {
    super(ast);
    setType(type);
    setExpression(expression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCast(this, p);
  }


  @Override
  public ExpressionImpl getExpression ()
  {
    return expression.get();
  }


  @Override
  public TypeImpl getType ()
  {
    return type.get();
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
  public TypeImpl setType ( final Type type )
  {
    this.type.set((TypeImpl)type);
    return (TypeImpl)type;
  }

  @Override
  protected int getPrecedence ()
  {
    // From operator precedence table in Java in a Nutshell
    return 13;
  }


  private final ChildNode<TypeImpl>       type       = new ChildNode<TypeImpl>(this);
  private final ChildNode<ExpressionImpl> expression = new ChildNode<ExpressionImpl>(this);
}
