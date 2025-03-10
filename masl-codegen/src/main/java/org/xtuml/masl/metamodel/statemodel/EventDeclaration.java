/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.statemodel;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;

import java.util.List;

public interface EventDeclaration extends ASTNode {

    enum Type {
        NORMAL, CREATION, ASSIGNER
    }

    String getName();

    List<? extends ParameterDefinition> getParameters();

    ObjectDeclaration getParentObject();

    PragmaList getPragmas();

    Type getType();

    boolean isScheduled();
}
