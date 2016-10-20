//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;

import org.xtuml.masl.javagen.ast.def.TypeParameter;


public interface TypeVariable
    extends ReferenceType
{

  TypeParameter getTypeParameter ();

  String getName ();
}
