//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.types.Type;


public interface Throwing
{

  List<? extends Type> getThrownExceptions ();

  Type addThrownException ( Type exceptionType );


}
