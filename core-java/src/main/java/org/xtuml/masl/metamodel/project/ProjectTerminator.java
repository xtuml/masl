//
// File: ProjectTerminator.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.project;

import java.util.List;

import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.DomainTerminator;


public interface ProjectTerminator
{

  String getName ();

  PragmaList getPragmas ();

  ProjectDomain getDomain ();

  DomainTerminator getDomainTerminator ();

  List<? extends ProjectTerminatorService> getServices ();

}
