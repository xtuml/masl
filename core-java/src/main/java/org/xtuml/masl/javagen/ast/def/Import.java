//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;


public interface Import
    extends ASTNode
{

  Package getParentPackage ();

  TypeDeclaration getTypeDeclaration ();

  String getImportedName ();

  boolean isOnDemand ();

  boolean isSingle ();

  boolean isStatic ();

}
