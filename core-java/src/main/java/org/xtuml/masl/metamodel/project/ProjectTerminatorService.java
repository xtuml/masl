//
// File: ProjectTerminatorService.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.project;

import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;


public interface ProjectTerminatorService
    extends Service
{

  ProjectTerminator getTerminator ();

  DomainTerminatorService getDomainTerminatorService ();
}
