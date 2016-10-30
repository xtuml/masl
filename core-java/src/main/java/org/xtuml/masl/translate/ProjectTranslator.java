/*
 * Filename : ProjectTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate;

import org.xtuml.masl.metamodel.project.Project;


@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".ProjectTranslator", value = "")
public abstract class ProjectTranslator extends Translator<Project>
{

  public static <T extends ProjectTranslator> T getInstance ( final Class<T> translatorClass, final Project project )
  {
    try
    {
      return getInstance(translatorClass, Project.class, project);
    }
    catch ( final Exception e )
    {
      assert false : e.getMessage();
      return null;
    }
  }

  protected ProjectTranslator ( final Project project )
  {
    this.project = project;
  }

  protected Project project;

  public Project getProject ()
  {
    return project;
  }
}
