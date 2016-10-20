//
// File: UserDefinedException.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.exception;

public interface UserDefinedException
    extends ExceptionReference
{

  ExceptionDeclaration getException ();
}
