/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.domain;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;

import java.util.List;

public interface DomainTerminator extends ASTNode {

    PragmaList getPragmas();

    Domain getDomain();

    String getName();

    List<? extends DomainTerminatorService> getServices();

    String getComment();

}
