//
// File: Mangler.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.EnumerateItem;
import org.xtuml.masl.metamodel.type.StructureElement;
import org.xtuml.masl.metamodel.type.TypeDeclaration;



public class Mangler
{

  public static String mangleFile ( final Project project )
  {
    return "__" + project.getProjectName();
  }

  public static String mangleFile ( final Domain domain )
  {
    return "__" + domain.getName();
  }

  public static String mangleFile ( final DomainTerminator terminator )
  {
    return mangleFile(terminator.getDomain()) + "__" + terminator.getName();
  }

  public static String mangleFile ( final DomainTerminatorService service )
  {
    return mangleFile(service.getTerminator()) + "__" + service.getName();
  }

  public static String mangleFile ( final ObjectDeclaration object )
  {
    return mangleFile(object.getDomain()) + "__" + object.getName();
  }

  public static String mangleFile ( final RelationshipDeclaration relationship )
  {
    return mangleFile(relationship.getDomain()) + "__" + relationship.getName();
  }


  public static String mangleFile ( final DomainService service )
  {
    return mangleFile(service.getDomain()) + "__" + service.getName() + (service.getOverloadNo() > 0 ? "_" + service.getOverloadNo() : "");
  }

  public static String mangleFile ( final ObjectService service )
  {
    return mangleFile(service.getParentObject()) + "__" + service.getName() + (service.getOverloadNo() > 0 ? "_" + service.getOverloadNo() : "");
  }

  public static String mangleFile ( final State state )
  {
    return mangleFile(state.getParentObject()) + "__" + state.getName();
  }

  public static String mangleFile ( final ExceptionDeclaration exception )
  {
    return mangleFile(exception.getDomain()) + "__" + exception.getName();
  }

  public static String mangleFile ( final ProjectDomain domain )
  {
    return mangleFile(domain.getProject()) + "__" + domain.getName();
  }

  public static String mangleFile ( final ProjectTerminator terminator )
  {
    return mangleFile(terminator.getDomain()) + "__" + terminator.getName();
  }

  public static String mangleFile ( final ProjectTerminatorService service )
  {
    return mangleFile(service.getTerminator()) + "__" + service.getName();
  }


  public static String mangleName ( final AttributeDeclaration attribute )
  {
    return "masla_" + attribute.getName();
  }

  public static String mangleName ( final StructureElement attribute )
  {
    return "masla_" + attribute.getName();
  }

  public static String mangleName ( final Domain domain )
  {
    return "masld_" + domain.getName();
  }

  public static String mangleName ( final Service service )
  {
    return "masls" + (service.getOverloadNo() > 0 ? "_overload" + service.getOverloadNo() : "") + "_" + service.getName();
  }

  public static String mangleName ( final EnumerateItem enumItem )
  {
    return "masle_" + enumItem.getName();
  }

  public static String mangleName ( final EventDeclaration event )
  {
    return "maslev_" + event.getName();
  }

  public static String mangleName ( final ExceptionDeclaration exception )
  {
    return "maslex_" + exception.getName();
  }

  public static String mangleName ( final ObjectDeclaration object )
  {
    return "maslo_" + object.getName();
  }

  public static String mangleName ( final DomainTerminator terminator )
  {
    return "maslb_" + terminator.getName();
  }

  public static String mangleName ( final ParameterDefinition param )
  {
    return "maslp_" + param.getName();
  }


  public static String mangleName ( final Project project )
  {
    return "maslp_" + project.getProjectName();
  }

  public static String mangleName ( final State state )
  {
    return "maslst_" + state.getName();
  }

  public static String mangleName ( final TypeDeclaration type )
  {
    return "maslt_" + type.getName();
  }


  public static String mangleName ( final VariableDefinition variable )
  {
    return "maslv_" + variable.getName();
  }


}
