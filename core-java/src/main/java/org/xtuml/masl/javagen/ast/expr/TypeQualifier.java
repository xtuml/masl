//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;


public interface TypeQualifier
    extends Qualifier
{

  TypeDeclaration getTypeDeclaration ();

  Qualifier getQualifier ();

  void forceQualifier ();

}
