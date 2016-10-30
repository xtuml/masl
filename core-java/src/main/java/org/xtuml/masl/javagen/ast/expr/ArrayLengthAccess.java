//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;



public interface ArrayLengthAccess
    extends Expression
{

  Expression getInstance ();

  Expression setInstance ( Expression instance );
}
