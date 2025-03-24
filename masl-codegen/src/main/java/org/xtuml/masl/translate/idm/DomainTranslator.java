package org.xtuml.masl.translate.idm;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodelImpl.type.BooleanType;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.Mangler;

@Alias("InterDomainMessaging")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    public static final String IDM_TOPIC_PRAGMA = "idm_topic";
    public static final String IDM_PARTITION_KEY_PRAGMA = "idm_partition_key";

    private final org.xtuml.masl.translate.main.DomainTranslator mainTranslator;
    private final Namespace domainNamespace;
    private final Library library;
    private final Library interfaceLibrary;

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        mainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
        domainNamespace = new Namespace(Mangler.mangleName(domain), InterDomainMessaging.idmNamespace);

        library = new SharedLibrary(mainTranslator.getLibrary().getName() + "_idm")
                .inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        library.addDependency(InterDomainMessaging.library);

        interfaceLibrary = new SharedLibrary(mainTranslator.getLibrary().getName() + "_if_idm")
                .inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        interfaceLibrary.addDependency(InterDomainMessaging.library);
    }

    @Override
    public void translate() {
        // create code files
        final CodeFile consumerCodeFile = library.createBodyFile("InterDomainMessaging_consumers" + Mangler.mangleFile(domain));
        final CodeFile producerCodeFile = interfaceLibrary
                .createBodyFile("InterDomainMessaging_producers" + Mangler.mangleFile(domain));

        // create domain service translators
        final List<DomainServiceTranslator> domainServiceTranslators = domain.getServices().stream()
                .filter(service -> service.getDeclarationPragmas().hasPragma(IDM_TOPIC_PRAGMA)
                        && !service.isFunction() && !service.isExternal() && !service.isScenario())
                .map(service -> new DomainServiceTranslator(service, this, new JsonSerializer(), consumerCodeFile, producerCodeFile))
                .collect(Collectors.toList());

        // translate domain service handlers
        domainServiceTranslators.forEach(DomainServiceTranslator::translate);

        // populate the code for the domain services
        final List<Iterator<Runnable>> domainServiceFilePopulators = domainServiceTranslators.stream()
                .map(ServiceTranslator::getFilePopulators).map(List::iterator).collect(Collectors.toList());
        while (domainServiceFilePopulators.stream().anyMatch(Iterator::hasNext)) {
            domainServiceFilePopulators.stream().filter(Iterator::hasNext).map(Iterator::next).forEach(Runnable::run);
        }

    }

    Namespace getNamespace() {
        return domainNamespace;
    }

}
