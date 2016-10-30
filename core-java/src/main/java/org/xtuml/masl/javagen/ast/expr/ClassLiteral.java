//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.types.Type;


public interface ClassLiteral
    extends Expression
{

  Type setType ( Type type );

  Type getType ();
}
