//
// File: TypeDefinition.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.expression.Expression;


public interface TypeDefinition
    extends ASTNode
{

  public enum ActualType
  {
    ANY_INSTANCE,
    BOOLEAN,
    CHARACTER,
    DEVICE,
    DURATION,
    EVENT,
    BYTE,
    INTEGER,
    REAL,
    SMALL_INTEGER,
    STRING,
    TIMER,
    TIMESTAMP,
    WCHARACTER,
    WSTRING,
    BAG,
    SET,
    ARRAY,
    SEQUENCE,
    INSTANCE,
    USER_DEFINED,
    UNCONSTRAINED_ARRAY_SUBTYPE,
    UNCONSTRAINED_ARRAY,
    CONSTRAINED,
    STRUCTURE,
    ENUMERATE,
    DICTIONARY
  }

  ActualType getActualType ();

  TypeDefinition getDefinedType ();

  Expression getMinValue ();

  Expression getMaxValue ();

  TypeDeclaration getTypeDeclaration ();


  boolean isNumeric ();

  boolean isCollection ();

  boolean isString ();

  boolean isCharacter ();

}
