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
package org.xtuml.masl.metamodelImpl.project;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Project extends Positioned implements org.xtuml.masl.metamodel.project.Project {

    private final String projectName;
    private PragmaList pragmas;
    private final List<ProjectDomain> domains;

    public Project(final Position position, final String projectName) {
        super(position);
        this.projectName = projectName;
        this.domains = new ArrayList<>();
    }

    public void addDomain(final ProjectDomain domain) {
        domains.add(domain);
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public List<ProjectDomain> getDomains() {
        return Collections.unmodifiableList(domains);
    }

    @Override
    public Set<Domain> getFullDomains() {
        return domains.stream().map(d -> d.getDomain()).collect(Collectors.toSet());
    }

    @Override
    public Set<Domain> getInterfaceDomains() {
        final Set<Domain>
                interfaceDomains =
                domains.stream().flatMap(d -> d.getDomain().getReferencedInterfaces().stream()).collect(Collectors.toSet());
        interfaceDomains.removeAll(getFullDomains());
        return interfaceDomains;
    }

    public void setPragmas(final PragmaList pragmas) {
        this.pragmas = pragmas;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitProject(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(domains, pragmas);
    }

}
