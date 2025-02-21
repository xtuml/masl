/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.exception.ExceptionReference;

import java.util.List;

public interface ExceptionHandler extends ASTNode {

    int getLineNumber();

    ExceptionReference getException();

    List<? extends Statement> getCode();

    String getMessageVariable();

    VariableDefinition getMessageVarDef();
}
