//
// File: ActionTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FunctionObjectCall;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.translate.main.code.CodeTranslator;



public class TerminatorServiceTranslator
{

  final DomainTranslator     domainTranslator;
  final TerminatorTranslator terminatorTranslator;
  final Function             domainFunction;

  public static TerminatorServiceTranslator getInstance ( final DomainTerminatorService service )
  {
    return TerminatorTranslator.getInstance(service.getTerminator()).getServiceTranslator(service);
  }

  public TerminatorServiceTranslator ( final TerminatorTranslator terminatorTranslator,
                                       final DomainTerminatorService service,
                                       final Expression serviceId )
  {
    this.terminatorTranslator = terminatorTranslator;
    this.domainTranslator = DomainTranslator.getInstance(service.getTerminator().getDomain());
    this.service = service;
    this.serviceId = serviceId;
    bodyFile = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(service));
    scope = new Scope();
    scope.setTerminatorService(service);

    final Class mainClass = terminatorTranslator.getMainClass();

    name = Mangler.mangleName(service);
    final DeclarationGroup terminatorServices = terminatorTranslator.getTerminatorServices();
    final DeclarationGroup domainServices = terminatorTranslator.getDomainServices();
    final DeclarationGroup overriddenServices = terminatorTranslator.getOverriddenServices();
    final DeclarationGroup overrideChecks = terminatorTranslator.getOverrideChecks();
    final DeclarationGroup serviceRegistration = terminatorTranslator.getServiceRegistration();

    // Create the forwarder function and the domain definition function.
    function = mainClass.createStaticFunction(terminatorServices, name, Visibility.PUBLIC);
    domainFunction = mainClass.createStaticFunction(domainServices, "domain_" + name, Visibility.PRIVATE);

    final List<Expression> forwardArgs = new ArrayList<Expression>();

    for ( final ParameterDefinition param : service.getParameters() )
    {
      final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
      parameters.add(paramTrans);
      scope.addParameter(param, paramTrans.getVariable().asExpression());

      domainFunction.createParameter(paramTrans.getVariable().getType(), paramTrans.getVariable().getName());
      forwardArgs.add(paramTrans.getVariable().asExpression());
    }


    // Add override member
    final Class overriderType = Architecture.createFunctionOverrider(new TypeUsage(domainFunction.asFunctionPointerType()));
    final Variable overrider = mainClass.createMemberVariable(overriddenServices,
                                                              "override_" + name,
                                                              new TypeUsage(overriderType),
                                                              Visibility.PRIVATE);
    terminatorTranslator.getConstructor().setInitialValue(overrider, domainFunction.asFunctionPointer());
    final Expression overrideMember = overrider.asMemberReference(terminatorTranslator.getGetInstance(), false);

    // Add main function to forward to the override member
    final Statement overrideCall = new FunctionObjectCall(new Function("getFunction").asFunctionCall(overrideMember, false),
                                                          forwardArgs).asStatement();
    function.getCode().appendStatement(overrideCall);
    bodyFile.addFunctionDefinition(function);

    // Add override registration function
    registerOverride = mainClass.createStaticFunction(serviceRegistration, "register_" + name, Visibility.PUBLIC);
    registerOverride.setReturnType(new TypeUsage(FundamentalType.BOOL));
    final Variable param = registerOverride.createParameter(new TypeUsage(overriderType.referenceNestedType("FunctionPtr")),
                                                            "override");
    final Statement registerCall = new Function("override").asFunctionCall(overrideMember, false, param.asExpression())
                                                           .asStatement();
    registerOverride.getCode().appendStatement(registerCall);
    registerOverride.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
    bodyFile.addFunctionDefinition(registerOverride);

    // Add function to check whether the service has been overridden
    checkOverride = mainClass.createStaticFunction(overrideChecks, "overriden_" + name, Visibility.PUBLIC);
    checkOverride.setReturnType(new TypeUsage(FundamentalType.BOOL));
    final Expression checkCall = new Function("isOverridden").asFunctionCall(overrideMember, false);
    checkOverride.getCode().appendStatement(new ReturnStatement(checkCall));
    bodyFile.addFunctionDefinition(checkOverride);


  }


  public Function getRegisterOverride ()
  {
    return registerOverride;
  }

  public Function getCheckOverride ()
  {
    return checkOverride;
  }

  private final Expression serviceId;

  public CodeTranslator getCodeTranslator ()
  {
    return code;
  }

  public Function getFunction ()
  {
    return function;
  }

  public List<ParameterTranslator> getParameters ()
  {
    return parameters;
  }

  public Scope getScope ()
  {
    return scope;
  }

  void translateCode ()
  {
    final CodeFile file;

    if ( service.getCode() != null || service.getDeclarationPragmas().hasPragma("generated_code") )
    {
      file = bodyFile;
    }
    else
    {
      file = domainTranslator.getNativeStubs();
    }
    file.addFunctionDefinition(domainFunction);

    // Code may be null if no implementation file was provided, eg for native
    // functions. In this case leave definition to third party library.
    if ( service.getCode() != null )
    {
      code = CodeTranslator.createTranslator(service.getCode(), scope);
      domainFunction.getCode().appendStatement(code.getFullCode());
    }
  }

  public DomainTerminatorService getService ()
  {
    return service;
  }


  public Expression getServiceId ()
  {
    return serviceId;
  }

  private final DomainTerminatorService   service;

  private final Scope                     scope;

  private final List<ParameterTranslator> parameters = new ArrayList<ParameterTranslator>();

  private final Function                  function;


  private CodeTranslator                  code;

  private final CodeFile                  bodyFile;

  private final String                    name;
  private final Function                  registerOverride;
  private final Function                  checkOverride;

  public DomainTranslator getDomainTranslator ()
  {
    return domainTranslator;
  }

  public TerminatorTranslator getTerminatorTranslator ()
  {
    return terminatorTranslator;
  }


}
