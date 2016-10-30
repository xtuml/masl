//
// File: IdentifierDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.object;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;


public interface IdentifierDeclaration
    extends ASTNode
{

  public List<? extends AttributeDeclaration> getAttributes ();

  boolean isPreferred ();
}
