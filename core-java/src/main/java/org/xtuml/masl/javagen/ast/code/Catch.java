//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Parameter;


public interface Catch
    extends ASTNode
{

  CodeBlock getCodeBlock ();

  Parameter getException ();

  void setCodeBlock ( CodeBlock createCodeBlock );

  void setException ( Parameter declaration );
}
