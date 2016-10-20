//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.BlockStatement;
import org.xtuml.masl.javagen.ast.code.Switch.SwitchBlock;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class SwitchBlockImpl extends ASTNodeImpl
    implements SwitchBlock
{

  public SwitchBlockImpl ( final ASTImpl ast )
  {
    super(ast);

  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSwitchBlock(this, p);
  }

  @Override
  public void addCaseLabel ( final Expression caseLabel )
  {
    this.caseLabels.add((ExpressionImpl)caseLabel);
  }

  @Override
  public void addStatement ( final BlockStatement statement )
  {
    this.statements.add((StatementImpl)statement);
  }

  @Override
  public List<? extends ExpressionImpl> getCaseLabels ()
  {
    return Collections.unmodifiableList(caseLabels);
  }

  @Override
  public List<? extends StatementImpl> getStatements ()
  {
    return Collections.unmodifiableList(statements);
  }

  @Override
  public boolean isDefault ()
  {
    return isDefault;
  }

  @Override
  public void setDefault ()
  {
    this.isDefault = true;
  }


  private final ChildNodeList<ExpressionImpl> caseLabels = new ChildNodeList<ExpressionImpl>(this);

  private final ChildNodeList<StatementImpl>  statements = new ChildNodeList<StatementImpl>(this);
  private boolean                             isDefault;
}
