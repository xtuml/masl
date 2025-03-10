/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.type.BasicType;

import java.util.List;

public interface Service extends ASTNode {

    String getName();

    PragmaList getDeclarationPragmas();

    PragmaList getDefinitionPragmas();

    List<? extends ExceptionReference> getExceptionSpecs();

    List<? extends ParameterDefinition> getParameters();

    List<? extends VariableDefinition> getLocalVariables();

    BasicType getReturnType();

    Visibility getVisibility();

    boolean isFunction();

    int getOverloadNo();

    CodeBlock getCode();

    String getQualifiedName();

    String getFileName();

    String getFileHash();

}
