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

import org.xtuml.masl.metamodel.project.Project;

@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".ProjectTranslator", value = "")
public abstract class ProjectTranslator extends Translator<Project> {

    public static <T extends ProjectTranslator> T getInstance(final Class<T> translatorClass, final Project project) {
        try {
            return getInstance(translatorClass, Project.class, project);
        } catch (final Exception e) {
            assert false : e.getMessage();
            return null;
        }
    }

    protected ProjectTranslator(final Project project) {
        this.project = project;
    }

    protected Project project;

    public Project getProject() {
        return project;
    }
}
