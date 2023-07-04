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

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectTerminator extends Positioned implements org.xtuml.masl.metamodel.project.ProjectTerminator {

    public static ProjectTerminator create(final Position position,
                                           final ProjectDomain domain,
                                           final String name,
                                           final PragmaList pragmas) {
        if (domain == null || name == null) {
            return null;
        }
        try {
            final ProjectTerminator obj = new ProjectTerminator(position, domain, name, pragmas);
            domain.addTerminator(obj);
            return obj;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    private ProjectTerminator(final Position position,
                              final ProjectDomain domain,
                              final String name,
                              final PragmaList pragmas) throws SemanticError {
        super(position);
        this.domain = domain;
        this.name = name;
        this.domainTerminator = domain.getDomain().getTerminator(name);

        this.pragmas = pragmas;
    }

    public void addService(final ProjectTerminatorService service) {
        if (service == null) {
            return;
        }
        services.add(service);
    }

    @Override
    public DomainTerminator getDomainTerminator() {
        return domainTerminator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public List<ProjectTerminatorService> getServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public ProjectDomain getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "terminator " +
               domainTerminator.getName() +
               " is\n" +
               TextUtils.alignTabs(TextUtils.formatList(services, "", "", "\n")) +
               "end terminator;\n" +
               pragmas;
    }

    private final DomainTerminator domainTerminator;

    private final List<ProjectTerminatorService> services = new ArrayList<ProjectTerminatorService>();

    private final PragmaList pragmas;

    private final ProjectDomain domain;
    private final String name;
}
