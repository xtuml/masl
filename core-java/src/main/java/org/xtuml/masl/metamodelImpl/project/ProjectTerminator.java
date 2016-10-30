//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.utils.TextUtils;


public class ProjectTerminator extends Positioned
    implements org.xtuml.masl.metamodel.project.ProjectTerminator
{

  public static ProjectTerminator create ( final Position position,
                                           final ProjectDomain domain,
                                           final String name,
                                           final PragmaList pragmas )
  {
    if ( domain == null || name == null )
    {
      return null;
    }
    try
    {
      final ProjectTerminator obj = new ProjectTerminator(position, domain, name, pragmas);
      domain.addTerminator(obj);
      return obj;
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }

  }

  private ProjectTerminator ( final Position position, final ProjectDomain domain, final String name, final PragmaList pragmas ) throws SemanticError
  {
    super(position);
    this.domain = domain;
    this.name = name;
    this.domainTerminator = domain.getDomain().getTerminator(name);

    this.pragmas = pragmas;
  }

  public void addService ( final ProjectTerminatorService service )
  {
    if ( service == null )
    {
      return;
    }
    services.add(service);
  }

  @Override
  public DomainTerminator getDomainTerminator ()
  {
    return domainTerminator;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public List<ProjectTerminatorService> getServices ()
  {
    return Collections.unmodifiableList(services);
  }

  @Override
  public ProjectDomain getDomain ()
  {
    return domain;
  }

  @Override
  public String toString ()
  {
    return "terminator "
           + domainTerminator.getName()
           + " is\n"
           + TextUtils.alignTabs(TextUtils.formatList(services, "", "", "\n"))
           + "end terminator;\n"
           + pragmas;
  }

  private final DomainTerminator               domainTerminator;

  private final List<ProjectTerminatorService> services = new ArrayList<ProjectTerminatorService>();

  private final PragmaList                     pragmas;

  private final ProjectDomain                  domain;
  private final String                         name;
}
