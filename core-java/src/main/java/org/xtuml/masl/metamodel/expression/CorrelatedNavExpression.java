//
// File: NavigationExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;


public interface CorrelatedNavExpression
    extends Expression
{

  Expression getLhs ();

  Expression getRhs ();

  RelationshipSpecification getRelationship ();

}
