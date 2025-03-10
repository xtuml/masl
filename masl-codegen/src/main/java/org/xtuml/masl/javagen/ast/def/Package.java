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

import java.util.Collection;

public interface Package extends ASTNode {

    String getName();

    Collection<? extends CompilationUnit> getCompilationUnits();

    CompilationUnit addCompilationUnit(CompilationUnit compilationUnit);

    CompilationUnit addCompilationUnit(String name);

    TypeDeclaration addTypeDeclaration(String name);

}
