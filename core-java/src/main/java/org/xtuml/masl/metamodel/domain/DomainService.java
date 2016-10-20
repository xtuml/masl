//
// File: DomainService.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.domain;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.Service;


public interface DomainService
    extends Service, ASTNode
{

  Domain getDomain ();

  boolean isExternal ();

  boolean isScenario ();

  int getExternalNo ();

  int getScenarioNo ();

  String getComment ();

}
