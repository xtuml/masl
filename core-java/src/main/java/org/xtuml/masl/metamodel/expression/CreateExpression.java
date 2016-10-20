//
// File: CreateExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;


public interface CreateExpression
    extends Expression
{

  interface AttributeValue
      extends ASTNode
  {

    AttributeDeclaration getAttribute ();

    Expression getValue ();
  }

  ObjectDeclaration getObject ();

  List<? extends AttributeValue> getAggregate ();

  State getState ();

}
