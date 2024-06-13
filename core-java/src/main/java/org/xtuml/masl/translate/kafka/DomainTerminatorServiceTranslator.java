package org.xtuml.masl.translate.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.xtuml.masl.cppgen.ArrayAccess;
import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition.Mode;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.NlohmannJson;
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
            CodeFile codeFile) {
        super(service, domainTranslator);
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
        // parsing JSON
        final boolean noParseJson = getService().getParameters().size() == 1
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
        final Predicate<BasicType> typeIsSerializable = paramType -> !(paramType.getBasicType()
                .getActualType() == ActualType.EVENT || paramType.getBasicType().getActualType() == ActualType.DEVICE
                || paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
        final Variable paramJson = new Variable(new TypeUsage(NlohmannJson.json), "params", NlohmannJson.parse(data));
        if (!noParseJson && getService().getParameters().stream().map(p -> p.getType().getBasicType())
                .anyMatch(typeIsSerializable)) {
            // Only build a JSON object if it will be used
            acceptFn.getCode().appendStatement(new VariableDefinitionStatement(paramJson));
        }

        // create the overrider function
        overrider = new Function(Mangler.mangleName(getService()), getDomainNamespace());
        overrider.setReturnType(Types.getInstance().getType(getService().getReturnType()));

        final List<Expression> consumerArgs = new ArrayList<>();
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
                if (typeIsSerializable.test(param.getType().getBasicType())) {
                    Expression paramAccess = Std.string.callConstructor(
                            new Function("begin").asFunctionCall(data, false),
                            new Function("end").asFunctionCall(data, false));
                    if (!noParseJson) {
                        paramAccess = NlohmannJson
                                .get(getService().getParameters().size() > 1
                                        ? new ArrayAccess(paramJson.asExpression(),
                                                Literal.createStringLiteral(param.getName()))
                                        : paramJson.asExpression(), type);
                    }
                    acceptFn.getCode().appendStatement(
                            new BinaryExpression(memberVar.asExpression(), BinaryOperator.ASSIGN, paramAccess)
                                    .asStatement());
                }

                // add to the list for the consumer call
                consumerArgs.add(paramTrans.getVariable().asExpression());
            }
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
