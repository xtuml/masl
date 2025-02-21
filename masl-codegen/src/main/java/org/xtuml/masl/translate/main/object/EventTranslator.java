/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.object;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.InstanceType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventTranslator {

    EventTranslator(final ObjectTranslator objectTranslator,
                    final StateMachineTranslator smTranslator,
                    final EventDeclaration event,
                    final Expression eventId) {
        this.event = event;
        domainTranslator = objectTranslator.getDomainTranslator();
        this.objectTranslator = objectTranslator;
        this.smTranslator = smTranslator;
        this.eventId = eventId;
    }

    public EventDeclaration getEvent() {
        return event;
    }

    public Expression getEventId() {
        return eventId;
    }

    public ObjectTranslator getObjectTranslator() {
        return objectTranslator;
    }

    public Function getProcessFunction() {
        return processFunction;
    }

    public Function getCreateFunction() {
        return createFunction;
    }

    private void addProcessFunction() {
        processFunction = objectTranslator.getMain().createProcessFunction(event);

        final List<Expression> stateArgs = new ArrayList<>();
        for (final ParameterDefinition param : event.getParameters()) {
            final TypeUsage type = domainTranslator.getTypes().getType(param.getType());
            final Variable
                    parameter =
                    processFunction.createParameter(type.getOptimalParameterType(), Mangler.mangleName(param));
            stateArgs.add(parameter.asExpression());
        }

        // Generate state action calls if a state machine exists - it might not for
        // a polymorphic object
        if (smTranslator != null) {
            processFunction.getCode().appendStatement(smTranslator.getProcessEventCode(event, stateArgs));
        }
    }

    public Class getEventClass() {
        return eventClass;
    }

    void addEventClass() {
        consumeFunction = objectTranslator.getMain().createConsumeFunction(event);

        eventClass = objectTranslator.getMain().createEventClass(event);
        final DeclarationGroup group = eventClass.createDeclarationGroup();

        if (event.getParameters().size() > 0) {
            final Function defaultConstructor = eventClass.createConstructor(group, Visibility.PUBLIC);
            objectTranslator.getMain().getBodyFile().addFunctionDefinition(defaultConstructor);
        }

        eventConstructor = eventClass.createConstructor(group, Visibility.PUBLIC);
        objectTranslator.getMain().getBodyFile().addFunctionDefinition(eventConstructor);
        eventInvoker = eventClass.redefineFunction(group, Architecture.event.getInvoke(), Visibility.PUBLIC);
        objectTranslator.getMain().getBodyFile().addFunctionDefinition(eventInvoker);

        final List<Expression> processArgs = new ArrayList<>();
        final List<Expression> consumeArgs = new ArrayList<>();

        if (event.getType() == EventDeclaration.Type.NORMAL) {
            final Variable consumeParam = consumeFunction.createParameter(new TypeUsage(Architecture.ID_TYPE), "id");

            consumeArgs.add(Architecture.event.getGetDestInstanceId().asFunctionCall());

            final Variable
                    instance =
                    new Variable(objectTranslator.getPointerType(),
                                 "instance",
                                 objectTranslator.getPopulation().getGetInstance().asFunctionCall(consumeParam.asExpression()));
            consumeFunction.getCode().appendStatement(instance.asStatement());
            consumeFunction.getCode().appendStatement(new IfStatement(instance.asExpression(),
                                                                      new ExpressionStatement(new BinaryExpression(
                                                                              instance.asExpression(),
                                                                              BinaryOperator.PTR_REF,
                                                                              processFunction.asFunctionCall(processArgs))),
                                                                      null));
        } else {
            consumeFunction.getCode().appendExpression(processFunction.asFunctionCall(processArgs));
        }

        eventInvoker.getCode().appendExpression(consumeFunction.asFunctionCall(consumeArgs));

        for (final ParameterDefinition param : event.getParameters()) {
            TypeUsage type = domainTranslator.getTypes().getType(param.getType());

            // When an event parameter is an instance type, the generated event code
            // will need to create an member variable to hold the instance. This will
            // be a "SWA::ObjectPtr<...>" . This definition will cause the header file
            // for the instance type to be included. This can cause build problems due
            // to inter-dependent header files. The "SWA::ObjectPtr<...> declaration
            // doesn't actually need the full class definition, just a forward
            // reference
            // to the class. Therefore when an instance type is detected make sure the
            // type
            // is set as a template reference only.
            if (param.getType() instanceof InstanceType) {
                type = type.getTemplateRefOnly();
            }

            final Variable
                    memberVar =
                    eventClass.createMemberVariable(group, Mangler.mangleName(param), type, Visibility.PRIVATE);

            final Function
                    getter =
                    eventClass.createMemberFunction(group, "get" + Mangler.mangleName(param), Visibility.PUBLIC);
            getter.setReturnType(type.getOptimalParameterType());
            getter.getCode().appendStatement(new ReturnStatement(memberVar.asExpression()));
            getter.declareInClass(true);
            getter.setConst(true);

            getters.put(param, getter);

            final Variable consumerParam = consumeFunction.createParameter(type, Mangler.mangleName(param));

            consumeArgs.add(memberVar.asExpression());
            processArgs.add(consumerParam.asExpression());

            final Variable
                    constructorParam =
                    eventConstructor.createParameter(type.getOptimalParameterType(), Mangler.mangleName(param));

            eventConstructor.setInitialValue(memberVar, constructorParam.asExpression());
            eventConstructor.getCode().appendExpression(Architecture.event.getAddParam().asFunctionCall(memberVar.asMemberReference(
                    eventClass.getThis().asExpression(),
                    true)));

        }
    }

    private Class eventClass;
    private final Expression eventId;
    private Function consumeFunction;
    private Function eventInvoker;
    private Function eventConstructor;
    private final Map<ParameterDefinition, Function> getters = new HashMap<>();

    public Function getParamGetter(final ParameterDefinition param) {
        return getters.get(param);
    }

    public Function getConsumeFunction() {
        return consumeFunction;
    }

    public Function getEventConstructor() {
        return eventConstructor;
    }

    public Function getEventInvoker() {
        return eventInvoker;
    }

    Function addCreateFunction() {
        if (event.getType() == EventDeclaration.Type.NORMAL) {
            createFunction = objectTranslator.getMain().addCreateEventFunction(event);
        } else {
            createFunction = objectTranslator.getPopulation().addCreateEventFunction(event);
        }

        createFunction.setReturnType(new TypeUsage(Architecture.event.getEventPtr()));

        addEventClass();

        final List<Expression> eventParams = new ArrayList<>();
        for (final ParameterDefinition param : event.getParameters()) {
            final TypeUsage type = domainTranslator.getTypes().getType(param.getType());
            final Variable
                    generateVar =
                    createFunction.createParameter(type.getOptimalParameterType(), Mangler.mangleName(param));
            eventParams.add(generateVar.asExpression());
        }

        final Expression minusOne = new UnaryExpression(UnaryOperator.MINUS, Literal.ONE);
        final Expression
                sourceObj =
                createFunction.createParameter(new TypeUsage(FundamentalType.INT),
                                               "sourceObj",
                                               minusOne).asExpression();
        final Expression
                sourceInst =
                createFunction.createParameter(new TypeUsage(Architecture.ID_TYPE),
                                               "sourceInstance",
                                               Literal.ZERO).asExpression();
        final Variable
                eventVar =
                new Variable(new TypeUsage(Architecture.event.getEventPtr()),
                             "event",
                             new Expression[]{new NewExpression(new TypeUsage(eventClass), eventParams)});

        createFunction.getCode().appendStatement(eventVar.asStatement());

        if (event.getType() == EventDeclaration.Type.NORMAL) {
            final Expression
                    setDest =
                    Architecture.event.getSetDest().asFunctionCall(eventVar.asExpression(),
                                                                   true,
                                                                   objectTranslator.getMain().getArchitectureId());
            createFunction.getCode().appendExpression(setDest);
        }

        final Expression
                setSource =
                Architecture.event.getSetSource().asFunctionCall(eventVar.asExpression(), true, sourceObj, sourceInst);

        createFunction.getCode().appendStatement(new IfStatement(new BinaryExpression(sourceObj,
                                                                                      BinaryOperator.NOT_EQUAL,
                                                                                      minusOne),
                                                                 setSource.asStatement()));

        createFunction.getCode().appendStatement(new ReturnStatement(eventVar.asExpression()));

        return createFunction;
    }

    boolean wrapEventParamsInTuple(final EventDeclaration event) {
        return event.getParameters().size() +
               (event.getType() == EventDeclaration.Type.NORMAL ?
                objectTranslator.getMain().getGetInstance().getParameters().size() :
                0) > Boost.MAX_BIND_PARAMS;
    }

    void translate() {
        addProcessFunction();
        addCreateFunction();
    }

    private final StateMachineTranslator smTranslator;

    private final EventDeclaration event;

    private final ObjectTranslator objectTranslator;
    private final DomainTranslator domainTranslator;

    private Function processFunction;
    private Function createFunction;

}
