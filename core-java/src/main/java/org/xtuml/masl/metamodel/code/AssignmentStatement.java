//
// File: AssignmentStatement.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.expression.Expression;


/**
 * Represents a MASL assignment statement. e.g. <code>my_age := 37;</code>
 */
public interface AssignmentStatement
    extends Statement
{

  /**
   * Returns an {@link org.xtuml.masl.metamodel.expression.Expression Expression}
   * representing the target of the assignment.
   * 
   * @return the target to be assigned a value
   */
  Expression getTarget ();

  /**
   * Returns an {@link org.xtuml.masl.metamodel.expression.Expression Expression}
   * representing the value to be assigned to the target.
   * 
   * @return the value to be assigned to the target
   */
  Expression getValue ();
}
