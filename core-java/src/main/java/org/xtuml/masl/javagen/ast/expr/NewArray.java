//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import java.util.List;

import org.xtuml.masl.javagen.ast.types.Type;


public interface NewArray
    extends Expression
{

  Type getType ();

  List<? extends Expression> getDimensionSizes ();

  int getNoDimensions ();

  ArrayInitializer getInitialValue ();

  void setNoDimensions ( int noDimensions );

  ArrayInitializer setInitialValue ( ArrayInitializer initialValue );

  Type setType ( final Type type );

  Expression addDimensionSize ( Expression dimensionSize );
}
