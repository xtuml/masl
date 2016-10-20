//
// File: ObjectDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.object;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.statemodel.TransitionTable;


public interface ObjectDeclaration
    extends ASTNode
{

  List<? extends AttributeDeclaration> getAttributes ();

  PragmaList getDeclarationPragmas ();

  PragmaList getDefinitionPragmas ();

  Domain getDomain ();

  List<? extends EventDeclaration> getEvents ();

  List<? extends EventDeclaration> getAllEvents ();

  IdentifierDeclaration getPreferredIdentifier ();

  List<? extends IdentifierDeclaration> getIdentifiers ();

  String getKeyLetters ();

  String getName ();

  List<? extends RelationshipSpecification> getRelationships ();

  List<? extends ObjectService> getServices ();

  List<? extends State> getStates ();

  boolean hasCurrentState ();

  boolean hasAssignerState ();

  TransitionTable getStateMachine ();

  TransitionTable getAssignerStateMachine ();

  List<? extends ObjectDeclaration> getSupertypes ();

}
