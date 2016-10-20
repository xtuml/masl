//
// File: FindExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.type.InstanceType;


public interface FindExpression
    extends Expression
{

  public enum Type
  {
    FIND, FIND_ONE, FIND_ONLY
  }

  Type getFindType ();

  Expression getCollection ();

  Expression getSkeleton ();

  List<? extends Expression> getArguments ();

  InstanceType getInstanceType ();
}
