//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;


public interface TypeDeclarationStatement
    extends BlockStatement
{

  TypeDeclaration getTypeDeclaration ();

  void setTypeDeclaration ( TypeDeclaration typeDeclaration );

}
