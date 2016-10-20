//
// File: TerminatorTranslator.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.EnumerationType;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.cppgen.EnumerationType.Enumerator;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;


public class TerminatorTranslator
{

  public TerminatorTranslator ( final DomainTerminator terminator, final org.xtuml.masl.cppgen.Expression terminatorId )
  {
    domainTranslator = DomainTranslator.getInstance(terminator.getDomain());
    this.terminator = terminator;
    this.terminatorId = terminatorId;

    headerFile = domainTranslator.getTerminatorsHeaderFile();
    bodyFile = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(terminator));

    namespace = DomainNamespace.get(terminator.getDomain());

    name = Mangler.mangleName(terminator);
    mainClass = new Class(name, namespace);


    terminatorServices = mainClass.createDeclarationGroup("Terminator Services");
    serviceRegistration = mainClass.createDeclarationGroup("Service Registration");
    overrideChecks = mainClass.createDeclarationGroup("Override Checks");

    singletonGroup = mainClass.createDeclarationGroup("Singleton");
    domainServices = mainClass.createDeclarationGroup("Domain Defined Services");
    overriddenServices = mainClass.createDeclarationGroup("Overriden Services");
    idEnums = mainClass.createDeclarationGroup("Id Enumerations");

    headerFile.addClassDeclaration(mainClass);

    addGetInstance();
    constructor = mainClass.createConstructor(singletonGroup, Visibility.PRIVATE);
    bodyFile.addFunctionDefinition(constructor);

  }

  void addGetInstance ()
  {
    getInstance = mainClass.createStaticFunction(singletonGroup, "getInstance", Visibility.PRIVATE);
    getInstance.setReturnType(new TypeUsage(mainClass, TypeUsage.Reference));
    final Variable instance = new Variable(new TypeUsage(mainClass), "instance");
    instance.setStatic(true);

    getInstance.getCode().appendStatement(instance.asStatement());
    getInstance.getCode().appendStatement(new ReturnStatement(instance.asExpression()));
    bodyFile.addFunctionDefinition(getInstance);
  }

  Function getConstructor ()
  {
    return constructor;
  }

  private Function                              getInstance;
  private final CodeFile                        bodyFile;
  private final DeclarationGroup                terminatorServices;
  private final DeclarationGroup                serviceRegistration;
  private final DeclarationGroup                domainServices;
  private final DeclarationGroup                overriddenServices;
  private final DeclarationGroup                overrideChecks;
  private final DeclarationGroup                singletonGroup;
  private final DeclarationGroup                idEnums;
  private final CodeFile                        headerFile;
  private final Function                        constructor;


  private final String                          name;
  private final Class                           mainClass;
  private final Namespace                       namespace;

  private final org.xtuml.masl.cppgen.Expression terminatorId;

  public org.xtuml.masl.cppgen.Expression getTerminatorId ()
  {
    return terminatorId;
  }

  public void translate ()
  {
    addServices();
  }


  public void translateCode ()
  {
    translateServiceCode();
  }

  private void translateServiceCode ()
  {
    for ( final DomainTerminatorService service : terminator.getServices() )
    {
      serviceTranslators.get(service).translateCode();
    }
  }


  private void addServices ()
  {
    final EnumerationType servicesEnum = new EnumerationType("ServiceIds");
    mainClass.addEnumeration(idEnums, servicesEnum, Visibility.PUBLIC);

    for ( final DomainTerminatorService service : terminator.getServices() )
    {
      final Enumerator serviceId = servicesEnum.addEnumerator("serviceId_" + Mangler.mangleName(service), null);

      final TerminatorServiceTranslator translator = new TerminatorServiceTranslator(this, service, serviceId.asExpression());
      serviceTranslators.put(service, translator);
    }
  }

  public TerminatorServiceTranslator getServiceTranslator ( final DomainTerminatorService service )
  {
    return serviceTranslators.get(service);
  }


  private final Map<DomainTerminatorService, TerminatorServiceTranslator> serviceTranslators = new HashMap<DomainTerminatorService, TerminatorServiceTranslator>();
  private final DomainTranslator                                          domainTranslator;
  private final DomainTerminator                                          terminator;

  public DomainTranslator getDomainTranslator ()
  {
    return domainTranslator;
  }

  public static TerminatorTranslator getInstance ( final DomainTerminator terminator )
  {
    return DomainTranslator.getInstance(terminator.getDomain()).getTerminatorTranslator(terminator);
  }

  public CodeFile getBodyFile ()
  {
    return bodyFile;
  }

  public CodeFile getHeaderFile ()
  {
    return headerFile;
  }

  public Class getMainClass ()
  {
    return mainClass;
  }

  DeclarationGroup getTerminatorServices ()
  {
    return terminatorServices;
  }

  DeclarationGroup getServiceRegistration ()
  {
    return serviceRegistration;
  }

  DeclarationGroup getOverriddenServices ()
  {
    return overriddenServices;
  }

  DeclarationGroup getDomainServices ()
  {
    return domainServices;
  }

  Expression getGetInstance ()
  {
    return getInstance.asFunctionCall();
  }

  public DeclarationGroup getOverrideChecks ()
  {
    return overrideChecks;
  }

}
