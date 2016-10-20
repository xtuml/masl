//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.def.InitializerBlock;


public class InitializerBlockImpl extends TypeMemberImpl
    implements InitializerBlock
{

  InitializerBlockImpl ( final ASTImpl ast, final boolean isStatic )
  {
    super(ast);
    this.isStatic = isStatic;
    setCodeBlock();
  }

  @Override
  public CodeBlockImpl getCodeBlock ()
  {
    return codeBlock.get();
  }

  @Override
  public boolean isStatic ()
  {
    return isStatic;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitInitializerBlock(this, p);
  }

  private final boolean                  isStatic;
  private final ChildNode<CodeBlockImpl> codeBlock = new ChildNode<CodeBlockImpl>(this);

  @Override
  public CodeBlock setCodeBlock ()
  {
    return setCodeBlock(getAST().createCodeBlock());
  }

  @Override
  public CodeBlock setCodeBlock ( final CodeBlock codeBlock )
  {
    this.codeBlock.set((CodeBlockImpl)codeBlock);
    return codeBlock;
  }

}
