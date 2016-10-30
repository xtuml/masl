//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.types;

import org.xtuml.masl.javagen.ast.expr.ClassLiteral;


public interface PrimitiveType
    extends Type
{

  enum Tag
  {
    BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID
  }

  Tag getTag ();

  @Override
  ClassLiteral clazz ();

}
