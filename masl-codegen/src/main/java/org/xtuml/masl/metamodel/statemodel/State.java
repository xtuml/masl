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
import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;

import java.util.List;

public interface State extends ASTNode {

    enum Type {
        NORMAL, CREATION, TERMINAL, ASSIGNER, ASSIGNER_START
    }

    String getName();

    ObjectDeclaration getParentObject();

    CodeBlock getCode();

    PragmaList getDeclarationPragmas();

    PragmaList getDefinitionPragmas();

    List<? extends ParameterDefinition> getParameters();

    List<? extends VariableDefinition> getLocalVariables();

    Type getType();

    String getQualifiedName();

    String getFileName();

    String getFileHash();
}
