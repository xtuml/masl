//
// File: BuiltinException.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.exception;



public interface BuiltinException
    extends ExceptionReference
{

  enum Type
  {
    PROGRAM_ERROR,
    DEADLOCK_ERROR,
    STORAGE_ERROR,
    CONSTRAINT_ERROR,
    RELATIONSHIP_ERROR,
    REFERENTIAL_ACCESS_ERROR,
    IOP_ERROR,
    IO_ERROR,
    OTHER_ERROR
  }

  Type getType ();

}
