//
// File: ProjectDeclarativeItem.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.project;

import java.util.List;

import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.Domain;


public interface ProjectDomain
{

  String getName ();

  Project getProject ();

  Domain getDomain ();

  PragmaList getPragmas ();

  List<? extends ProjectTerminator> getTerminators ();
}
