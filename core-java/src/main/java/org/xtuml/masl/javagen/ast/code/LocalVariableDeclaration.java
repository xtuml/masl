//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;


public interface LocalVariableDeclaration
    extends Statement
{

  LocalVariable getLocalVariable ();

  void setLocalVariable ( LocalVariable variable );
}
