//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.stacktrack;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;


public class TerminatorTranslator
{

  public TerminatorTranslator ( final DomainTerminator term )
  {
    for ( final DomainTerminatorService service : term.getServices() )
    {
      serviceTranslators.add(new ActionTranslator(TerminatorServiceTranslator.getInstance(service)));
    }

  }


  void translate ()
  {

    for ( final ActionTranslator serviceTranslator : serviceTranslators )
    {
      serviceTranslator.translate();
    }
  }


  private final List<ActionTranslator> serviceTranslators = new ArrayList<ActionTranslator>();

}
