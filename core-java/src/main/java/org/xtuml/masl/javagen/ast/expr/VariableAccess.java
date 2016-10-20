//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.code.Variable;


public interface VariableAccess
    extends Expression
{

  Variable getVariable ();

  Variable setVariable ( Variable variable );
}
