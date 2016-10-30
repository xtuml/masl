/*
 * Filename : PragmaDefinition.java
 * 
 * UK Crown Copyright (c) 2009. All Rights Reserved
 */
package org.xtuml.masl.metamodel.common;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;


public interface PragmaDefinition
    extends ASTNode
{

  public String getName ();

  public List<String> getValues ();
}
