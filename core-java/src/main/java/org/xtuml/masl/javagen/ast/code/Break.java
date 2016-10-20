//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;


public interface Break
    extends Statement
{

  void setReferencedLabel ( LabeledStatement label );

  LabeledStatement getReferencedLabel ();
}
