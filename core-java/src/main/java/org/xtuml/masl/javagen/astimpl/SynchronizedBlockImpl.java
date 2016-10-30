//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.SynchronizedBlock;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class SynchronizedBlockImpl extends StatementImpl
    implements SynchronizedBlock
{

  public SynchronizedBlockImpl ( final ASTImpl ast, final Expression lockExpression )
  {
    super(ast);
    setLockExpression(lockExpression);
    setCodeBlock(ast.createCodeBlock());
  }

  @Override
  public CodeBlock getCodeBlock ()
  {
    return codeBlock.get();
  }

  @Override
  public void setCodeBlock ( final CodeBlock block )
  {
    codeBlock.set((CodeBlockImpl)block);
  }

  @Override
  public Expression getLockExpression ()
  {
    return lockExpression.get();
  }

  @Override
  public void setLockExpression ( final Expression expression )
  {
    lockExpression.set((ExpressionImpl)expression);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSynchronizedBlock(this, p);
  }

  private final ChildNode<ExpressionImpl> lockExpression = new ChildNode<ExpressionImpl>(this);
  private final ChildNode<CodeBlockImpl>  codeBlock      = new ChildNode<CodeBlockImpl>(this);
}
