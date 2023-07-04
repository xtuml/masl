/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.List;

public interface TypeDeclaration extends ASTNode, TypeMember, GenericItem, TypeMemberGroup {

    DeclaredType addInterface(DeclaredType iface);

    CompilationUnit getEnclosingCompilationUnit();

    Package getEnclosingPackage();

    List<? extends DeclaredType> getInterfaces();

    Modifiers getModifiers();

    String getName();

    void setName(String name);

    DeclaredType getSupertype();

    boolean isAnnotation();

    boolean isEnum();

    boolean isInterface();

    void setAnnotation();

    void setInterface();

    void setStatic();

    void setAbstract();

    void setFinal();

    void setStrictFp();

    DeclaredType setSupertype(DeclaredType supertype);

    TypeBody getTypeBody();

    TypeDeclarationStatement asStatement();

    void setVisibility(Visibility visibility);

    Visibility getVisibility();

    This asThis();

    EnumConstant addEnumConstant(EnumConstant enumConstant);

    EnumConstant addEnumConstant(String name, Expression... args);

    List<? extends EnumConstant> getEnumConstants();

    DeclaredType asType(Type... args);

    ClassLiteral clazz();

    CompilationUnit getDeclaringCompilationUnit();

    TypeDeclaration getDeclaringType();

    void setEnum();

    @Override
    Method overrideMethod(Method superMethod);

}
