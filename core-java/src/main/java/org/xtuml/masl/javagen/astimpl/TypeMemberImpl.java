//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.def.TypeMember;


public abstract class TypeMemberImpl extends ASTNodeImpl
    implements TypeMember, TypeMemberGroupMember
{

  public TypeMemberImpl ( final ASTImpl ast )
  {
    super(ast);

  }

}
