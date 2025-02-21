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
import org.xtuml.masl.metamodel.domain.Domain;

import java.util.List;
import java.util.Set;

public interface Project extends ASTNode {

    /**
     * Returns the list of domains explicity specified for inclusion in the project
     *
     * @return the list of domains
     */
    List<? extends ProjectDomain> getDomains();

    /**
     * Returns the list of pragmas defined for the project
     *
     * @return the list of pragmas
     */
    PragmaList getPragmas();

    /**
     * Returns the name of the project
     *
     * @return the project name
     */
    String getProjectName();

    Set<Domain> getFullDomains();

    Set<Domain> getInterfaceDomains();

}
