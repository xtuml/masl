//
// File: ForStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;


public interface ForStatement
    extends Statement
{

  LoopSpec getLoopSpec ();

  List<? extends Statement> getStatements ();

}
