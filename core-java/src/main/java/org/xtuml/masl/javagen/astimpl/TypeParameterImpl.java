//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.TypeParameter;
import org.xtuml.masl.javagen.ast.types.ReferenceType;


public class TypeParameterImpl extends ASTNodeImpl
    implements TypeParameter
{

  TypeParameterImpl ( final ASTImpl ast, final String name )
  {
    super(ast);
    this.name = name;
  }

  TypeParameterImpl ( final ASTImpl ast, final java.lang.reflect.TypeVariable<?> param, final Scope declaringScope )
  {
    this(ast, param.getName());
    for ( final java.lang.reflect.Type bound : param.getBounds() )
    {
      if ( bound != Object.class )
      {
        addExtendsBound((ReferenceTypeImpl)ast.createType(bound));
      }
    }

  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public ReferenceTypeImpl addExtendsBound ( final ReferenceType extendsBound )
  {
    extendsBounds.add((ReferenceTypeImpl)extendsBound);
    return (ReferenceTypeImpl)extendsBound;
  }

  @Override
  public List<ReferenceTypeImpl> getExtendsBounds ()
  {
    return Collections.unmodifiableList(extendsBounds);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTypeParameter(this, p);
  }

  private String                                 name;
  private final ChildNodeList<ReferenceTypeImpl> extendsBounds = new ChildNodeList<ReferenceTypeImpl>(this);

  @Override
  public void setName ( final String name )
  {
    this.name = name;
  }

}
