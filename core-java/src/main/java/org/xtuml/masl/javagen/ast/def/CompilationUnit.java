//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;


public interface CompilationUnit
    extends ASTNode
{

  String getName ();

  List<? extends Import> getImportDeclarations ();

  List<? extends TypeDeclaration> getTypeDeclarations ();

  Package getPackage ();

  String getFileName ();

  TypeDeclaration addTypeDeclaration ( TypeDeclaration declaration );

  Import addImportDeclaration ( Import declaration );

  TypeDeclaration createTypeDeclaration ( String name );

}
