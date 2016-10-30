/*
 * Filename : Project.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.metamodelImpl.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;


public class Project extends Positioned
    implements org.xtuml.masl.metamodel.project.Project
{

  private final String              projectName;
  private PragmaList                pragmas;
  private final List<ProjectDomain> domains;

  public Project ( final Position position, final String projectName )
  {
    super(position);
    this.projectName = projectName;
    this.domains = new ArrayList<ProjectDomain>();
  }

  public void addDomain ( final ProjectDomain domain )
  {
    domains.add(domain);
  }

  @Override
  public String getProjectName ()
  {
    return projectName;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public List<ProjectDomain> getDomains ()
  {
    return Collections.unmodifiableList(domains);
  }

  public void setPragmas ( final PragmaList pragmas )
  {
    this.pragmas = pragmas;
  }

}
