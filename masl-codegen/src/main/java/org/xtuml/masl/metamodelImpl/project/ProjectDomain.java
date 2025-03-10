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

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitProjectDomain(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(terminators, pragmas);
    }

    private final List<ProjectTerminator> terminators = new ArrayList<>();

}
