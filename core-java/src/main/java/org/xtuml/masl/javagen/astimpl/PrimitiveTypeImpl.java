//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.types.PrimitiveType;


public class PrimitiveTypeImpl extends TypeImpl
    implements PrimitiveType
{

  PrimitiveTypeImpl ( final ASTImpl ast, final Tag tag )
  {
    super(ast);
    this.tag = tag;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPrimitiveType(this, p);
  }

  @Override
  public Tag getTag ()
  {
    return tag;
  }

  private final Tag tag;

  @Override
  public ClassLiteralImpl clazz ()
  {
    return getAST().createClassLiteral(this);
  }

  @Override
  public PrimitiveTypeImpl deepCopy ()
  {
    return new PrimitiveTypeImpl(getAST(), tag);
  }

}
