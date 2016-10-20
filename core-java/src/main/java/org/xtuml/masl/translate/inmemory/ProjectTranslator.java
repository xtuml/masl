//
// File: ProjectTranslator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.inmemory;

import java.util.Arrays;
import java.util.Collection;

import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;

import com.google.common.collect.Iterables;


@Alias("InMemory")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator
{

  public static ProjectTranslator getInstance ( final Project project )
  {
    return getInstance(ProjectTranslator.class, project);
  }

  private ProjectTranslator ( final Project project )
  {
    super(project);
    mainProjectTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
    executable = new Executable(project.getProjectName() + "_transient").inBuildSet(mainProjectTranslator.getBuildSet()).withCCDefaultExtensions();
  }

  @Override
  public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.ProjectTranslator>asList(mainProjectTranslator);
  }

  @Override
  public void translate ()
  {
    if ( Iterables.isEmpty(executable.getBodyFiles()) )
    {
      // Some build systems complain if there's not at least one file in an executable
      executable.createBodyFile("transient_dummy");
    }

       executable.addDependency(mainProjectTranslator.getLibrary());

    for ( final ProjectDomain domain : project.getDomains() )
    {
      executable.addDependency(DomainTranslator.getInstance(domain.getDomain()).getLibrary());
    }
  }

  private final org.xtuml.masl.translate.main.ProjectTranslator mainProjectTranslator;
  private final Library                                      executable;


  public Library getExecutable ()
  {
    return executable;
  }

  public org.xtuml.masl.translate.main.ProjectTranslator getMainProjectTranslator ()
  {
    return mainProjectTranslator;
  }

}
