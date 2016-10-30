//
// File: ProjectTerminatorService.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.project;

import java.util.List;

import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class ProjectTerminatorService extends Service
    implements org.xtuml.masl.metamodel.project.ProjectTerminatorService
{

  public static void create ( final Position position,
                              final ProjectTerminator terminator,
                              final String name,
                              final Visibility type,
                              final List<ParameterDefinition> parameters,
                              final BasicType returnType,
                              final List<ExceptionReference> exceptionSpecs,
                              final PragmaList pragmas )
  {
    if ( terminator == null || name == null || type == null || parameters == null || exceptionSpecs == null || pragmas == null )
    {
      return;
    }

    try
    {
      terminator.addService(new ProjectTerminatorService(position,
                                                         terminator,
                                                         name,
                                                         type,
                                                         parameters,
                                                         returnType,
                                                         exceptionSpecs,
                                                         pragmas));
    }
    catch ( final SemanticError e )
    {
      e.report();
    }
  }


  private ProjectTerminatorService ( final Position position,
                                     final ProjectTerminator terminator,
                                     final String name,
                                     final Visibility type,
                                     final List<ParameterDefinition> parameters,
                                     final BasicType returnType,
                                     final List<ExceptionReference> exceptionSpecs,
                                     final PragmaList pragmas ) throws SemanticError
  {
    super(position, name, type, parameters, returnType, exceptionSpecs, pragmas);
    this.terminator = terminator;
    domainTerminatorService = terminator.getDomainTerminator().getMatchingService(this);
  }


  @Override
  public DomainTerminatorService getDomainTerminatorService ()
  {
    return domainTerminatorService;
  }

  @Override
  public String getFileName ()
  {
    if ( getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME) != null && getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME)
                                                                                                        .size() > 0 )
    {
      return getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).get(0);
    }
    else
    {
      return terminator.getDomainTerminator().getDomain().getName()
             + "_"
             + terminator.getDomainTerminator().getKeyLetters()
             + "_"
             + domainTerminatorService.getName() + (getOverloadNo() > 0 ? "." + getOverloadNo() : "")
             + ".tr";
    }
  }

  @Override
  public String getQualifiedName ()
  {
    return terminator.getDomainTerminator().getDomain().getName() + "::"
           + terminator.getDomainTerminator().getName()
           + "=>"
           + getName();
  }


  @Override
  public String getServiceType ()
  {
    return "";
  }

  @Override
  public ProjectTerminator getTerminator ()
  {
    return terminator;
  }

  private final ProjectTerminator       terminator;
  private final DomainTerminatorService domainTerminatorService;

}
