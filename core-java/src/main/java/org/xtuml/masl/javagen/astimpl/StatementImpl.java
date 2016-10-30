//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.code.BlockStatement;
import org.xtuml.masl.javagen.ast.code.Statement;


public abstract class StatementImpl extends ASTNodeImpl
    implements BlockStatement, Statement, StatementGroupMember
{

  StatementImpl ( final ASTImpl ast )
  {
    super(ast);
  }

}
