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
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;

import java.util.List;

public interface AttributeDeclaration extends ASTNode {

    ObjectDeclaration getParentObject();

    Expression getDefault();

    PragmaList getPragmas();

    String getName();

    BasicType getType();

    boolean isPreferredIdentifier();

    boolean isIdentifier();

    boolean isUnique();

    boolean isReferential();

    List<? extends ReferentialAttributeDefinition> getRefAttDefs();

}
