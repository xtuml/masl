package org.xtuml.masl.translate.idm;

import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;

import java.util.Collection;
import java.util.Collections;

@Alias("InterDomainMessaging")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    private final org.xtuml.masl.translate.main.ProjectTranslator mainTranslator;
    private final Library library;

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    private ProjectTranslator(final Project project) {
        super(project);
        mainTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
        library = new SharedLibrary(project.getProjectName() + "_idm").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        library.addDependency(InterDomainMessaging.library);
    }

    @Override
    public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites() {
        return Collections.singletonList(mainTranslator);
    }

    @Override
    public void translate() {
        // create dummy source file
        library.createBodyFile("InterDomainMessaging" + Mangler.mangleFile(project));

        // add dependencies on all interface domains
        for (final Domain interfaceDomain : mainTranslator.getInterfaceDomains()) {
            if (interfaceDomain.getPragmas().hasPragma("service_domain")) {
                library.addDependency(DomainTranslator.getInstance(interfaceDomain).getLibrary());
            } else {
                library.addDependency(DomainTranslator.getInstance(interfaceDomain).getInterfaceLibrary());
            }
        }

        // add dependencies on all full domains
        for (final Domain fullDomain : mainTranslator.getFullDomains()) {
            library.addDependency(DomainTranslator.getInstance(fullDomain).getLibrary());
        }
    }

}
