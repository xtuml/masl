//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.inspector;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;


class TerminatorTranslator
{

  private final org.xtuml.masl.translate.main.TerminatorTranslator termTrans;

  private final CodeFile                                          codeFile;
  private final CodeFile                                          headerFile;
  private final Class                                             handlerClass;

  private final Map<DomainTerminatorService, ActionTranslator>    serviceTranslators = new HashMap<DomainTerminatorService, ActionTranslator>();
  private final DomainTranslator                                  domainTranslator;

  private final Namespace                                         namespace;

  public Namespace getNamespace ()
  {
    return namespace;
  }

  TerminatorTranslator ( final DomainTerminator terminator )
  {
    domainTranslator = DomainTranslator.getInstance(terminator.getDomain());
    namespace = new Namespace(Mangler.mangleName(terminator), domainTranslator.getNamespace());

    termTrans = org.xtuml.masl.translate.main.TerminatorTranslator.getInstance(terminator);
    this.codeFile = domainTranslator.getLibrary().createBodyFile("Inspector" + Mangler.mangleFile(terminator));
    this.headerFile = domainTranslator.getLibrary().createPrivateHeader("Inspector" + Mangler.mangleFile(terminator));

    this.handlerClass = new Class(Mangler.mangleName(terminator) + "Handler", namespace);
    handlerClass.addSuperclass(Inspector.terminatorHandlerClass, Visibility.PUBLIC);
    headerFile.addClassDeclaration(handlerClass);

    final DeclarationGroup constructors = handlerClass.createDeclarationGroup("Constructors");

    for ( final DomainTerminatorService service : terminator.getServices() )
    {
      final ActionTranslator trans = new ActionTranslator(service, this);
      serviceTranslators.put(service, trans);
      trans.translate();
    }

    final Function constructor = handlerClass.createConstructor(constructors, Visibility.PUBLIC);

    codeFile.addFunctionDefinition(constructor);


    final Class actionPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.actionHandlerClass));
    final Function registerServiceHandler = new Function("registerServiceHandler");
    for ( final DomainTerminatorService service : terminator.getServices() )
    {
      final ActionTranslator servTrans = serviceTranslators.get(service);
      final Expression id = termTrans.getServiceTranslator(service).getServiceId();
      final Class servHandlerClass = servTrans.getHandlerClass();

      constructor.getCode()
                 .appendExpression(registerServiceHandler.asFunctionCall(id,
                                                                         actionPtrType
                                                                                      .callConstructor(new NewExpression(new TypeUsage(servHandlerClass)))));
    }

  }

  void translate ()
  {
  }

  Class getHandlerClass ()
  {
    return handlerClass;
  }


  public CodeFile getCodeFile ()
  {
    return codeFile;
  }

  public CodeFile getHeaderFile ()
  {
    return headerFile;
  }

  public static TerminatorTranslator getInstance ( final DomainTerminator terminator )
  {
    return DomainTranslator.getInstance(terminator.getDomain()).getTerminatorTranslator(terminator);
  }

}
