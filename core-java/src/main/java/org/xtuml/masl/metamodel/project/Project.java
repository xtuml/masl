/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
