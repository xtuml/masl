//
// File: LoopSpec.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;


public interface LoopSpec
    extends ASTNode
{

  interface FromToRange
      extends LoopSpec
  {

    Expression getFrom ();

    Expression getTo ();
  }

  interface VariableElements
      extends LoopSpec
  {

    Expression getVariable ();
  }

  interface VariableRange
      extends LoopSpec
  {

    Expression getVariable ();
  }

  interface TypeRange
      extends LoopSpec
  {

    BasicType getType ();
  }

  VariableDefinition getLoopVariableDef ();

  boolean isReverse ();
}
