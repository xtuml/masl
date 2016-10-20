//
// File: InstanceOrderingExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.object.AttributeDeclaration;


public interface InstanceOrderingExpression
    extends Expression
{

  Expression getCollection ();

  boolean isReverse ();

  List<? extends Component> getOrder ();

  interface Component
  {

    boolean isReverse ();

    AttributeDeclaration getAttribute ();
  }
}
