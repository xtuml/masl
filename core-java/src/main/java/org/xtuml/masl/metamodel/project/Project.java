//
// File: Project.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.project;

import java.util.List;

import org.xtuml.masl.metamodel.common.PragmaList;


public interface Project
{

  /**
   * Returns the list of domains explicity specified for inclusion in the
   * project
   *
   * @return the list of domains
   */
  List<? extends ProjectDomain> getDomains ();

  /**
   * Returns the list of pragmas defined for the project
   *
   * @return the list of pragmas
   */
  PragmaList getPragmas ();

  /**
   * Returns the name of the project
   *
   * @return the project name
   */
  String getProjectName ();

}
