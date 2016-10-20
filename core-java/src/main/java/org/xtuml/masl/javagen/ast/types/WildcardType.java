//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;


public interface WildcardType
    extends Type
{

  ReferenceType getSuperBound ();

  ReferenceType getExtendsBound ();

  void setExtendsBound ( ReferenceType extendsBound );

  void setSuperBound ( ReferenceType superBound );
}
