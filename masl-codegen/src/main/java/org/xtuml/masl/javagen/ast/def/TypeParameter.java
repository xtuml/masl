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
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.List;

public interface TypeParameter extends ASTNode {

    List<? extends ReferenceType> getExtendsBounds();

    ReferenceType addExtendsBound(ReferenceType bound);

    String getName();

    void setName(String name);
}
