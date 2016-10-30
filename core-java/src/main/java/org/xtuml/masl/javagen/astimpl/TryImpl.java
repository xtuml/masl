//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Catch;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.Try;
import org.xtuml.masl.javagen.ast.types.Type;


public class TryImpl extends StatementImpl
    implements Try
{

  TryImpl ( final ASTImpl ast )
  {
    super(ast);
    setMainBlock(ast.createCodeBlock());
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTry(this, p);
  }

  @Override
  public CatchImpl addCatch ( final Catch clause )
  {
    catchClauses.add((CatchImpl)clause);
    return (CatchImpl)clause;
  }

  @Override
  public List<? extends Catch> getCatches ()
  {
    return catchClauses;
  }

  @Override
  public CodeBlock getFinallyBlock ()
  {
    return finallyBlock.get();
  }

  @Override
  public CodeBlock getMainBlock ()
  {
    return mainBlock.get();
  }

  @Override
  public CodeBlockImpl setFinallyBlock ( final CodeBlock code )
  {
    finallyBlock.set((CodeBlockImpl)code);
    return (CodeBlockImpl)code;
  }

  @Override
  public CodeBlockImpl setMainBlock ( final CodeBlock code )
  {
    mainBlock.set((CodeBlockImpl)code);
    return (CodeBlockImpl)code;
  }

  private final List<CatchImpl>          catchClauses = new ChildNodeList<CatchImpl>(this);
  private final ChildNode<CodeBlockImpl> mainBlock    = new ChildNode<CodeBlockImpl>(this);
  private final ChildNode<CodeBlockImpl> finallyBlock = new ChildNode<CodeBlockImpl>(this);

  @Override
  public Catch addCatch ( final Type type, final String name )
  {
    return addCatch(getAST().createCatch(getAST().createParameter(type, name)));
  }
}
