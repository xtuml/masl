package org.xtuml.masl.translate.kafka;

import java.util.List;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;

abstract class ServiceTranslator {

    private final Service service;
    private final DomainTranslator domainTranslator;
    private final ParameterSerializer serializer;

    ServiceTranslator(final Service service, final DomainTranslator domainTranslator,
            final ParameterSerializer serializer) {
        this.service = service;
        this.domainTranslator = domainTranslator;
        this.serializer = serializer;
    }

    abstract void translate();

    abstract List<Runnable> getFilePopulators();

    Service getService() {
        return service;
    }

    DomainTranslator getDomainTranslator() {
        return domainTranslator;
    }

    ParameterSerializer getParameterSerializer() {
        return serializer;
    }

    Namespace getDomainNamespace() {
        return getDomainTranslator().getNamespace();
    }

    Expression getTopicName(final DomainTerminatorService service) {
        if (service.getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).size() == 1) {
            final String topicNameString = service.getDeclarationPragmas()
                    .getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).get(0);
            if (!isBoolean(topicNameString) && !isNumeric(topicNameString)) {
                return Literal.createStringLiteral(topicNameString);
            }
        }
        final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
        final Expression domainId = new Function("getId")
                .asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false,
                        Literal.createStringLiteral(getDomainTranslator().getDomain().getName())), false);
        final Expression serviceId = TerminatorServiceTranslator.getInstance(service).getServiceId();
        return new Function("getTopicName").asFunctionCall(processHandler, false, domainId, serviceId);
    }

    static boolean isBoolean(final String value) {
        return "true".equals(value) || "false".equals(value);
    }

    static boolean isNumeric(final String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean hasSingleStringParameter(Service service) {
        return service.getParameters().size() == 1
                && service.getParameters().get(0).getType().isAssignableFrom(StringType.createAnonymous());
    }

    static boolean isTypeSerializable(BasicType type) {
        return !(type.getBasicType().getActualType() == ActualType.EVENT
                || type.getBasicType().getActualType() == ActualType.DEVICE
                || type.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
    }
}
