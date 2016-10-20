//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;


public interface LabeledStatement
    extends Statement
{

  void setName ( String name );

  void setStatement ( Statement statement );

  String getName ();

  Statement getStatement ();
}
