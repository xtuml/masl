//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;


public class TypeDeclarationStatementImpl extends StatementImpl
    implements TypeDeclarationStatement
{

  TypeDeclarationStatementImpl ( final ASTImpl ast, final TypeDeclarationImpl declaration )
  {
    super(ast);
    setTypeDeclaration(declaration);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTypeDeclarationStatement(this, p);
  }

  @Override
  public TypeDeclarationImpl getTypeDeclaration ()
  {
    return typeDeclaration.get();
  }

  @Override
  public void setTypeDeclaration ( final TypeDeclaration typeDeclaration )
  {
    this.typeDeclaration.set((TypeDeclarationImpl)typeDeclaration);
  }

  private final ChildNode<TypeDeclarationImpl> typeDeclaration = new ChildNode<TypeDeclarationImpl>(this);

}
