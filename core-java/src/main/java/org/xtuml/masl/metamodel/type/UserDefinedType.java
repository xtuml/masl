//
// File: UserDefinedType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.domain.Domain;


public interface UserDefinedType
    extends BasicType
{

  Domain getDomain ();

  String getName ();

}
