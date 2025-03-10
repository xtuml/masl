/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;

public interface TypeDeclarationStatement extends BlockStatement {

    TypeDeclaration getTypeDeclaration();

    void setTypeDeclaration(TypeDeclaration typeDeclaration);

}
