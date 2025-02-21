/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.exception;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodel.domain.Domain;

public interface ExceptionDeclaration extends ASTNode {

    String getName();

    Domain getDomain();

    Visibility getVisibility();

    PragmaList getPragmas();

}
