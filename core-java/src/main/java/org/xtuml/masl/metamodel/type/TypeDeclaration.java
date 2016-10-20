//
// File: TypeDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodel.domain.Domain;


public interface TypeDeclaration
    extends ASTNode
{

  UserDefinedType getDeclaredType ();

  TypeDefinition getTypeDefinition ();

  Domain getDomain ();

  String getName ();

  PragmaList getPragmas ();

  Visibility getVisibility ();

  String getComment ();

}
