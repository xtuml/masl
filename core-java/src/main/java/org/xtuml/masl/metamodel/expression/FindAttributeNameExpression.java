//
// File: FindAttributeNameExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.object.AttributeDeclaration;


public interface FindAttributeNameExpression
    extends Expression
{

  AttributeDeclaration getAttribute ();
}
