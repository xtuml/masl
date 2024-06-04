package org.xtuml.masl.translate.kafka;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.ImportedLibrary;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.translate.main.Architecture;


public class Kafka
{

  static Namespace kafkaNamespace            = new Namespace("Kafka");
  static Library   library                   = new InterfaceLibrary("Kafka").inBuildSet(Architecture.buildSet);
  static Library   cppkafkaLibrary           = new ImportedLibrary("cppkafka");
  static Library   rdkafkaLibrary            = new ImportedLibrary("rdkafka");

  static CodeFile  bufferedIOInc             = library.createInterfaceHeader("kafka/BufferedIO.hh");
  static CodeFile  processHandlerInc         = library.createInterfaceHeader("kafka/ProcessHandler.hh");
  static CodeFile  producerInc               = library.createInterfaceHeader("kafka/Producer.hh");
  static CodeFile  serviceHandlerInc         = library.createInterfaceHeader("kafka/ServiceHandler.hh");
  static CodeFile  dataConsumerInc           = library.createInterfaceHeader("kafka/DataConsumer.hh");
  static CodeFile  consumerInc           	 = library.createInterfaceHeader("kafka/Consumer.hh");

  static Class     processHandlerClass       = new Class("ProcessHandler", kafkaNamespace, processHandlerInc);
  static Class     producerClass             = new Class("Producer", kafkaNamespace, producerInc);
  static Class     callable                  = new Class("Callable", kafkaNamespace, processHandlerInc);
  static Class     serviceHandlerClass       = new Class("ServiceHandler", kafkaNamespace, serviceHandlerInc);
  static Class     dataConsumerClass         = new Class("DataConsumer", kafkaNamespace, dataConsumerInc);
  static Class     consumerClass         	 = new Class("Consumer", kafkaNamespace, consumerInc);

}
