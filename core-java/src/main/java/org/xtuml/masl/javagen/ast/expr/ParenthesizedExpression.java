//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;


public interface ParenthesizedExpression
    extends Expression
{

  Expression getExpression ();

  Expression setExpression ( Expression expression );

}
