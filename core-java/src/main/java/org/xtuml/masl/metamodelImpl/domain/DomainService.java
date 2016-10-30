//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.domain;

import java.util.List;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class DomainService extends Service
    implements org.xtuml.masl.metamodel.domain.DomainService
{

  public static DomainService create ( final Position position,
                                       final Domain domain,
                                       final String name,
                                       final Visibility type,
                                       final List<ParameterDefinition> parameters,
                                       final BasicType returnType,
                                       final List<ExceptionReference> exceptionSpecs,
                                       final PragmaList pragmas )
  {
    if ( pragmas.getValue(PragmaList.TEST_ONLY).equals("true") && !CommandLine.INSTANCE.isForTest() )
    {
      return null;
    }

    if ( domain == null || name == null || type == null || parameters == null || exceptionSpecs == null || pragmas == null )
    {
      return null;
    }
    try
    {
      final DomainService service = new DomainService(position, domain, name, type, parameters, returnType, exceptionSpecs, pragmas);
      domain.addService(service);
      return service;
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  private DomainService ( final Position position,
                          final Domain domain,
                          final String name,
                          final Visibility visibility,
                          final List<ParameterDefinition> parameters,
                          final BasicType returnType,
                          final List<ExceptionReference> exceptionSpecs,
                          final PragmaList pragmas )
  {
    super(position, name, visibility, parameters, returnType, exceptionSpecs, pragmas);

    this.domain = domain;

    if ( isExternal() )
    {
      fileExtension = ".ext";
    }
    else if ( isScenario() )
    {
      fileExtension = ".scn";
    }
    else if ( isFunction() )
    {
      fileExtension = ".fn";
    }
    else
    {
      fileExtension = ".svc";
    }
    if ( visibility == Visibility.PUBLIC )
    {
      for ( final ParameterDefinition parameter : parameters )
      {
        parameter.getType().checkCanBePublic();
      }
      if ( returnType != null )
      {
        returnType.checkCanBePublic();
      }
    }

  }

  private final String fileExtension;

  @Override
  public String getFileName ()
  {
    if ( getDeclarationPragmas().hasValue(PragmaList.FILENAME) )
    {
      return getDeclarationPragmas().getValue(PragmaList.FILENAME);
    }
    else
    {
      return getName() + (getOverloadNo() > 0 ? "." + getOverloadNo() : "") + fileExtension;
    }
  }

  @Override
  public Domain getDomain ()
  {
    return domain;
  }

  @Override
  public boolean isExternal ()
  {
    return getDeclarationPragmas().hasPragma(PragmaList.EXTERNAL);
  }

  @Override
  public boolean isScenario ()
  {
    return getDeclarationPragmas().hasPragma(PragmaList.SCENARIO);
  }

  @Override
  public int getExternalNo ()
  {
    return Integer.parseInt(getDeclarationPragmas().getValue(PragmaList.EXTERNAL));
  }

  @Override
  public int getScenarioNo ()
  {
    return Integer.parseInt(getDeclarationPragmas().getValue(PragmaList.SCENARIO));
  }

  @Override
  public String getQualifiedName ()
  {
    return domain.getName() + "::" + getName();
  }

  @Override
  public String getServiceType ()
  {
    return "";
  }


  private final Domain domain;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDomainService(this, p);
  }

  private String comment;

  @Override
  public String getComment ()
  {
    return comment;
  }

  public void setComment ( final String comment )
  {
    this.comment = comment;
  }
}
