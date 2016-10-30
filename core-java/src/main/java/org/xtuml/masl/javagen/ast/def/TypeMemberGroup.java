//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;


public interface TypeMemberGroup
{

  Comment addComment ( Comment comment );

  Comment addComment ( String text );

  Constructor addConstructor ( Parameter... params );

  Constructor addConstructor ( Constructor constructor );

  Field addField ( Field field );

  Field addField ( Type type, String name );

  Field addField ( Type type, String name, Expression initialValue );

  InitializerBlock addInitializerBlock ( InitializerBlock declaration );

  InitializerBlock addInitializerBlock ( boolean isStatic );

  Method overrideMethod ( Method method );

  Method addMethod ( Method method );

  Method addMethod ( String name, Type returnType, Parameter... params );

  Method addMethod ( String name, Parameter... params );

  TypeDeclaration addTypeDeclaration ( TypeDeclaration typeDeclaration );

  TypeDeclaration addTypeDeclaration ( String name );

  TypeMemberGroup addGroup ();

  Property addProperty ( Type type, String name );

  Property addProperty ( Type type, String name, Constructor initBy );
}
