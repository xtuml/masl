//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Callable;
import org.xtuml.masl.javagen.ast.def.Parameter;
import org.xtuml.masl.javagen.ast.types.Type;


public class ParameterImpl extends VariableImpl
    implements Parameter
{

  public ParameterImpl ( final ASTImpl ast, final Type paramType, final String name )
  {
    super(ast, paramType, name);
  }

  @Override
  public Callable getParentCallable ()
  {
    return (Callable)getParentNode();
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitParameter(this, p);
  }

}
