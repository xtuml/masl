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

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

public interface TypeMemberGroup {

    Comment addComment(Comment comment);

    Comment addComment(String text);

    Constructor addConstructor(Parameter... params);

    Constructor addConstructor(Constructor constructor);

    Field addField(Field field);

    Field addField(Type type, String name);

    Field addField(Type type, String name, Expression initialValue);

    InitializerBlock addInitializerBlock(InitializerBlock declaration);

    InitializerBlock addInitializerBlock(boolean isStatic);

    Method overrideMethod(Method method);

    Method addMethod(Method method);

    Method addMethod(String name, Type returnType, Parameter... params);

    Method addMethod(String name, Parameter... params);

    TypeDeclaration addTypeDeclaration(TypeDeclaration typeDeclaration);

    TypeDeclaration addTypeDeclaration(String name);

    TypeMemberGroup addGroup();

    Property addProperty(Type type, String name);

    Property addProperty(Type type, String name, Constructor initBy);
}
