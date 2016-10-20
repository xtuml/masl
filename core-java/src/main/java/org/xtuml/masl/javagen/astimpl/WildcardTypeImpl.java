//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.ast.types.WildcardType;


public class WildcardTypeImpl extends TypeImpl
    implements WildcardType
{

  enum Direction
  {
    EXTENDS, SUPER
  }

  WildcardTypeImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  WildcardTypeImpl ( final ASTImpl ast, final java.lang.reflect.WildcardType type )
  {
    super(ast);
    if ( type.getLowerBounds().length > 0 )
    {
      superBound.set((ReferenceTypeImpl)ast.createType(type.getLowerBounds()[0]));
    }
    else
    {
      extendsBound.set((ReferenceTypeImpl)ast.createType(type.getUpperBounds()[0]));
    }
  }

  @Override
  public ReferenceTypeImpl getSuperBound ()
  {
    return superBound.get();
  }

  @Override
  public ReferenceTypeImpl getExtendsBound ()
  {
    return extendsBound.get();
  }

  @Override
  public void setExtendsBound ( final ReferenceType extendsBound )
  {
    this.extendsBound.set((ReferenceTypeImpl)extendsBound);
  }

  @Override
  public void setSuperBound ( final ReferenceType superBound )
  {
    this.superBound.set((ReferenceTypeImpl)superBound);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitWildcardType(this, p);
  }

  private final ChildNode<ReferenceTypeImpl> superBound   = new ChildNode<ReferenceTypeImpl>(this);
  private final ChildNode<ReferenceTypeImpl> extendsBound = new ChildNode<ReferenceTypeImpl>(this);

  @Override
  public TypeImpl deepCopy ()
  {
    final WildcardTypeImpl result = new WildcardTypeImpl(getAST());
    if ( getSuperBound() != null )
    {
      result.setSuperBound(getSuperBound().deepCopy());
    }
    if ( getExtendsBound() != null )
    {
      result.setExtendsBound(getExtendsBound().deepCopy());
    }
    return result;
  }

}
