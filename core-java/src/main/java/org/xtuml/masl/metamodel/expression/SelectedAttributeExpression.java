//
// File: SelectedAttributeExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.object.AttributeDeclaration;


public interface SelectedAttributeExpression
    extends Expression
{

  Expression getPrefix ();

  AttributeDeclaration getAttribute ();

}
