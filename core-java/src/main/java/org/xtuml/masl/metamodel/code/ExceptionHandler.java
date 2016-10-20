//
// File: ExceptionHandler.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.exception.ExceptionReference;


public interface ExceptionHandler
    extends ASTNode
{

  int getLineNumber ();

  ExceptionReference getException ();

  List<? extends Statement> getCode ();
}
