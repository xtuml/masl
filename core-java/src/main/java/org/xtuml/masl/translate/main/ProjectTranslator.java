/*
 * Filename : ProjectTranslator.java
 *
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Main;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.BuildSet;

import com.google.common.base.Suppliers;


/**
 * MASL domains are grouped in to projects to form deployable components. The
 * definition of the domains within a project are held in a project file with a
 * .prj extension.
 *
 * Associated with each project is a set of terminator implementations that
 * should override the default terminator implementation.
 *
 */
@Alias("Main")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator
{

  private final Map<ProjectTerminatorService, ProjectTerminatorServiceTranslator> terminatorServices = new HashMap<ProjectTerminatorService, ProjectTerminatorServiceTranslator>();


  /**
   * The class uses the singleton pattern to only allow one instance of the
   * project translator within the current JVM.
   *
   * @return The translator being used to translate the current project
   *         definition
   * @param project a {@link org.xtuml.masl.metamodel.project.Project} object.
   */
  static public ProjectTranslator getInstance ( final Project project )
  {
    return getInstance(ProjectTranslator.class, project);
  }

  /**
   * Do not allow construction of this class, except through the static
   * getInstance method.
   */
  private ProjectTranslator ( final Project project )
  {
    super(project);

    buildSet = BuildSet.getBuildSet(project);
    library = new SharedLibrary(project.getProjectName()).inBuildSet(buildSet).withCCDefaultExtensions();
    namespace = new Namespace(Mangler.mangleName(project));
    final Function initialiseProcess = new Function("initialiseProcess", namespace);
    initialiseProcess.setReturnType(new TypeUsage(FundamentalType.BOOL));

    final CodeFile bodyFile = library.createBodyFile(Mangler.mangleFile(project));

    final Variable initialised = new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                                              "processInitialised",
                                              namespace,
                                              initialiseProcess.asFunctionCall());
    bodyFile.addFunctionDefinition(initialiseProcess);
    bodyFile.addVariableDefinition(initialised);

    initialiseProcess.getCode()
                     .appendStatement(new Function("setProjectName").asFunctionCall(Architecture.process,
                                                                                    false,
                                                                                    Literal.createStringLiteral(project.getProjectName()))
                                                                    .asStatement());
    initialiseProcess.getCode().appendStatement(new ReturnStatement(Literal.TRUE));

    final Main main = new Main();
    bodyFile.addFunctionDefinition(main);
    main.getCode().appendStatement(new ReturnStatement(Architecture.main.asFunctionCall(main.getArgc(), main.getArgv())));
  }

  /**
   * Translate the specified project into the required c++ code.
   */
  @Override
  public void translate ()
  {
    translateTerminatorCode();
    
    Set<Domain> fullDomains = project.getDomains().stream().map(d->d.getDomain()).collect(Collectors.toSet());
    Set<Domain> interfaceDomains = project.getDomains().stream().flatMap(d->d.getDomain().getReferencedInterfaces().stream()).collect(Collectors.toSet());
    interfaceDomains.removeAll(fullDomains);	  
	  
    for ( final Domain interfaceDomain : interfaceDomains )
    {
      getLibrary().addDependency(DomainTranslator.getInstance(interfaceDomain).getInterfaceLibrary());
    }

  }

  /**
   * Iterate around the set of terminator services defined for the project and
   * generate the required set of new implementations for these services. Each
   * service is translated into a static method on the generated project class.
   */
  private void translateTerminatorCode ()
  {
    for ( final ProjectDomain domain : project.getDomains() )
    {
      final DomainTranslator domainTranslator = DomainTranslator.getInstance(domain.getDomain());
      domainTranslator.translate();

      for ( final ProjectTerminator terminator : domain.getTerminators() )
      {
        for ( final ProjectTerminatorService service : terminator.getServices() )
        {
          final ProjectTerminatorServiceTranslator translator = new ProjectTerminatorServiceTranslator(this, service);
          translator.translateCode();
          terminatorServices.put(service, translator);

        }

      }
    }

  }

  @Override
  public Project getProject ()
  {
    return project;
  }

  private final BuildSet  buildSet;
  private final Namespace namespace;

  public BuildSet getBuildSet ()
  {
    return buildSet;
  }

  public ProjectTerminatorServiceTranslator getServiceTranslator ( final ProjectTerminatorService service )
  {
    return terminatorServices.get(service);
  }

  public Namespace getNamespace ()
  {
    return namespace;
  }

  final Library library;


  public Library getLibrary ()
  {
    return library;
  }

  public static final String  NativeStubsFile     = "NativeStubs.cc";

  public CodeFile getNativeStubs ()
  {
    return Suppliers.memoize( () -> new Library("native").inBuildSet(buildSet).createBodyFile(NativeStubsFile) ).get();
  }
}
