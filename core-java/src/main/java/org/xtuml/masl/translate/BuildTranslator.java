//
// File: BuildTranslator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.io.File;

import org.xtuml.masl.translate.build.BuildSet;


@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".Translator", value = "")
public abstract class BuildTranslator
{

  public abstract void translate ( BuildSet buildSet, File sourceDirectory );

  public abstract void translateBuild ( Translator<?> parent, File sourceDirectory );

  public abstract void dump ( File directory );
}
