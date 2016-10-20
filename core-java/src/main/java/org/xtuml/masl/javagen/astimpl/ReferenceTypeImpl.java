//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.types.ReferenceType;


public abstract class ReferenceTypeImpl extends TypeImpl
    implements ReferenceType
{

  ReferenceTypeImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  @Override
  public abstract ReferenceTypeImpl deepCopy ();
}
