//
// File: Statement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;


public interface Statement
    extends ASTNode
{

  String toAbbreviatedString ();

  int getLineNumber ();

  PragmaList getPragmas ();

  List<? extends Statement> getChildStatements ();

  Statement getParentStatement ();

  boolean inExceptionHandler();
}
