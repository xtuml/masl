//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface Conditional
    extends Expression
{

  Expression getCondition ();

  Expression setCondition ( Expression condition );

  Expression getTrueValue ();

  Expression getFalseValue ();

  Expression setTrueValue ( Expression value );

  Expression setFalseValue ( Expression value );
}
