//
// File: NavigationExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;


public interface NavigationExpression
    extends Expression
{

  Expression getLhs ();

  RelationshipSpecification getRelationship ();

  Expression getSkeleton ();

  List<? extends Expression> getArguments ();

}
