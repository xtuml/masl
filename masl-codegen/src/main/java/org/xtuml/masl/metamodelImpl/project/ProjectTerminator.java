/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.project;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
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

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitProjectTerminator(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(services, pragmas);
    }

    private final DomainTerminator domainTerminator;

    private final List<ProjectTerminatorService> services = new ArrayList<>();

    private final PragmaList pragmas;

    private final ProjectDomain domain;
    private final String name;
}
