//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.astimpl.TypeParameterImpl;


public interface GenericItem
{

  TypeParameter addTypeParameter ( TypeParameter typeParameter );

  List<? extends TypeParameter> getTypeParameters ();

  TypeParameterImpl addTypeParameter ( String name );

}
