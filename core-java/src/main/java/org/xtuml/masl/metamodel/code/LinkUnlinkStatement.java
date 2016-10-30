//
// File: LinkUnlinkStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public interface LinkUnlinkStatement
    extends Statement
{

  enum Type
  {
    LINK,
    UNLINK
  }

  RelationshipSpecification getRelationship ();

  Expression getLhs ();

  Expression getRhs ();

  Expression getAssoc ();

  Type getLinkType ();

  public ObjectDeclaration getAssocObject ();

  public ObjectDeclaration getLhsObject ();

  public ObjectDeclaration getRhsObject ();


}
