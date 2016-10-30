//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.This;


public interface TypeBody
    extends TypeMemberGroup, ASTNode
{

  List<? extends TypeMember> getMembers ();


  EnumConstant addEnumConstant ( EnumConstant enumConstant );

  EnumConstant addEnumConstant ( String name, Expression... args );

  List<? extends EnumConstant> getEnumConstants ();


  This asThis ();
}
