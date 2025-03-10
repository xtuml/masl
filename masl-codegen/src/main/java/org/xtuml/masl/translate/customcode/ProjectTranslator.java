/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.customcode;

import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.BuildSet;

import java.io.File;

@Alias("CustomCode")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    private final BuildSet buildSet;

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    private ProjectTranslator(final Project project) {
        super(project);
        buildSet = BuildSet.getBuildSet(project);
    }

    @Override
    public void translate() {
        if (new XMLParser(buildSet).parse()) {
            buildSet.addIncludeDir(new File("../custom"));
            buildSet.addSourceDir(new File("../custom"));
        }
    }
}
