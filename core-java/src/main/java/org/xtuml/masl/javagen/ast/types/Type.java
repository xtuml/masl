//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Cast;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewArray;


public interface Type
    extends ASTNode
{

  NewArray newArray ( int noDimensions, ArrayInitializer initialValue );

  NewArray newArray ( int noDimensions, Expression... dimensionSizes );

  Cast cast ( Expression expression );

  ClassLiteral clazz ();

}
