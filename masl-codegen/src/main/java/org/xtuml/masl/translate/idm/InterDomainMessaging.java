package org.xtuml.masl.translate.idm;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.ImportedLibrary;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.translate.building.BuildSet;
import org.xtuml.masl.translate.main.Architecture;

public class InterDomainMessaging {

    static Namespace idmNamespace = new Namespace("InterDomainMessaging");
    static BuildSet buildSet = new BuildSet("xtuml_idm");
    static Library library = new ImportedLibrary("xtuml_idm").inBuildSet(buildSet);

    static CodeFile processHandlerInc = library.createInterfaceHeader("idm/ProcessHandler.hh");
    static CodeFile producerInc = library.createInterfaceHeader("idm/Producer.hh");
    static CodeFile serviceHandlerInc = library.createInterfaceHeader("idm/ServiceHandler.hh");
    static CodeFile dataConsumerInc = library.createInterfaceHeader("idm/DataConsumer.hh");
    static CodeFile consumerInc = library.createInterfaceHeader("idm/Consumer.hh");

    static Class processHandlerClass = new Class("ProcessHandler", idmNamespace, processHandlerInc);
    static Class producerClass = new Class("Producer", idmNamespace, producerInc);
    static Class callable = new Class("Callable", idmNamespace, processHandlerInc);
    static Class serviceHandlerClass = new Class("ServiceHandler", idmNamespace, serviceHandlerInc);
    static Class dataConsumerClass = new Class("DataConsumer", idmNamespace, dataConsumerInc);
    static Class consumerClass = new Class("Consumer", idmNamespace, consumerInc);
}
