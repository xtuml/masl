//
// File: CharacteristicExpression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.type.BasicType;


public interface CharacteristicExpression
    extends Expression
{

  enum Type
  {
    FIRST,
    FIRSTCHARPOS,
    GET_UNIQUE,
    IMAGE,
    LAST,
    LENGTH,
    LOWER,
    PRED,
    RANGE,
    SUCC,
    UPPER,
    VALUE,

    NOW,

    TIME,
    DATE,

  }

  Type getCharacteristic ();

  Expression getLhs ();

  List<? extends Expression> getArguments ();

  BasicType getLhsType ();
}
