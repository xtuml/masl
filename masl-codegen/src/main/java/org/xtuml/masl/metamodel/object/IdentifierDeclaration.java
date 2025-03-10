/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.object;

import org.xtuml.masl.metamodel.ASTNode;

import java.util.List;

public interface IdentifierDeclaration extends ASTNode {

    List<? extends AttributeDeclaration> getAttributes();

    boolean isPreferred();
}
