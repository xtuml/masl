//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.Variable;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;


public class VariableAccessImpl extends ExpressionImpl
    implements VariableAccess
{

  public VariableAccessImpl ( final ASTImpl ast, final Variable variable )
  {
    super(ast);
    setVariable(variable);
  }

  @Override
  protected int getPrecedence ()
  {
    return Integer.MAX_VALUE;
  }

  @Override
  public Variable getVariable ()
  {
    return variable;
  }

  @Override
  public Variable setVariable ( final Variable variable )
  {
    this.variable = variable;
    return variable;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitvariableAccess(this, p);
  }

  private Variable variable;

}
