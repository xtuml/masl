package org.xtuml.masl.translate.kafka;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition.Mode;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.Types;

public class DomainTerminatorServiceTranslator extends ServiceTranslator {

    private final CodeFile codeFile;

    private Function acceptFn;
    private Variable consumer;
    private Function overrider;
    private Variable register;
    private Class consumerClass;

    DomainTerminatorServiceTranslator(DomainTerminatorService service, DomainTranslator domainTranslator,
            ParameterSerializer serializer, CodeFile codeFile) {
        super(service, domainTranslator, serializer);
        this.codeFile = codeFile;
    }

    @Override
    DomainTerminatorService getService() {
        return (DomainTerminatorService) super.getService();
    }

    @Override
    List<Runnable> getFilePopulators() {
        return List.of(() -> codeFile.addClassDeclaration(consumerClass),
                () -> codeFile.addVariableDefinition(consumer), () -> codeFile.addFunctionDefinition(acceptFn),
                () -> codeFile.addFunctionDefinition(overrider), () -> codeFile.addVariableDefinition(register));
    }

    @Override
    void translate() {

        // if the service has a single string parameter, just pass through without
        // parsing
        final boolean noParse = getService().getParameters().size() == 1
                && getService().getParameters().get(0).getType().isAssignableFrom(StringType.createAnonymous());

        // create and add the consumer class to the file
        consumerClass = new Class(Mangler.mangleName(getService()) + "Consumer", getDomainNamespace());
        consumerClass.addSuperclass(Kafka.dataConsumerClass, Visibility.PUBLIC);
        final DeclarationGroup functions = consumerClass.createDeclarationGroup();

        // create the constructor
        final Function constructor = consumerClass.createConstructor(functions, Visibility.PUBLIC);
        constructor.declareInClass(true);

        // create the accept function
        acceptFn = consumerClass.createMemberFunction(functions, "accept", Visibility.PUBLIC);
        final Expression data = acceptFn.createParameter(new TypeUsage(Std.vector(new TypeUsage(Std.uint8))), "data")
                .asExpression();
        acceptFn.setConst(true);

        // create the overrider function
        overrider = new Function(Mangler.mangleName(getService()), getDomainNamespace());
        overrider.setReturnType(Types.getInstance().getType(getService().getReturnType()));

        final List<Expression> consumerArgs = new ArrayList<>();
        final List<Variable> deserializeVars = new ArrayList<>();
        final DeclarationGroup vars = consumerClass.createDeclarationGroup();
        for (final ParameterDefinition param : getService().getParameters()) {
            final TypeUsage type = Types.getInstance().getType(param.getType());

            // add the parameter to the overrider function
            final ParameterTranslator paramTrans = new ParameterTranslator(param, overrider);

            // only process "out" parameters
            if (param.getMode() == Mode.OUT) {

                // capture each parameter as a member variable
                final Variable constructorParam = constructor.createParameter(type.getReferenceType(),
                        Mangler.mangleName(param));
                final Variable memberVar = consumerClass.createMemberVariable(vars, Mangler.mangleName(param),
                        type.getReferenceType(), Visibility.PRIVATE);
                constructor.setInitialValue(memberVar, constructorParam.asExpression());

                // parse out each parameter and assign it to the member variable
                if (isTypeSerializable(param.getType().getBasicType())) {
                    deserializeVars.add(memberVar);
                }

                // add to the list for the consumer call
                consumerArgs.add(paramTrans.getVariable().asExpression());
            }
        }

        // add the deserialization code
        if (noParse) {
            final Expression paramAccess = Std.string.callConstructor(new Function("begin").asFunctionCall(data, false),
                    new Function("end").asFunctionCall(data, false));
            acceptFn.getCode().appendStatement(
                    new BinaryExpression(deserializeVars.get(0).asExpression(), BinaryOperator.ASSIGN, paramAccess)
                            .asStatement());
        } else {
            getParameterSerializer().deserialize(data, deserializeVars, acceptFn.getCode());
        }

        // create consumer instance
        consumer = new Variable(new TypeUsage(Kafka.consumerClass), "consumer_" + Mangler.mangleName(getService()),
                getDomainNamespace(),
                Kafka.consumerClass.callConstructor(Std.string.callConstructor(getTopicName(getService()))));

        // add the call to consume to the overrider
        final Variable dataConsumer = new Variable(new TypeUsage(consumerClass), "dataConsumer",
                consumerClass.callConstructor(consumerArgs));
        overrider.getCode().appendStatement(new VariableDefinitionStatement(dataConsumer));
        overrider.getCode().appendStatement(new ReturnStatement(new Function("consumeOne")
                .asFunctionCall(consumer.asExpression(), false, dataConsumer.asExpression())));

        // register the overrider function
        register = new Variable(new TypeUsage(FundamentalType.BOOL), "register_" + Mangler.mangleName(getService()),
                getDomainNamespace(), TerminatorServiceTranslator.getInstance(getService()).getRegisterOverride()
                        .asFunctionCall(overrider.asFunctionPointer()));
    }
}
