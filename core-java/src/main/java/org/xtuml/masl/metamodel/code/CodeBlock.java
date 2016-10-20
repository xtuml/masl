//
// File: CodeBlock.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import java.util.List;


/**
 * Represents a MASL code block. A code block may either be the top level block
 * for a service or state action, or may be a statement within another code
 * block. A code block may define a number of variables available to the
 * statements within the block, and a number of exception handlers to handle any
 * exceptions thrown by statements within the block.
 */
public interface CodeBlock
    extends Statement
{

  /**
   * Gets the statements to be executed for this code block
   * 
   * @return the statements to be executed
   */
  List<? extends Statement> getStatements ();

  /**
   * Gets the variables defined by this code block
   * 
   * @return the variables defined
   */
  List<? extends VariableDefinition> getVariables ();

  /**
   * Gets the exception handlers defined by this code block
   * 
   * @return the variables defined
   */
  List<? extends ExceptionHandler> getExceptionHandlers ();
}
