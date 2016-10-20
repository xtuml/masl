//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;

import java.util.List;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewInstance;
import org.xtuml.masl.javagen.ast.expr.Qualifier;


public interface DeclaredType
    extends ReferenceType
{

  List<? extends Type> getTypeArguments ();

  TypeDeclaration getTypeDeclaration ();

  void addTypeArgument ( Type argument );

  Qualifier getQualifier ();

  void forceQualifier ();

  NewInstance newInstance ( Expression... args );

}
