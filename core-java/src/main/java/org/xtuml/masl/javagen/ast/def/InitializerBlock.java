//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.code.CodeBlock;


public interface InitializerBlock
    extends TypeMember, ASTNode
{

  boolean isStatic ();

  CodeBlock getCodeBlock ();

  CodeBlock setCodeBlock ();

  CodeBlock setCodeBlock ( CodeBlock codeBlock );
}
