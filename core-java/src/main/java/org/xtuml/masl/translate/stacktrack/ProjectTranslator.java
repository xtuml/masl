//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.stacktrack;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;



@Alias("StackTrack")
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
  }


  /**
   * 
   * @return
   * @see org.xtuml.masl.translate.Translator#getPrerequisites()
   */
  @Override
  public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.ProjectTranslator>asList(mainProjectTranslator);
  }

  @Override
  public void translate ()
  {
    for ( final ProjectDomain domain : project.getDomains() )
    {
      for ( final ProjectTerminator terminator : domain.getTerminators() )
      {
        for ( final ProjectTerminatorService service : terminator.getServices() )
        {
          final ActionTranslator serviceTranslator = new ActionTranslator(mainProjectTranslator.getServiceTranslator(service));
          serviceTranslator.translate();
        }
      }
    }
  }

  ObjectTranslator getObjectTranslator ( final ObjectDeclaration object )
  {
    return objectTranslators.get(object);
  }

  Map<ObjectDeclaration, ObjectTranslator>                     objectTranslators = new HashMap<ObjectDeclaration, ObjectTranslator>();

  private final org.xtuml.masl.translate.main.ProjectTranslator mainProjectTranslator;

}
