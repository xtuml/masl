//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.Type;


public interface TypeDeclaration
    extends ASTNode, TypeMember, GenericItem, TypeMemberGroup
{

  DeclaredType addInterface ( DeclaredType iface );

  CompilationUnit getEnclosingCompilationUnit ();

  Package getEnclosingPackage ();

  List<? extends DeclaredType> getInterfaces ();

  Modifiers getModifiers ();

  String getName ();

  void setName ( String name );

  DeclaredType getSupertype ();

  boolean isAnnotation ();

  boolean isEnum ();

  boolean isInterface ();

  void setAnnotation ();

  void setInterface ();

  void setStatic ();

  void setAbstract ();

  void setFinal ();

  void setStrictFp ();

  DeclaredType setSupertype ( DeclaredType supertype );

  TypeBody getTypeBody ();

  TypeDeclarationStatement asStatement ();

  void setVisibility ( Visibility visibility );

  Visibility getVisibility ();

  This asThis ();

  EnumConstant addEnumConstant ( EnumConstant enumConstant );

  EnumConstant addEnumConstant ( String name, Expression... args );

  List<? extends EnumConstant> getEnumConstants ();

  DeclaredType asType ( Type... args );

  ClassLiteral clazz ();

  CompilationUnit getDeclaringCompilationUnit ();

  TypeDeclaration getDeclaringType ();

  void setEnum ();

  @Override
  Method overrideMethod ( Method superMethod );

}
