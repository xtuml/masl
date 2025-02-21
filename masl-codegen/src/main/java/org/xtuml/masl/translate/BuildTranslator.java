/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.building.BuildSet;

import java.io.File;

@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".Translator", value = "")
public abstract class BuildTranslator {

    public abstract void translate(File sourceDirectory);

    public abstract void translateBuild(Translator<?> parent, File sourceDirectory);

    public abstract void dump(File directory);

    private Domain domain;
    private Project project;

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public BuildSet getBuildSet() {
        if (domain != null) {
            return BuildSet.getBuildSet(domain);
        } else if (project != null) {
            return BuildSet.getBuildSet(project);
        } else {
            return null;
        }
    }
}
