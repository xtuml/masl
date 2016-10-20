//
// File: ObjectServiceTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.main.object;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.code.CodeTranslator;


public class ObjectServiceTranslator
{

  private final DomainTranslator domainTranslator;

  private final Expression       serviceId;


  public static ObjectServiceTranslator getInstance ( final ObjectService service )
  {
    return ObjectTranslator.getInstance(service.getParentObject()).getServiceTranslator(service);
  }

  ObjectServiceTranslator ( final ObjectTranslator objectTranslator, final ObjectService service, final Expression serviceId )
  {
    domainTranslator = objectTranslator.getDomainTranslator();
    this.service = service;
    this.objectTranslator = objectTranslator;
    this.serviceId = serviceId;

    function = objectTranslator.getMain().addService(service);

    function.setReturnType(domainTranslator.getTypes().getType(service.getReturnType()));

    scope = new Scope(objectTranslator);
    scope.setObjectService(service);

    for ( final ParameterDefinition param : service.getParameters() )
    {
      final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
      parameters.add(paramTrans);
      scope.addParameter(param, paramTrans.getVariable().asExpression());
    }

  }

  public CodeTranslator getCodeTranslator ()
  {
    return codeTranslator;
  }

  public List<ParameterTranslator> getParameters ()
  {
    return parameters;
  }

  public Function getFunction ()
  {
    return function;
  }

  Scope getScope ()
  {
    return scope;
  }


  void translate ()
  {
    final CodeFile file;

    if ( service.getCode() != null || service.getDeclarationPragmas().hasPragma("generated_code") )
    {
      file = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(service));
    }
    else
    {
      file = domainTranslator.getNativeStubs();
    }

    file.addFunctionDefinition(function);

    if ( service.getCode() != null )
    {
      codeTranslator = CodeTranslator.createTranslator(service.getCode(), scope);

      function.getCode().appendStatement(codeTranslator.getFullCode());
    }
  }

  public ObjectService getService ()
  {
    return service;
  }

  private final List<ParameterTranslator> parameters     = new ArrayList<ParameterTranslator>();

  private final Function                  function;

  private final Scope                     scope;

  private final ObjectService             service;

  private CodeTranslator                  codeTranslator = null;

  private final ObjectTranslator          objectTranslator;


  public ObjectTranslator getObjectTranslator ()
  {
    return objectTranslator;
  }

  public Expression getServiceId ()
  {
    return serviceId;
  }

}
