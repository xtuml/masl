//
// File: InstanceType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.type;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface InstanceType
    extends BasicType
{

  ObjectDeclaration getObjectDeclaration ();
}
