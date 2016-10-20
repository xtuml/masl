//
// File: ReturnStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;



public class PragmaStatement extends Statement
    implements org.xtuml.masl.metamodel.code.PragmaStatement
{

  public PragmaStatement ()
  {
    super(null);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPragmaStatement(this, p);
  }


}
