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

import java.util.List;

public interface CompilationUnit extends ASTNode {

    String getName();

    List<? extends Import> getImportDeclarations();

    List<? extends TypeDeclaration> getTypeDeclarations();

    Package getPackage();

    String getFileName();

    TypeDeclaration addTypeDeclaration(TypeDeclaration declaration);

    Import addImportDeclaration(Import declaration);

    TypeDeclaration createTypeDeclaration(String name);

}
