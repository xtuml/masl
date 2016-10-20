//
// File: InstanceServiceInvocation.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.object.ObjectService;


public interface ObjectServiceInvocation
    extends Statement
{

  List<? extends Expression> getArguments ();

  ObjectService getService ();
}
