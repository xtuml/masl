//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.stacktrack;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;
import org.xtuml.masl.translate.main.object.StateActionTranslator;


public class ObjectTranslator
{

  public ObjectTranslator ( final ObjectDeclaration object )
  {
    for ( final ObjectService service : object.getServices() )
    {
      serviceTranslators.add(new ActionTranslator(ObjectServiceTranslator.getInstance(service)));
    }

    for ( final State state : object.getStates() )
    {
      stateTranslators.add(new ActionTranslator(StateActionTranslator.getInstance(state)));
    }

  }


  void translate ()
  {

    for ( final ActionTranslator serviceTranslator : serviceTranslators )
    {
      serviceTranslator.translate();
    }
    for ( final ActionTranslator stateTranslator : stateTranslators )
    {
      stateTranslator.translate();
    }

  }


  private final List<ActionTranslator> serviceTranslators = new ArrayList<ActionTranslator>();
  private final List<ActionTranslator> stateTranslators   = new ArrayList<ActionTranslator>();

}
