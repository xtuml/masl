//
// File: ObjectService.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.object;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;


public interface ObjectService
    extends Service, ASTNode
{

  boolean isInstance ();

  boolean isDeferred ();

  RelationshipDeclaration getRelationship ();

  List<? extends ObjectService> getDeferredTo ();

  ObjectDeclaration getParentObject ();


}
