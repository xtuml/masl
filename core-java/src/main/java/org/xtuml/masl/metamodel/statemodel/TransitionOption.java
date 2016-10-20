//
// File: TransitionOption.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.statemodel;

import org.xtuml.masl.metamodel.ASTNode;


public interface TransitionOption
    extends ASTNode
{

  EventDeclaration getEvent ();

  TransitionType getType ();

  State getDestinationState ();
}
