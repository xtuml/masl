//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.types.ReferenceType;


public interface TypeParameter
    extends ASTNode
{

  List<? extends ReferenceType> getExtendsBounds ();

  ReferenceType addExtendsBound ( ReferenceType bound );

  String getName ();

  void setName ( String name );
}
