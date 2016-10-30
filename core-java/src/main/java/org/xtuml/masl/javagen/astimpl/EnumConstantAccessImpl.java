//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;


public class EnumConstantAccessImpl extends ExpressionImpl
    implements EnumConstantAccess
{

  public EnumConstantAccessImpl ( final ASTImpl ast, final EnumConstant constant )
  {
    super(ast);
    setConstant(constant);
  }

  @Override
  protected int getPrecedence ()
  {
    return 15;
  }

  @Override
  public void forceQualifier ()
  {
    if ( qualifier.get() == null )
    {
      setQualifier(new TypeQualifierImpl(getAST(), constant.getDeclaringType()));
    }

  }

  @Override
  public EnumConstantImpl getConstant ()
  {
    return constant;
  }


  @Override
  public EnumConstant setConstant ( final EnumConstant constant )
  {
    return this.constant = (EnumConstantImpl)constant;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEnumConstantAccess(this, p);
  }

  private EnumConstantImpl constant;

  @Override
  public TypeQualifierImpl getQualifier ()
  {
    if ( !(getParentNode() instanceof SwitchBlockImpl) && getEnclosingScope().requiresQualifier(this) )
    {
      forceQualifier();
    }

    return qualifier.get();
  }

  private void setQualifier ( final TypeQualifier var )
  {
    this.qualifier.set((TypeQualifierImpl)var);
  }

  private final ChildNode<TypeQualifierImpl> qualifier = new ChildNode<TypeQualifierImpl>(this);
}
