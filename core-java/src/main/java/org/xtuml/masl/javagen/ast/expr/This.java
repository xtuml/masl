//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.TypeBody;


public interface This
{

  TypeQualifier getQualifier ();

  TypeBody getTypeBody ();

  void forceQualifier ();
}
