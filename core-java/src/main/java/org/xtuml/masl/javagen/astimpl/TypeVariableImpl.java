//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.TypeParameter;
import org.xtuml.masl.javagen.ast.types.TypeVariable;


public class TypeVariableImpl extends ReferenceTypeImpl
    implements TypeVariable
{

  TypeVariableImpl ( final ASTImpl ast, final TypeParameter parameter )
  {
    super(ast);
    this.parameter = parameter;
  }

  @Override
  public String getName ()
  {
    return parameter.getName();
  }

  @Override
  public TypeParameter getTypeParameter ()
  {
    return parameter;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTypeVariable(this, p);
  }

  private final TypeParameter parameter;

  @Override
  public TypeVariableImpl deepCopy ()
  {
    return new TypeVariableImpl(getAST(), parameter);
  }

}
