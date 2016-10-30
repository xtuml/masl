//
// File: EventDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.statemodel;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface EventDeclaration
    extends ASTNode
{

  enum Type
  {
    NORMAL, CREATION, ASSIGNER
  }

  String getName ();

  List<? extends ParameterDefinition> getParameters ();

  ObjectDeclaration getParentObject ();

  PragmaList getPragmas ();

  Type getType ();

  boolean isScheduled ();
}
