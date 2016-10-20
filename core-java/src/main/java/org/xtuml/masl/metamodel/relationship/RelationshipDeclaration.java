//
// File: RelationshipDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.relationship;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.Domain;


public interface RelationshipDeclaration
    extends ASTNode
{

  String getName ();

  Domain getDomain ();

  PragmaList getPragmas ();

}
