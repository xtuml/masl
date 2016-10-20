//
// File: TransitionTable.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.statemodel;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;


public interface TransitionTable
    extends ASTNode
{

  boolean isAssigner ();

  List<? extends TransitionRow> getRows ();

  PragmaList getPragmas ();
}
