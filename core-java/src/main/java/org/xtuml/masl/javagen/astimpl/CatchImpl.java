//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Catch;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.def.Parameter;


public class CatchImpl extends ASTNodeImpl
    implements Catch
{

  public CatchImpl ( final ASTImpl ast, final Parameter exceptionParameter )
  {
    super(ast);
    setCodeBlock(ast.createCodeBlock());
    setException(exceptionParameter);
  }

  @Override
  public void setCodeBlock ( final CodeBlock codeBlock )
  {
    this.codeBlock.set((CodeBlockImpl)codeBlock);
  }

  @Override
  public CodeBlockImpl getCodeBlock ()
  {
    return codeBlock.get();
  }

  @Override
  public ParameterImpl getException ()
  {
    return exceptionParameter.get();
  }

  @Override
  public void setException ( final Parameter exceptionParameter )
  {
    this.exceptionParameter.set((ParameterImpl)exceptionParameter);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCatch(this, p);
  }

  private final ChildNode<ParameterImpl> exceptionParameter = new ChildNode<ParameterImpl>(this);
  private final ChildNode<CodeBlockImpl>            codeBlock          = new ChildNode<CodeBlockImpl>(this);

}
