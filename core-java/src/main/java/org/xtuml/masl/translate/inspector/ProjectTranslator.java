//
// File: MetaDataTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.inspector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;


@Alias("Inspector")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator
{

  private final org.xtuml.masl.translate.main.ProjectTranslator mainTranslator;


  private final Namespace                                      projectNamespace;

  public static ProjectTranslator getInstance ( final Project project )
  {
    return getInstance(ProjectTranslator.class, project);
  }

  public Namespace getNamespace ()
  {
    return projectNamespace;
  }

  private ProjectTranslator ( final Project project )
  {
    super(project);
    mainTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
    this.projectNamespace = new Namespace(Mangler.mangleName(project), Inspector.inspectorNamespace);
    this.library = new SharedLibrary(project.getProjectName() + "_inspector").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
  }

  /**
   *
   * @return
   * @see org.xtuml.masl.translate.Translator#getPrerequisites()
   */
  @Override
  public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.ProjectTranslator>asList(mainTranslator);
  }

  @Override
  public void translate ()
  {

    library.addDependency(Inspector.library);

    addRegistration();
    final Class actionPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.actionHandlerClass));

    for ( final ProjectDomain domain : project.getDomains() )
    {
      final Namespace domainNamespace = new Namespace(Mangler.mangleName(domain.getDomain()), projectNamespace);
      final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain.getDomain());
      for ( final ProjectTerminator terminator : domain.getTerminators() )
      {
        final Namespace termNamespace = new Namespace(Mangler.mangleName(terminator.getDomainTerminator()), domainNamespace);
        final org.xtuml.masl.translate.main.TerminatorTranslator mainTerminatorTranslator = org.xtuml.masl.translate.main.TerminatorTranslator.getInstance(terminator.getDomainTerminator());

        final Expression termHandler = Inspector.getTerminatorHandler(mainDomainTranslator.getDomainId(),
                                                                      mainTerminatorTranslator.getTerminatorId());

        for ( final ProjectTerminatorService service : terminator.getServices() )
        {
          final TerminatorServiceTranslator mainTranslator = TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService());
          final ActionTranslator trans = new ActionTranslator(service, this, termNamespace);
          trans.translate();
          final CodeBlock registerBlock = new CodeBlock();
          final Statement ifOverridden = new IfStatement(mainTranslator.getCheckOverride().asFunctionCall(), registerBlock);

          registerBlock.appendStatement(new Function("overrideServiceHandler").asFunctionCall(termHandler,
                                                                                              false,
                                                                                              mainTranslator.getServiceId(),
                                                                                              actionPtrType
                                                                                                           .callConstructor(new NewExpression(new TypeUsage(trans.getHandlerClass()))))
                                                                              .asStatement());

          initialisationCode.appendStatement(ifOverridden);

        }
      }

      Set<Domain> fullDomains = project.getDomains().stream().map(d->d.getDomain()).collect(Collectors.toSet());
      Set<Domain> interfaceDomains = project.getDomains().stream().flatMap(d->d.getDomain().getReferencedInterfaces().stream()).collect(Collectors.toSet());
      interfaceDomains.removeAll(fullDomains);    
      
      for ( final Domain interfaceDomain : interfaceDomains )
      {
        getLibrary().addDependency(DomainTranslator.getInstance(interfaceDomain).getInterfaceLibrary());
      }

      for ( final Domain fullDomain : fullDomains )
      {
        getLibrary().addDependency(DomainTranslator.getInstance(fullDomain).getLibrary());
      }
    }

  }


  private void addRegistration ()
  {
    codeFile = library.createBodyFile("Inspector" + Mangler.mangleFile(project));

    final Namespace namespace = new Namespace("");

    final Function initInspector = new Function("initProcessInspector", namespace);
    initInspector.setReturnType(new TypeUsage(FundamentalType.BOOL));


    initInspector.getCode().appendStatement(initialisationCode);
    initInspector.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
    codeFile.addFunctionDefinition(initInspector);

    final Variable initialised = new Variable(new TypeUsage(FundamentalType.BOOL),
                                              "initialised",
                                              namespace,
                                              initInspector.asFunctionCall());
    codeFile.addVariableDefinition(initialised);

  }

  private final StatementGroup initialisationCode = new StatementGroup();

  public Library getLibrary ()
  {
    return library;
  }

  private CodeFile        codeFile;
  private final Library library;

  public org.xtuml.masl.translate.main.ProjectTranslator getMainTranslator ()
  {
    return mainTranslator;
  }

  public CodeFile getCodeFile ()
  {
    return codeFile;
  }

}
