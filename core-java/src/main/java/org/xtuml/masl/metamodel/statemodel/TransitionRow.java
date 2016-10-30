//
// File: TransitionRow.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.statemodel;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;


public interface TransitionRow
    extends ASTNode
{

  State getInitialState ();

  List<? extends TransitionOption> getOptions ();

  TransitionOption getOption ( EventDeclaration event );
}
