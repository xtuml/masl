//
// File: RelationshipTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.main.object.ObjectTranslator;



public class RelationshipTranslator
{

  public static RelationshipTranslator getInstance ( final RelationshipDeclaration rel )
  {
    return DomainTranslator.getInstance(rel.getDomain()).getRelationshipTranslator(rel);
  }

  final DomainTranslator domainTranslator;

  public RelationshipTranslator ( final RelationshipDeclaration rel, final Expression relationshipId )
  {
    relationship = rel;
    domainTranslator = DomainTranslator.getInstance(rel.getDomain());
    this.relationshipId = relationshipId;
  }

  public void translateRelationship ()
  {
    if ( relationship instanceof SubtypeRelationshipDeclaration )
    {
      createRelationshipPolymorphicEvents((SubtypeRelationshipDeclaration)relationship);
    }
  }

  private void createRelationshipPolymorphicEvents ( final SubtypeRelationshipDeclaration rel )
  {
    final ObjectTranslator supObj = domainTranslator.getObjectTranslator(rel.getSupertype());

    supObj.addPolymorphism(rel);
  }

  private final RelationshipDeclaration relationship;

  public Expression getRelationshipId ()
  {
    return relationshipId;
  }

  private final Expression relationshipId;


}
