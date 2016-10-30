/*
 * Filename : ProjectDeclarativeItem.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.metamodelImpl.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;


public class ProjectDomain extends Positioned
    implements org.xtuml.masl.metamodel.project.ProjectDomain
{

  private final Project project;
  private final Domain  domain;
  private PragmaList    pragmas;

  public ProjectDomain ( final Domain.Reference domain, final Project project )
  {
    super(domain);
    this.project = project;
    this.domain = domain.getDomain();
  }

  public void setPragmas ( final PragmaList pragmas )
  {
    this.pragmas = pragmas;
  }

  @Override
  public String getName ()
  {
    return domain.getName();
  }

  @Override
  public Domain getDomain ()
  {
    return domain;
  }

  @Override
  public Project getProject ()
  {
    return project;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public List<ProjectTerminator> getTerminators ()
  {
    return Collections.unmodifiableList(terminators);
  }

  public void addTerminator ( final ProjectTerminator terminator )
  {
    if ( terminator == null )
    {
      return;
    }
    terminators.add(terminator);
  }

  private final List<ProjectTerminator> terminators = new ArrayList<ProjectTerminator>();

}
