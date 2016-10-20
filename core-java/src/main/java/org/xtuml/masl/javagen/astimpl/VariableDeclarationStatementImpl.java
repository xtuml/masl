//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.LocalVariableDeclaration;


public class VariableDeclarationStatementImpl extends StatementImpl
    implements LocalVariableDeclaration
{

  VariableDeclarationStatementImpl ( final ASTImpl ast, final LocalVariableImpl declaration )
  {
    super(ast);
    this.declaration.set(declaration);

  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLocalVariableDeclaration(this, p);
  }

  private final ChildNode<LocalVariableImpl> declaration = new ChildNode<LocalVariableImpl>(this);

  @Override
  public LocalVariableImpl getLocalVariable ()
  {
    return declaration.get();
  }

  @Override
  public void setLocalVariable ( final LocalVariable declaration )
  {
    this.declaration.set((LocalVariableImpl)declaration);
  }

}
