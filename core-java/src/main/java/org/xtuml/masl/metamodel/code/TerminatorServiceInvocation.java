//
// File: InstanceServiceInvocation.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.expression.Expression;


public interface TerminatorServiceInvocation
    extends Statement
{

  List<? extends Expression> getArguments ();

  DomainTerminatorService getService ();
}
