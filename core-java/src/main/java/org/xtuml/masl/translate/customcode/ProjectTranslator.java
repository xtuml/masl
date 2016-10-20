//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.customcode;

import java.io.File;

import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.BuildSet;



@Alias("CustomCode")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator
{

  private final BuildSet buildSet;

  public static ProjectTranslator getInstance ( final Project project )
  {
    return getInstance(ProjectTranslator.class, project);
  }

  private ProjectTranslator ( final Project project )
  {
    super(project);
    buildSet = BuildSet.getBuildSet(project);
  }

  @Override
  public void translate ()
  {
    if ( new XMLParser(buildSet).parse() )
    {
      buildSet.addIncludeDir(new File("../custom"));
      buildSet.addSourceDir(new File("../custom"));
    }
  }
}
