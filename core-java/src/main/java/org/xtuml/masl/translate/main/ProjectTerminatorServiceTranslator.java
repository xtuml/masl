/*
 * Filename : TerminatorServiceTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.main;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.main.code.CodeTranslator;


public class ProjectTerminatorServiceTranslator
{

  private final ProjectTerminatorService  service;
  private final Scope                     scope;
  private final List<ParameterTranslator> parameters = new ArrayList<ParameterTranslator>();
  private CodeTranslator                  code;
  private final Function                  function;
  private final ProjectTranslator         projectTranslator;

  private final CodeFile                  bodyFile;

  public ProjectTerminatorServiceTranslator ( final ProjectTranslator projectTranslator, final ProjectTerminatorService service )
  {
    this.service = service;
    this.projectTranslator = projectTranslator;
    scope = new Scope();
    scope.setProjectTerminatorService(service);

    final ProjectTerminator terminator = service.getTerminator();
    final ProjectDomain domain = terminator.getDomain();
    final Project project = domain.getProject();

    final String filename = Mangler.mangleFile(service);

    bodyFile = projectTranslator.getLibrary().createBodyFile(filename);
    final CodeFile headerFile = projectTranslator.getLibrary().createPrivateHeader(filename);

    final Namespace prjNamespace = projectTranslator.getNamespace();
    final Namespace domainNamespace = new Namespace(Mangler.mangleName(domain.getDomain()), prjNamespace);
    final Namespace termNamespace = new Namespace(Mangler.mangleName(terminator.getDomainTerminator()), domainNamespace);


    function = new Function(Mangler.mangleName(service), termNamespace);
    headerFile.addFunctionDeclaration(function);

    function.setReturnType(Types.getInstance().getType(service.getReturnType()));
    for ( final ParameterDefinition param : service.getParameters() )
    {
      final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
      parameters.add(paramTrans);
      scope.addParameter(param, paramTrans.getVariable().asExpression());
    }


    // Register the project terminator with the domain
    final Function overrider = TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService()).getRegisterOverride();
    final Variable register = new Variable(new TypeUsage(FundamentalType.BOOL),
                                           "register_" + Mangler.mangleName(service),
                                           termNamespace,
                                           overrider.asFunctionCall(function.asFunctionPointer()));
    bodyFile.addVariableDefinition(register);
  }

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
    CodeFile file;
    if ( service.getCode() != null || service.getDeclarationPragmas().hasPragma("generated_code") )
    {
      file = bodyFile;
    }
    else
    {
      file = projectTranslator.getNativeStubs();
    }
    file.addFunctionDefinition(function);

    // Code may be null if no implementation file was provided, eg for native
    // functions. In this case leave definition to third party library.
    if ( service.getCode() != null )
    {
      code = CodeTranslator.createTranslator(service.getCode(), scope);
      function.getCode().appendStatement(code.getFullCode());
    }

  }

  public ProjectTerminatorService getService ()
  {
    return service;
  }

  public static ProjectTerminatorServiceTranslator getInstance ( final ProjectTerminatorService service )
  {
    return ProjectTranslator.getInstance(service.getTerminator().getDomain().getProject()).getServiceTranslator(service);
  }

}
