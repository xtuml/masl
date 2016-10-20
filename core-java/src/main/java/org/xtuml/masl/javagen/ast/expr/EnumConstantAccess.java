//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.EnumConstant;


public interface EnumConstantAccess
    extends Expression
{

  EnumConstant getConstant ();

  EnumConstant setConstant ( EnumConstant field );

  Qualifier getQualifier ();

  void forceQualifier ();

}
