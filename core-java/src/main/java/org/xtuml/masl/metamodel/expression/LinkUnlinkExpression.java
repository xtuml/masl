//
// File: LinkUnlinkStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;


public interface LinkUnlinkExpression
    extends Expression
{

  enum Type
  {
    LINK, UNLINK
  }

  RelationshipSpecification getRelationship ();

  Expression getLhs ();

  Expression getRhs ();

  Type getLinkType ();

  ObjectDeclaration getRhsObject ();

  ObjectDeclaration getLhsObject ();
}
