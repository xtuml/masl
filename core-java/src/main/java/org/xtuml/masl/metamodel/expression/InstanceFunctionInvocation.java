//
// File: DomainFunctionInvocation.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.object.ObjectService;


public interface InstanceFunctionInvocation
    extends Expression
{

  List<? extends Expression> getArguments ();

  ObjectService getService ();

  Expression getInstance ();

}
