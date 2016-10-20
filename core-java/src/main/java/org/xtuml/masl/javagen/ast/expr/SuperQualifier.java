//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.TypeBody;


public interface SuperQualifier
    extends Qualifier
{

  TypeQualifier getQualifier ();

  void forceQualifier ();

  TypeBody getTypeBody ();
}
