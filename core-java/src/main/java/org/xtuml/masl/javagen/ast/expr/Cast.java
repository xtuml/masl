//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.types.Type;


public interface Cast
    extends Expression
{

  Type getType ();

  Expression getExpression ();

  Expression setExpression ( Expression expression );

  Type setType ( Type type );
}
