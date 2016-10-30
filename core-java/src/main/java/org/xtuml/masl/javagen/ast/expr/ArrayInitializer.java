//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import java.util.List;


public interface ArrayInitializer
    extends Expression
{

  Expression addElement ( Expression element );

  List<? extends Expression> getElements ();
}
