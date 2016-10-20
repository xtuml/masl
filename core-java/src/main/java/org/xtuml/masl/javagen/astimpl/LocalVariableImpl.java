//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;


public class LocalVariableImpl extends VariableImpl
    implements LocalVariable
{

  LocalVariableImpl ( final ASTImpl ast, final Type type, final String name )
  {
    super(ast, type, name);
  }

  LocalVariableImpl ( final ASTImpl ast, final Type type, final String name, final Expression initialValue )
  {
    super(ast, type, name);
    setInitialValue(initialValue);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLocalVariable(this, p);
  }

  private final ChildNode<ExpressionImpl> initialValue = new ChildNode<ExpressionImpl>(this);

  @Override
  public Expression getInitialValue ()
  {
    return initialValue.get();
  }

  @Override
  public void setInitialValue ( final Expression initialValue )
  {
    this.initialValue.set((ExpressionImpl)initialValue);
  }

  @Override
  public VariableDeclarationStatementImpl asStatement ()
  {
    return getAST().createVariableDeclaration(this);
  }

}
