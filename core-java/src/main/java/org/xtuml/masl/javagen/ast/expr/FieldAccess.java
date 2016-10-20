//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.Field;


public interface FieldAccess
    extends Expression
{

  Expression getInstance ();

  Field getField ();

  Expression setInstance ( Expression instance );

  Field setField ( Field field );

  Qualifier getQualifier ();

  void forceQualifier ();
}
