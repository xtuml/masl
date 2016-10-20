//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface ArrayAccess
    extends Expression
{

  Expression getArrayExpression ();

  Expression setArrayExpression ( Expression expression );

  Expression getIndexExpression ();

  Expression setIndexExpression ( Expression expression );

}
