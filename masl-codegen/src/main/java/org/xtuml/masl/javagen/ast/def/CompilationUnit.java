/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
