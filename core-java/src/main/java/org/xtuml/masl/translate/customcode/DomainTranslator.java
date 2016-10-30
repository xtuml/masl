//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.customcode;

import java.io.File;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.BuildSet;



@Alias("CustomCode")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  private final BuildSet buildSet;

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    buildSet = BuildSet.getBuildSet(domain);
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
