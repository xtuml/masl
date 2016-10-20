//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.types.ReferenceType;


public class MirroredTypeParameterImpl extends TypeParameterImpl
    implements org.xtuml.masl.javagen.ast.def.TypeParameter
{

  MirroredTypeParameterImpl ( final ASTImpl ast, final java.lang.reflect.TypeVariable<?> param, final Scope declaringScope )
  {
    super(ast, param.getName());
    for ( final java.lang.reflect.Type bound : param.getBounds() )
    {
      if ( bound != Object.class )
      {
        super.addExtendsBound((ReferenceTypeImpl)getAST().createType(bound));
      }
    }

  }

  @Override
  public ReferenceTypeImpl addExtendsBound ( final ReferenceType extendsBound )
  {
    throw new UnsupportedOperationException("Mirrored Type Parameter");
  }


}
