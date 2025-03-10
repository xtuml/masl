package org.xtuml.masl.translate.kafka;

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

@Alias("Kafka")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    public static final String KAFKA_TOPIC_PRAGMA = "kafka_topic";
    public static final String KAFKA_PARTITION_KEY_PRAGMA = "kafka_partition_key";

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
        domainNamespace = new Namespace(Mangler.mangleName(domain), Kafka.kafkaNamespace);

        library = new SharedLibrary(mainTranslator.getLibrary().getName() + "_kafka")
                .inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        library.addDependency(Kafka.library);

        interfaceLibrary = new SharedLibrary(mainTranslator.getLibrary().getName() + "_if_kafka")
                .inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        interfaceLibrary.addDependency(Kafka.library);
    }

    @Override
    public void translate() {
        // create code files
        final CodeFile consumerCodeFile = library.createBodyFile("Kafka_consumers" + Mangler.mangleFile(domain));
        final CodeFile pollerCodeFile = library.createBodyFile("Kafka_pollers" + Mangler.mangleFile(domain));
        final CodeFile publisherCodeFile = interfaceLibrary
                .createBodyFile("Kafka_publishers" + Mangler.mangleFile(domain));

        // create domain service translators
        final List<DomainServiceTranslator> domainServiceTranslators = domain.getServices().stream()
                .filter(service -> service.getDeclarationPragmas().hasPragma(KAFKA_TOPIC_PRAGMA)
                        && !service.isFunction() && !service.isExternal() && !service.isScenario())
                .map(service -> new DomainServiceTranslator(service, this, new JsonSerializer(), consumerCodeFile, publisherCodeFile))
                .collect(Collectors.toList());

        // translate domain service handlers
        domainServiceTranslators.forEach(DomainServiceTranslator::translate);

        // populate the code for the domain services
        final List<Iterator<Runnable>> domainServiceFilePopulators = domainServiceTranslators.stream()
                .map(ServiceTranslator::getFilePopulators).map(List::iterator).collect(Collectors.toList());
        while (domainServiceFilePopulators.stream().anyMatch(Iterator::hasNext)) {
            domainServiceFilePopulators.stream().filter(Iterator::hasNext).map(Iterator::next).forEach(Runnable::run);
        }

        // create domain terminator service translators
        final List<DomainTerminatorServiceTranslator> terminatorServiceTranslators = domain.getTerminators().stream()
                .flatMap(terminator -> terminator.getServices().stream())
                .filter(service -> service.getDeclarationPragmas().hasPragma(KAFKA_TOPIC_PRAGMA) && service.isFunction()
                        && service.getReturnType().isAssignableFrom(BooleanType.createAnonymous()))
                .map(service -> new DomainTerminatorServiceTranslator(service, this, new JsonSerializer(), pollerCodeFile))
                .collect(Collectors.toList());

        // translate domain terminator service handlers
        terminatorServiceTranslators.forEach(DomainTerminatorServiceTranslator::translate);

        // populate the code for the terminator services
        final List<Iterator<Runnable>> terminatorServiceFilePopulators = terminatorServiceTranslators.stream()
                .map(ServiceTranslator::getFilePopulators).map(List::iterator).collect(Collectors.toList());
        while (terminatorServiceFilePopulators.stream().anyMatch(Iterator::hasNext)) {
            terminatorServiceFilePopulators.stream().filter(Iterator::hasNext).map(Iterator::next)
                    .forEach(Runnable::run);
        }
    }

    Namespace getNamespace() {
        return domainNamespace;
    }

}
