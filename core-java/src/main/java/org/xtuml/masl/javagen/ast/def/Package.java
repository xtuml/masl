//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.Collection;

import org.xtuml.masl.javagen.ast.ASTNode;


public interface Package
    extends ASTNode
{

  String getName ();

  Collection<? extends CompilationUnit> getCompilationUnits ();

  CompilationUnit addCompilationUnit ( CompilationUnit compilationUnit );

  CompilationUnit addCompilationUnit ( String name );

  TypeDeclaration addTypeDeclaration ( String name );

}
