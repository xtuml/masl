package org.xtuml.masl.translate.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FunctionObjectCall;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.Types;

public class DomainServiceTranslator extends ServiceTranslator {

    private final CodeFile consumerCodeFile;
    private final CodeFile publisherCodeFile;

    private Class handlerClass;
    private Class invokerClass;
    private Function getInvokerFn;
    private Variable topicNameSet;
    private Variable consumerRegisteredVar;
    private Variable publisherRegisteredVar;
    private Function publishFn;

    DomainServiceTranslator(DomainService service, DomainTranslator domainTranslator, ParameterSerializer serializer,
            CodeFile consumerCodeFile, CodeFile publisherCodeFile) {
        super(service, domainTranslator, serializer);
        this.consumerCodeFile = consumerCodeFile;
        this.publisherCodeFile = publisherCodeFile;
    }

    @Override
    DomainService getService() {
        return (DomainService) super.getService();
    }

    @Override
    List<Runnable> getFilePopulators() {
        return List.of(() -> consumerCodeFile.addClassDeclaration(handlerClass),
                () -> consumerCodeFile.addClassDeclaration(invokerClass),
                () -> consumerCodeFile.addFunctionDefinition(getInvokerFn),
                topicNameSet != null ? () -> consumerCodeFile.addVariableDefinition(topicNameSet) : () -> {},
                () -> consumerCodeFile.addVariableDefinition(consumerRegisteredVar),
                () -> publisherCodeFile.addFunctionDefinition(publishFn),
                topicNameSet != null ? () -> publisherCodeFile.addVariableDefinition(topicNameSet) : () -> {},
                () -> publisherCodeFile.addVariableDefinition(publisherRegisteredVar));
    }

    @Override
    void translate() {
        translateConsumer();
        translatePublisher();
        translateCustomTopicName();
    }

    void translateConsumer() {
        // create and add the handler class to the file
        handlerClass = new Class(Mangler.mangleName(getService()) + "Handler", getDomainNamespace());
        handlerClass.addSuperclass(Kafka.serviceHandlerClass, Visibility.PUBLIC);
        final DeclarationGroup group = handlerClass.createDeclarationGroup();
        getInvokerFn = handlerClass.createMemberFunction(group, "getInvoker", Visibility.PUBLIC);
        getInvokerFn.setReturnType(new TypeUsage(Kafka.callable));
        getInvokerFn.setConst(true);

        // create the invoker class
        invokerClass = new Class(Mangler.mangleName(getService()) + "Invoker", getDomainNamespace());
        final DeclarationGroup functions = invokerClass.createDeclarationGroup();
        final Function constructor = invokerClass.createConstructor(functions, Visibility.PUBLIC);
        constructor.declareInClass(true);
        final Expression paramData = constructor
                .createParameter(new TypeUsage(Std.vector(new TypeUsage(Std.uint8))), "param_data").asExpression();

        // create invoker function
        final Function invoker = invokerClass.createMemberFunction(functions, "operator()", Visibility.PUBLIC);
        invoker.declareInClass(true);

        // if the service has a single string parameter, just pass through
        final boolean noParse = hasSingleStringParameter(getService());

        // handle parameters
        final List<Expression> invokeArgs = new ArrayList<>();
        final List<Variable> deserializeVars = new ArrayList<>();
        final DeclarationGroup vars = invokerClass.createDeclarationGroup();
        for (final ParameterDefinition param : getService().getParameters()) {
            final TypeUsage type = Types.getInstance().getType(param.getType());
            if (isTypeSerializable(param.getType().getBasicType())) {
                final Variable arg = invokerClass.createMemberVariable(vars, Mangler.mangleName(param), type,
                        Visibility.PRIVATE);
                deserializeVars.add(arg);
                invokeArgs.add(arg.asExpression());
            } else {
                final Variable arg = new Variable(type, Mangler.mangleName(param));
                invoker.getCode().appendStatement(arg.asStatement());
                invokeArgs.add(arg.asExpression());
            }
        }

        // add the deserialization code
        if (noParse) {
            final Expression paramAccess = Std.string.callConstructor(
                    new Function("begin").asFunctionCall(paramData, false),
                    new Function("end").asFunctionCall(paramData, false));
            constructor.getCode().appendStatement(
                    new BinaryExpression(deserializeVars.get(0).asExpression(), BinaryOperator.ASSIGN, paramAccess)
                            .asStatement());
        } else {
            getParameterSerializer().deserialize(paramData, deserializeVars, constructor.getCode());
        }

        // create the call to the service interceptor
        final org.xtuml.masl.translate.main.DomainServiceTranslator serviceTranslator = org.xtuml.masl.translate.main.DomainServiceTranslator
                .getInstance(getService());
        final TypedefType serviceInterceptor = serviceTranslator.getServiceInterceptor();
        final Function serviceFunction = new Function("callService");
        final Expression instanceFnCall = serviceInterceptor.asClass().callStaticFunction("instance");
        Expression invokeExpression = new FunctionObjectCall(serviceFunction.asFunctionCall(instanceFnCall, false),
                invokeArgs);
        invoker.getCode().appendStatement(invokeExpression.asStatement());

        // add implementation of 'getInvoker' to the file
        final Expression paramData2 = getInvokerFn
                .createParameter(new TypeUsage(Std.vector(new TypeUsage(Std.uint8))), "param_data").asExpression();
        getInvokerFn.getCode().appendStatement(new ReturnStatement(invokerClass.callConstructor(paramData2)));

        // void addTopicRegistration(final CodeFile codeFile) {
        final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
        final Expression domainId = new Function("getId")
                .asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false,
                        Literal.createStringLiteral(getDomainTranslator().getDomain().getName())), false);
        final Expression serviceId = org.xtuml.masl.translate.main.DomainServiceTranslator
                .getInstance((DomainService) getService()).getServiceId();
        final Expression handler = Std.shared_ptr(new TypeUsage(handlerClass))
                .callConstructor(new NewExpression(new TypeUsage(handlerClass)));
        final Function registerServiceFunc = new Function("registerServiceHandler");
        final Expression registerService = registerServiceFunc.asFunctionCall(processHandler, false, domainId,
                serviceId, handler);
        consumerRegisteredVar = new Variable(new TypeUsage(FundamentalType.BOOL),
                Mangler.mangleName(getService()) + "_registered", getDomainNamespace(), registerService);
    }

    void translatePublisher() {
        // create service function
        publishFn = new Function(Mangler.mangleName(getService()), getDomainNamespace());
        publishFn.setReturnType(getDomainTranslator().getTypes().getType(getService().getReturnType()));

        // if the service has a single string parameter, just pass through
        final boolean noParse = hasSingleStringParameter(getService());

        // determine whether or not to include partition key
        final boolean includePartKey = getService().getParameters().stream().anyMatch(
                param -> getService().getDeclarationPragmas().hasPragma(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA)
                        && getService().getDeclarationPragmas()
                                .getPragmaValues(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA)
                                .contains(param.getName()));

        // handle parameters
        final List<Variable> serializeVars = new ArrayList<>();
        final List<Variable> serializeKeyVars = new ArrayList<>();
        final Map<ParameterDefinition, ParameterTranslator> paramTranslators = new HashMap<>();
        for (final ParameterDefinition param : getService().getParameters()) {
            final ParameterTranslator paramTrans = new ParameterTranslator(param, publishFn);
            paramTranslators.put(param, paramTrans);
            if (isTypeSerializable(param.getType().getBasicType())) {
                serializeVars.add(paramTrans.getVariable());
            }
            if (getService().getDeclarationPragmas().hasPragma(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA)
                    && getService().getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_PARTITION_KEY_PRAGMA)
                            .contains(param.getName())) {
                serializeKeyVars.add(paramTrans.getVariable());
            }
        }

        // serialize the data
        Expression paramData = null;
        if (!noParse) {
            paramData = getParameterSerializer().serialize(serializeVars, publishFn.getCode());
        }

        // serialize the partition key
        Expression keyData = null;
        if (includePartKey) {
            keyData = getParameterSerializer().serialize(serializeKeyVars, publishFn.getCode());
        }

        // call publisher
        final Expression producer = Kafka.producerClass.callStaticFunction("getInstance");
        final Function publishFunc = new Function("publish");
        final Expression domainId = new Function("getId")
                .asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process, false,
                        Literal.createStringLiteral(getDomainTranslator().getDomain().getName())), false);
        final Expression serviceId = org.xtuml.masl.translate.main.DomainServiceTranslator
                .getInstance((DomainService) getService()).getServiceId();

        final List<Expression> publishArgs = new ArrayList<>();
        publishArgs.add(domainId);
        publishArgs.add(serviceId);
        publishArgs.add(noParse
                ? Std.string.callConstructor(
                        paramTranslators.get(getService().getParameters().get(0)).getVariable().asExpression())
                : paramData);
        if (includePartKey) {
            publishArgs.add(keyData);
        }
        final Expression publishExpr = publishFunc.asFunctionCall(producer, false, publishArgs);
        publishFn.getCode().appendStatement(new ExpressionStatement(publishExpr));

        // add service registration
        final org.xtuml.masl.translate.main.DomainServiceTranslator serviceTranslator = org.xtuml.masl.translate.main.DomainServiceTranslator
                .getInstance((DomainService) getService());
        final TypedefType serviceInterceptor = serviceTranslator.getServiceInterceptor();
        final Expression interceptorFnCall = serviceInterceptor.asClass().callStaticFunction("instance");
        final Function registerFunction = new Function("registerLocal");
        final Expression initialValue = registerFunction.asFunctionCall(interceptorFnCall, false,
                publishFn.asFunctionPointer());
        publisherRegisteredVar = new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                "localServiceRegistration_" + Mangler.mangleName(getService()), getDomainNamespace(), initialValue);
        publisherRegisteredVar.setStatic(true);
    }

    void translateCustomTopicName() {
        if (getService().getDeclarationPragmas().getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).size() == 1) {
            final String topicNameString = getService().getDeclarationPragmas()
                    .getPragmaValues(DomainTranslator.KAFKA_TOPIC_PRAGMA).get(0);
            if (!isBoolean(topicNameString) && !isNumeric(topicNameString)) {
                final Expression processHandler = Kafka.processHandlerClass.callStaticFunction("getInstance");
                final Expression domainId = new Function("getId")
                        .asFunctionCall(
                                new Function("getDomain").asFunctionCall(Architecture.process, false,
                                        Literal.createStringLiteral(getDomainTranslator().getDomain().getName())),
                                false);
                final Expression serviceId = org.xtuml.masl.translate.main.DomainServiceTranslator
                        .getInstance((DomainService) getService()).getServiceId();
                final Expression topicName = Literal.createStringLiteral(topicNameString);
                final Function setTopicNameFunc = new Function("setCustomTopicName");
                final Expression setTopicName = setTopicNameFunc.asFunctionCall(processHandler, false, domainId,
                        serviceId, topicName);
                topicNameSet = new Variable(new TypeUsage(FundamentalType.BOOL),
                        Mangler.mangleName(getService()) + "_topic_name_set", getDomainNamespace(), setTopicName);
            }
        }
    }
}
