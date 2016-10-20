//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.Expression;


public class EnumConstantImpl extends ASTNodeImpl
    implements EnumConstant
{

  EnumConstantImpl ( final ASTImpl ast, final String name, final Expression... args )
  {
    super(ast);
    setName(name);
    for ( final Expression arg : args )
    {
      addArgument(arg);
    }
  }

  public EnumConstantImpl ( final ASTImpl ast )
  {
    super(ast);

  }

  @Override
  public EnumConstantAccess asExpression ()
  {
    return getAST().createEnumConstantAccess(this);
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
  public TypeBody setTypeBody ()
  {
    return setTypeBody(getAST().createTypeBody());
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEnumConstant(this, p);
  }

  @Override
  public TypeBodyImpl getTypeBody ()
  {
    return typeBody.get();
  }

  @Override
  public TypeBodyImpl setTypeBody ( final TypeBody typeBody )
  {
    this.typeBody.set((TypeBodyImpl)typeBody);
    return (TypeBodyImpl)typeBody;
  }


  private final ChildNode<TypeBodyImpl> typeBody = new ChildNode<TypeBodyImpl>(this);
  private String                        name;

  @Override
  public List<? extends ExpressionImpl> getArguments ()
  {
    return Collections.unmodifiableList(arguments);
  }

  @Override
  public ExpressionImpl addArgument ( final Expression argument )
  {
    this.arguments.add((ExpressionImpl)argument);
    return (ExpressionImpl)argument;
  }


  private final ChildNodeList<ExpressionImpl> arguments = new ChildNodeList<ExpressionImpl>(this);

  TypeBodyImpl getParentTypeBody ()
  {
    return (TypeBodyImpl)getParentNode();
  }

  TypeDeclarationImpl getDeclaringType ()
  {
    if ( getParentTypeBody() != null )
    {
      return getParentTypeBody().getParentTypeDeclaration();
    }
    else
    {
      return null;
    }
  }
}
