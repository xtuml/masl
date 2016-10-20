//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;


class ThisImpl extends ExpressionImpl
    implements This
{

  ThisImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  ThisImpl ( final ASTImpl ast, final TypeBody typeBody )
  {
    super(ast);
    setTypeBody(typeBody);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitThis(this, p);
  }

  @Override
  public void forceQualifier ()
  {
    if ( qualifier.get() == null )
    {
      setQualifier(new TypeQualifierImpl(getAST(), getTypeBody().getParentTypeDeclaration()));
    }
  }

  @Override
  public TypeQualifierImpl getQualifier ()
  {
    if ( getEnclosingScope().requiresQualifier(this) )
    {
      forceQualifier();
    }
    return qualifier.get();
  }

  @Override
  public TypeBodyImpl getTypeBody ()
  {
    if ( typeBody == null )
    {
      typeBody = getEnclosingTypeBody();
    }
    return typeBody;
  }

  @Override
  protected int getPrecedence ()
  {
    return Integer.MAX_VALUE;
  }

  private void setQualifier ( final TypeQualifier qualifier )
  {
    this.qualifier.set((TypeQualifierImpl)qualifier);
  }

  private void setTypeBody ( final TypeBody typeBody )
  {
    this.typeBody = (TypeBodyImpl)typeBody;
  }


  private TypeBodyImpl                       typeBody;

  private final ChildNode<TypeQualifierImpl> qualifier = new ChildNode<TypeQualifierImpl>(this);

}
