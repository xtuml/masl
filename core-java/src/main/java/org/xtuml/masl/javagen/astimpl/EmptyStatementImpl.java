//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.EmptyStatement;


public class EmptyStatementImpl extends StatementImpl
    implements EmptyStatement
{

  EmptyStatementImpl ( final ASTImpl ast )
  {
    super(ast);
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEmptyStatement(this, p);
  }

}
