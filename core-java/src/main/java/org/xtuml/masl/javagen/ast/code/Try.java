//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.List;

import org.xtuml.masl.javagen.ast.types.Type;


public interface Try
    extends Statement
{

  Catch addCatch ( Catch clause );

  Catch addCatch ( Type type, String name );

  List<? extends Catch> getCatches ();

  CodeBlock getFinallyBlock ();

  CodeBlock getMainBlock ();

  CodeBlock setFinallyBlock ( CodeBlock code );

  CodeBlock setMainBlock ( CodeBlock code );

}
