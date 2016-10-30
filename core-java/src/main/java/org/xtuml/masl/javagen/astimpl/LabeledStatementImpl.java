//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Statement;


public class LabeledStatementImpl extends StatementImpl
    implements org.xtuml.masl.javagen.ast.code.LabeledStatement
{

  public LabeledStatementImpl ( final ASTImpl ast, final String name, final StatementImpl statement )
  {
    super(ast);

  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public void setName ( final String name )
  {
    this.name = name;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLabeledStatement(this, p);
  }

  @Override
  public StatementImpl getStatement ()
  {
    return statement.get();
  }

  @Override
  public void setStatement ( final Statement statement )
  {
    this.statement.set((StatementImpl)statement);
  }


  private final ChildNode<StatementImpl> statement = new ChildNode<StatementImpl>(this);
  private String                         name;
}
