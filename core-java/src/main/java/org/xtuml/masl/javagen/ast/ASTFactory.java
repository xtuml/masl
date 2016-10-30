//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast;


public class ASTFactory
{

  public static AST createAST ()
  {
    return new org.xtuml.masl.javagen.astimpl.ASTImpl();
  }
}
