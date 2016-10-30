//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;


public interface TypeMember
    extends ASTNode
{

  TypeDeclaration getEnclosingTypeDeclaration ();
}
