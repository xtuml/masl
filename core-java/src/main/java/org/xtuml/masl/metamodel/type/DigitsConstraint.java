//
// File: DigitsConstraint.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.expression.Expression;


public interface DigitsConstraint
    extends TypeConstraint
{

  Expression getDigits ();
}
