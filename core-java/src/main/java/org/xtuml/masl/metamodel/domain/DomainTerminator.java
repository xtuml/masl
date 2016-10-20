//
// File: Terminator.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.domain;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;


public interface DomainTerminator
    extends ASTNode
{

  PragmaList getPragmas ();

  Domain getDomain ();

  String getKeyLetters ();

  String getName ();

  List<? extends DomainTerminatorService> getServices ();

  String getComment ();

}
