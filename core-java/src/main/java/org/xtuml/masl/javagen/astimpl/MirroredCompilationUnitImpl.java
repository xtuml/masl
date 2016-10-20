//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;


class MirroredCompilationUnitImpl extends CompilationUnitImpl
{

  MirroredCompilationUnitImpl ( final ASTImpl ast, final java.lang.Class<?> clazz )
  {
    super(ast, clazz.getSimpleName());
    super.addTypeDeclaration(new MirroredTypeDeclarationImpl(getAST(), clazz));
  }

  public MirroredTypeDeclarationImpl getTypeDeclaration ()
  {
    return (MirroredTypeDeclarationImpl)super.getTypeDeclarations().get(0);
  }

  @Override
  public TypeDeclarationImpl addTypeDeclaration ( final TypeDeclaration typeDeclaration )
  {
    throw new UnsupportedOperationException("Mirrored Compilation Unit");
  }

  @Override
  boolean containsPublicTypeNamed ( final String name )
  {
    return getName().equals(name) && getTypeDeclaration().getModifiers().isPublic();
  }

  @Override
  boolean containsTypeNamed ( final String name )
  {
    return getName().equals(name);
  }
}
