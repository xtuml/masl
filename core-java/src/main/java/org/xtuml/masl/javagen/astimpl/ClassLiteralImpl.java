//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.types.Type;


public class ClassLiteralImpl extends ExpressionImpl
    implements ClassLiteral
{

  public ClassLiteralImpl ( final ASTImpl ast, final Type type )
  {
    super(ast);
    setType(type);

  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitClassLiteral(this, p);
  }

  @Override
  public TypeImpl getType ()
  {
    return type.get();
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
    return Integer.MAX_VALUE;
  }


  private final ChildNode<TypeImpl> type = new ChildNode<TypeImpl>(this);


}
