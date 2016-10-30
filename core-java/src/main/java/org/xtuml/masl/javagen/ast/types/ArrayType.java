//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;



public interface ArrayType
    extends ReferenceType
{

  Type getElementType ();

  void setElementType ( Type elementType );

}
