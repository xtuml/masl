/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import com.google.common.collect.Iterables;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Alias("Sqlite")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    private ProjectTranslator(final Project project) {
        super(project);
        mainProjectTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
        executable =
                new Executable(project.getProjectName() +
                               "_sqlite").inBuildSet(mainProjectTranslator.getBuildSet()).withCCDefaultExtensions();
    }

    public Library getExecutable() {
        return executable;
    }

    public org.xtuml.masl.translate.main.ProjectTranslator getMainProjectTranslator() {
        return mainProjectTranslator;
    }

    @Override
    public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites() {
        return Collections.singletonList(mainProjectTranslator);
    }

    public Collection<CodeFile> getSourceFiles() {
        Collection<CodeFile> files = Collections.emptyList();
        if (sqliteMbusCodeFile != null) {
            files = List.of(sqliteMbusCodeFile);
        }
        return files;
    }

    @Override
    public void translate() {
        executable.addDependency(mainProjectTranslator.getLibrary());
        if (Iterables.isEmpty(executable.getBodyFiles())) {
            // Some build systems complain if there's not at least one file in an executable
            executable.createBodyFile("sqlite_dummy");
        }

        for (final Domain domain : mainProjectTranslator.getFullDomains()) {
            executable.addDependency(DomainTranslator.getInstance(domain).getLibrary());
        }
    }

    private CodeFile sqliteMbusCodeFile;
    private final org.xtuml.masl.translate.main.ProjectTranslator mainProjectTranslator;

    private final Library executable;

}
