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

import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectDomain extends Positioned implements org.xtuml.masl.metamodel.project.ProjectDomain {

    private final Project project;
    private final Domain domain;
    private PragmaList pragmas;

    public ProjectDomain(final Domain.Reference domain, final Project project) {
        super(domain);
        this.project = project;
        this.domain = domain.getDomain();
    }

    public void setPragmas(final PragmaList pragmas) {
        this.pragmas = pragmas;
    }

    @Override
    public String getName() {
        return domain.getName();
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public List<ProjectTerminator> getTerminators() {
        return Collections.unmodifiableList(terminators);
    }

    public void addTerminator(final ProjectTerminator terminator) {
        if (terminator == null) {
            return;
        }
        terminators.add(terminator);
    }

    private final List<ProjectTerminator> terminators = new ArrayList<ProjectTerminator>();

}
