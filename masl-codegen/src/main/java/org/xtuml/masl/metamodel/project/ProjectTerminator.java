/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.project;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.domain.DomainTerminator;

import java.util.List;

public interface ProjectTerminator extends ASTNode {

    String getName();

    PragmaList getPragmas();

    ProjectDomain getDomain();

    DomainTerminator getDomainTerminator();

    List<? extends ProjectTerminatorService> getServices();

}
