//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.types.Type;


public class ArrayTypeImpl extends ReferenceTypeImpl
    implements org.xtuml.masl.javagen.ast.types.ArrayType
{

  ArrayTypeImpl ( final ASTImpl ast, final Type elementType )
  {
    super(ast);
    setElementType(elementType);
  }

  @Override
  public void setElementType ( final Type elementType )
  {
    this.elementType.set((TypeImpl)elementType);
  }

  @Override
  public TypeImpl getElementType ()
  {
    return elementType.get();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitArrayType(this, p);
  }

  private final ChildNode<TypeImpl> elementType = new ChildNode<TypeImpl>(this);

  @Override
  public ArrayTypeImpl deepCopy ()
  {
    return new ArrayTypeImpl(getAST(), elementType.get().deepCopy());
  }
}
