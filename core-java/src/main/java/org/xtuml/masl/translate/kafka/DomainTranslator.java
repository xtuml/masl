package org.xtuml.masl.translate.kafka;

import java.util.List;
import java.util.stream.Collectors;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.Mangler;


@Alias("Kafka")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  public static final String KAFKA_TOPIC_PRAGMA = "kafka_topic";
  public static final String KAFKA_PARTITION_KEY_PRAGMA = "kafka_partition_key";

  private final org.xtuml.masl.translate.main.DomainTranslator mainTranslator;
  private final Namespace domainNamespace;
  private final Library library;
  private final Library interfaceLibrary;

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    mainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    domainNamespace = new Namespace(Mangler.mangleName(domain), Kafka.kafkaNamespace);


    library = new SharedLibrary(mainTranslator.getLibrary().getName() + "_kafka").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
    library.addDependency(Kafka.library);
    library.addDependency(Kafka.cppkafkaLibrary);
    library.addDependency(Kafka.rdkafkaLibrary);

    interfaceLibrary = new SharedLibrary(mainTranslator.getLibrary().getName() + "_if_kafka").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
    interfaceLibrary.addDependency(Kafka.library);
    interfaceLibrary.addDependency(Kafka.cppkafkaLibrary);
    interfaceLibrary.addDependency(Kafka.rdkafkaLibrary);
  }

  @Override
  public void translate ()
  {
    // create code files
    final CodeFile consumerCodeFile = library.createBodyFile("Kafka" + Mangler.mangleFile(domain));
    final CodeFile publisherCodeFile = interfaceLibrary.createBodyFile("Kafka_publishers" + Mangler.mangleFile(domain));

    // create service translators
    final List<ServiceTranslator> serviceTranslators = domain.getServices().stream()
      .filter(service -> service.getDeclarationPragmas().hasPragma(KAFKA_TOPIC_PRAGMA) && !service.isFunction() && !service.isExternal() && !service.isScenario())
      .map(service -> new ServiceTranslator(service, this)).collect(Collectors.toList());


    // translate service handlers
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addServiceHandler(consumerCodeFile);
    }

    // handle custom topics (consumer)
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addCustomTopicName(consumerCodeFile);
    }

    // add topic registrations
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addTopicRegistration(consumerCodeFile);
    }

    // create publisher services
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addPublisher(publisherCodeFile);
    }

    // handle custom topics (publisher)
    for (final ServiceTranslator serviceTranslator : serviceTranslators)
    {
      serviceTranslator.addCustomTopicName(publisherCodeFile);
    }

  }

  Namespace getNamespace()
  {
    return domainNamespace;
  }

  org.xtuml.masl.translate.main.DomainTranslator getMainTranslator()
  {
    return mainTranslator;
  }

}
