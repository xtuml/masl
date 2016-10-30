//
// File: PragmaList.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.common;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;


public interface PragmaList
extends ASTNode
{

  static public final String INDEX  = "index";
  static public final String SCOPE  = "scope";
  static public final String NUMBER = "number";

  List<String> getPragmaValues ( String name );

  List<String> getPragmaValues ( String name, boolean allowValueList );

  List<? extends PragmaDefinition> getPragmas ();

  String getValue ( String name );

  boolean hasValue ( String name );

  boolean hasPragma ( String name );

}
