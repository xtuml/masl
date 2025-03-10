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

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.expression.FindParameterExpression;
import org.xtuml.masl.metamodel.expression.InstanceOrderingExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.*;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.expression.PredicateNameMangler;

import java.util.*;

public class Main {

    public Main(final ObjectTranslator translator) {
        domainTranslator = translator.getDomainTranslator();

        // The project build contains terminators that can contain an
        // implementation that references objects of the domain. Therefore
        // need to expose the object header fiels to enable project C++ compiles
        // to find the required object header files.

        // TODO - when terminators in a project build can nolonger access objects
        // replace line
        // with the one below so that object heaser files are not publically
        // exposed.
        // orginal line headerFile =
        // CodeFiles.getHeaderFile(Mangler.mangleFile(translator.getObjectDeclaration().getName()),
        // null);
        headerFile =
                domainTranslator.getLibrary().createPrivateHeader(Mangler.mangleFile(translator.getObjectDeclaration()));
        bodyFile = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(translator.getObjectDeclaration()));

        this.translator = translator;

        scope = new Scope(translator);

        namespace = DomainNamespace.get(translator.getObjectDeclaration().getDomain());

        name = Mangler.mangleName(translator.getObjectDeclaration());
        mainClass = new Class(name, namespace);
        nestedTypes = mainClass.createDeclarationGroup();
        creators = mainClass.createDeclarationGroup("Instance Creation");
        retrieval = mainClass.createDeclarationGroup("Instance Retrieval");

        relationshipNavs = mainClass.createDeclarationGroup("Relationship Navigators");
        relationshipCounts = mainClass.createDeclarationGroup("Relationship Counters");
        relationshipLinkers = mainClass.createDeclarationGroup("Relationship Linkers");
        setters = mainClass.createDeclarationGroup("Setters for each object attribute");
        getters = mainClass.createDeclarationGroup("Getters for each object attribute");

        objectServices = mainClass.createDeclarationGroup("Object Services");
        instanceServices = mainClass.createDeclarationGroup("Instance Services");

        orderByPredicates = mainClass.createDeclarationGroup("OrderBy Predicates");
        findPredicates = mainClass.createDeclarationGroup("Find Predicates");
        finders = mainClass.createDeclarationGroup("Find Functions");

        constructors = mainClass.createDeclarationGroup("Constructors and Destructors");
        copyPreventers = mainClass.createDeclarationGroup("Prevent copy");
        stateMachine = mainClass.createDeclarationGroup("State Machine");
        stateActions = mainClass.createDeclarationGroup("State Actions");
        generateEvent = mainClass.createDeclarationGroup("Generate Events");
        consumeEvent = mainClass.createDeclarationGroup("Consume Events");
        processEvent = mainClass.createDeclarationGroup("Process Events");
        delayedCreateEvent = mainClass.createDeclarationGroup("Delayed Create Events");
        delayedGenerateEvent = mainClass.createDeclarationGroup("Delayed Generate Events");
        idEnums = mainClass.createDeclarationGroup("Id Enumerations");

        headerFile.addClassDeclaration(mainClass);
    }

    private final Namespace namespace;

    public Class createEventClass(final EventDeclaration event) {
        final String
                name =
                "Event_" +
                Mangler.mangleName(translator.getObjectDeclaration()) +
                "_" +
                (event.getParentObject() != translator.getObjectDeclaration() ?
                 Mangler.mangleName(event.getParentObject()) + "_" :
                 "") +
                Mangler.mangleName(event);
        final Class eventClass = new Class(name, namespace);
        getEventHeaderFile().addClassDeclaration(eventClass);

        eventClass.addSuperclass(Architecture.event.getClazz(), Visibility.PUBLIC);

        final DeclarationGroup group = eventClass.createDeclarationGroup("Get Id Overrides");

        final Function
                getDomainId =
                eventClass.redefineFunction(group, Architecture.event.getGetDomainId(), Visibility.PUBLIC);
        getDomainId.getCode().appendStatement(new ReturnStatement(domainTranslator.getDomainId()));
        getBodyFile().addFunctionDefinition(getDomainId);

        final Function
                getObjectId =
                eventClass.redefineFunction(group, Architecture.event.getGetObjectId(), Visibility.PUBLIC);
        getObjectId.getCode().appendStatement(new ReturnStatement(translator.getObjectId()));
        getBodyFile().addFunctionDefinition(getObjectId);

        final Function
                getEventId =
                eventClass.redefineFunction(group, Architecture.event.getGetEventId(), Visibility.PUBLIC);
        getEventId.getCode().appendStatement(new ReturnStatement(translator.getEventId(event)));
        getBodyFile().addFunctionDefinition(getEventId);

        return eventClass;
    }

    public Function createConsumeFunction(final EventDeclaration event) {
        final Function
                consumeFunction =
                mainClass.createStaticFunction(consumeEvent,
                                               "consume_" +
                                               Mangler.mangleName(event.getParentObject()) +
                                               "_" +
                                               Mangler.mangleName(event),
                                               Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(consumeFunction);

        return consumeFunction;
    }

    public Function addCreateEventFunction(final EventDeclaration event) {
        Function generateFunction = null;
        if (event.getType() == EventDeclaration.Type.NORMAL) {
            generateFunction =
                    mainClass.createMemberFunction(generateEvent,
                                                   "create_" +
                                                   Mangler.mangleName(event.getParentObject()) +
                                                   "_" +
                                                   Mangler.mangleName(event),
                                                   Visibility.PUBLIC);
        }
        bodyFile.addFunctionDefinition(generateFunction);
        return generateFunction;
    }

    public Function addGenerateDelayedFunction(final EventDeclaration event) {
        if (event.getType() != EventDeclaration.Type.NORMAL) {
            throw new IllegalStateException("Creation Event must be associated with Population class : " + event);
        }

        Function generateDelayedFunction = null;
        if (event.getParameters().size() > 0) {
            generateDelayedFunction =
                    mainClass.createMemberFunction(delayedGenerateEvent,
                                                   "generate_delayed_" +
                                                   Mangler.mangleName(event.getParentObject()) +
                                                   "_" +
                                                   Mangler.mangleName(event),
                                                   Visibility.PUBLIC);
            generateDelayedFunction.setPure(true);
        }
        return generateDelayedFunction;
    }

    public Function addCreateDelayedFunction(final EventDeclaration event) {
        if (event.getType() != EventDeclaration.Type.NORMAL) {
            throw new IllegalStateException("Creation Event must be associated with Population class : " + event);
        }

        Function createDelayedFunction;
        createDelayedFunction =
                mainClass.createMemberFunction(delayedCreateEvent,
                                               "create_delayed_" +
                                               Mangler.mangleName(event.getParentObject()) +
                                               "_" +
                                               Mangler.mangleName(event),
                                               Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(createDelayedFunction);
        return createDelayedFunction;
    }

    public Function createProcessFunction(final EventDeclaration event) {
        // If the event is a normal event, need to make this public so that any
        // superclass process function can call it. Would
        // probably be better as a friend function of the superclass, but that
        // would mean having to have the full superclass definition visible, not
        // just the forward declaration as now.

        Function processFunction;
        if (event.getType() == EventDeclaration.Type.NORMAL) {
            processFunction =
                    mainClass.createMemberFunction(processEvent,
                                                   "process_" +
                                                   Mangler.mangleName(event.getParentObject()) +
                                                   "_" +
                                                   Mangler.mangleName(event),
                                                   Visibility.PUBLIC);
        } else {
            processFunction =
                    mainClass.createStaticFunction(processEvent,
                                                   "process_" +
                                                   Mangler.mangleName(event.getParentObject()) +
                                                   "_" +
                                                   Mangler.mangleName(event),
                                                   Visibility.PRIVATE);

        }
        bodyFile.addFunctionDefinition(processFunction);

        return processFunction;
    }

    public String getName() {
        return this.name;
    }

    Function addArchitectureId() {
        final Function
                getArchitectureId =
                mainClass.createMemberFunction(getters, "getArchitectureId", Visibility.PUBLIC);
        getArchitectureId.setPure(true);
        getArchitectureId.setReturnType(translator.getIdType());
        getArchitectureId.setConst(true);

        architectureId = getArchitectureId.asFunctionCall();

        return getArchitectureId;
    }

    Expression getArchitectureId() {
        return architectureId;
    }

    Function addAttributeGetter(final AttributeDeclaration att) {
        final TypeUsage type = domainTranslator.getTypes().getType(att.getType());

        final Function
                getter =
                mainClass.createMemberFunction(getters, "get_" + Mangler.mangleName(att), Visibility.PUBLIC);
        getter.setPure(true);
        getter.setReturnType(type);
        getter.setConst(true);

        return getter;
    }

    void addReferentialAttributeGetter(final AttributeDeclaration att, final Function getter) {
        getter.setPure(false);
        Expression attValue = null;
        for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
            final Expression
                    relatedObj =
                    translator.getRelationshipTranslator(refAtt.getRelationship()).getPublicAccessors().getNavigateFunction().asFunctionCall();
            final ObjectDeclaration destObj = refAtt.getRelationship().getDestinationObject();
            final AttributeDeclaration destAtt = refAtt.getDestinationAttribute();
            final Function attGetter = domainTranslator.getObjectTranslator(destObj).getAttributeGetter(destAtt);
            final Expression thisValue = attGetter.asFunctionCall(relatedObj, true);
            if (attValue == null) {
                attValue = thisValue;
            } else {
                attValue = new ConditionalExpression(relatedObj, thisValue, attValue);
            }
        }
        getter.getCode().appendStatement(new ReturnStatement(attValue));

        bodyFile.addFunctionDefinition(getter);
    }

    Function addAttributeSetter(final AttributeDeclaration att) {
        final TypeUsage type = domainTranslator.getTypes().getType(att.getType());

        final Function
                setter =
                mainClass.createMemberFunction(setters, "set_" + Mangler.mangleName(att), Visibility.PUBLIC);
        setter.setPure(true);
        setter.createParameter(type.getOptimalParameterType(), "value");
        return setter;
    }

    void addConstructors() {
        final Function constructor = mainClass.createConstructor(constructors, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(constructor);

        mainClass.createCopyConstructor(copyPreventers, Visibility.PRIVATE);
        mainClass.createAssignmentOperator(copyPreventers, Visibility.PRIVATE);

        final Function destructor = mainClass.createDestructor(constructors, Visibility.PUBLIC);
        destructor.setVirtual(true);
        bodyFile.addFunctionDefinition(destructor);

    }

    Function addCreateInstance() {
        final Function forwarder = addPopulationForwarder(population.addCreateInstance(), creators);
        final StatementGroup useUniques = new StatementGroup();
        forwarder.getCode().prependStatement(useUniques);
        for (final AttributeDeclaration att : translator.getObjectDeclaration().getAttributes()) {
            final Function useUniqueId = translator.getUseUniqueId(att);
            if (useUniqueId != null) {
                forwarder.getCode().prependStatement(useUniqueId.asFunctionCall(population.getCreateInstanceParam(att)).asStatement());
            }
        }
        return forwarder;
    }

    Function addDeleteInstance() {
        final Variable
                isDeletedFlag =
                mainClass.createMemberVariable(creators,
                                               "isDeletedFlag",
                                               new TypeUsage(FundamentalType.BOOL),
                                               Visibility.PRIVATE);

        getIsDeleted = mainClass.createMemberFunction(creators, "isDeleted", Visibility.PUBLIC);
        getIsDeleted.setReturnType(new TypeUsage(FundamentalType.BOOL));
        getIsDeleted.declareInClass(true);
        getIsDeleted.setConst(true);
        getIsDeleted.getCode().appendStatement(new ReturnStatement(isDeletedFlag.asExpression()));

        final Function popDelete = population.addDeleteInstance();

        final Function deleteInstance = mainClass.createMemberFunction(creators, "deleteInstance", Visibility.PUBLIC);

        if (domainTranslator.addRefIntegChecks() || CommandLine.INSTANCE.isForTest()) {
            // Check for dangling relationships
            for (final RelationshipSpecification rel : translator.getObjectDeclaration().getRelationships()) {
                final Expression
                        count =
                        translator.getRelationshipTranslator(rel).getPublicAccessors().getCountFunction().asFunctionCall();
                final ThrowStatement
                        throwStatement =
                        new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                                "Cannot delete instance - relationship " +
                                rel.getRelationship().getName() +
                                " still linked")));
                deleteInstance.getCode().appendStatement(new IfStatement(count, throwStatement));
            }
        }

        deleteInstance.getCode().appendStatement(popDelete.asFunctionCall(population.getSingleton(),
                                                                          false,
                                                                          translator.createPointer(mainClass.getThis().asExpression())).asStatement());
        for (final AttributeDeclaration att : translator.getObjectDeclaration().getAttributes()) {
            if (att.getType().getBasicType().getActualType() == ActualType.TIMER) {
                deleteInstance.getCode().appendStatement(Architecture.Timer.deleteTimer(translator.getAttributeGetter(
                        att).asFunctionCall()).asStatement());
            }
        }

        deleteInstance.getCode().appendStatement(new BinaryExpression(isDeletedFlag.asExpression(),
                                                                      BinaryOperator.ASSIGN,
                                                                      Literal.TRUE).asStatement());
        bodyFile.addFunctionDefinition(deleteInstance);

        return deleteInstance;
    }

    Function addGetPopulationSize() {
        return addPopulationForwarder("getPopulationSize", population.addSize(), creators);
    }

    Function addFindFunction(final org.xtuml.masl.metamodel.expression.Expression predicate,
                             final FindExpression.Type type) {
        final Function fn = addPopulationForwarder(population.addFindFunction(predicate, type), finders);
        if (predicate != null) {
            fn.setComment("MASL find: " + predicate);
        }
        return fn;
    }

    Function addFindPredicate(final org.xtuml.masl.metamodel.expression.Expression predicate) {
        final Function
                predicateFn =
                mainClass.createMemberFunction(findPredicates,
                                               "findPredicate_" +
                                               PredicateNameMangler.createMangler(predicate).getName(),
                                               Visibility.PUBLIC);
        predicateFn.setConst(true);

        final Scope fnScope = new Scope(scope);

        // This function is going to be called by way of boost::bind, which has a
        // maximum number of parameters, so if we would exceed this number (after
        // including the object to bind to at the start) then use a tuple.
        if (predicate.getFindParameters().size() + 1 > Boost.MAX_BIND_PARAMS) {
            final List<TypeUsage> tupleTypes = new ArrayList<>(predicate.getFindParameters().size());
            for (final FindParameterExpression maslParam : predicate.getFindParameters()) {
                tupleTypes.add(domainTranslator.getTypes().getType(maslParam.getType()));
            }
            final BigTuple tuple = new BigTuple(tupleTypes);
            final Variable
                    params =
                    predicateFn.createParameter(tuple.getTupleType().getOptimalParameterType(), "params");

            int i = 0;
            for (final FindParameterExpression maslParam : predicate.getFindParameters()) {
                fnScope.addFindParameter(maslParam, tuple.getTupleGetter(params.asExpression(), i++));
            }

        } else {
            for (final FindParameterExpression maslParam : predicate.getFindParameters()) {
                final Variable
                        param =
                        predicateFn.createParameter(domainTranslator.getTypes().getType(maslParam.getType()).getOptimalParameterType(),
                                                    maslParam.getName());

                fnScope.addFindParameter(maslParam, param.asExpression());
            }
        }

        Expression predicateExpression = ExpressionTranslator.createTranslator(predicate, fnScope).getReadExpression();

        predicateExpression =
                new BinaryExpression(new UnaryExpression(UnaryOperator.NOT, getIsDeleted.asFunctionCall()),
                                     BinaryOperator.AND,
                                     predicateExpression);

        predicateFn.getCode().appendStatement(new ReturnStatement(predicateExpression));
        predicateFn.declareInClass(true);
        predicateFn.setReturnType(new TypeUsage(FundamentalType.BOOL));

        predicateFn.setComment("MASL find: " + predicate);

        return predicateFn;
    }

    Function addGetCurrentState(final boolean assigner, final EnumerationType stateEnum) {
        final TypeUsage type = new TypeUsage(stateEnum);
        Function getter;
        if (!assigner) {

            getter = mainClass.createMemberFunction(stateMachine, "getCurrentState", Visibility.PUBLIC);
            getter.setPure(true);
            getter.setReturnType(type);
            getter.setConst(true);
        } else {
            getter = addPopulationForwarder(population.addGetAssignerState(stateEnum), getters);
        }
        return getter;
    }

    Function addGetInstance() {
        getInstance = addPopulationForwarder(population.addGetInstance(), retrieval);
        return getInstance;
    }

    Function addGetNextArchId() {
        return addPopulationForwarder(population.addGetNextArchId(), creators);
    }

    Function addGetUniqueId(final AttributeDeclaration att) {
        return addPopulationForwarder(population.addGetUniqueId(att), creators);
    }

    Function addUseUniqueId(final AttributeDeclaration att) {
        return addPopulationForwarder(population.addUseUniqueId(att), creators);
    }

    void addIdEnumeration(final EnumerationType enumeration) {
        mainClass.addEnumeration(idEnums, enumeration, Visibility.PUBLIC);
    }

    Function addOrderByPredicate(final List<? extends InstanceOrderingExpression.Component> attributeOrder) {
        String fnName = "orderByPredicate_";

        for (final InstanceOrderingExpression.Component orderAttribute : attributeOrder) {
            fnName =
                    fnName +
                    (orderAttribute.isReverse() ? "r_" : "") +
                    Mangler.mangleName(orderAttribute.getAttribute());
        }
        if (attributeOrder.size() == 0) {
            fnName = fnName + "preferred";
        }

        final Function predicateFn = mainClass.createMemberFunction(orderByPredicates, fnName, Visibility.PUBLIC);
        predicateFn.setConst(true);

        final Expression
                rhsObj =
                predicateFn.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "rhs").asExpression();

        Expression predicateExpression = null;

        if (attributeOrder.size() == 0) {
            final List<AttributeDeclaration>
                    reversedAttributes =
                    new ArrayList<>(translator.getObjectDeclaration().getPreferredIdentifier().getAttributes());
            Collections.reverse(reversedAttributes);
            for (final AttributeDeclaration orderAttribute : reversedAttributes) {
                final Function getter = translator.getAttributeGetter(orderAttribute);
                final Expression lhs = getter.asFunctionCall();
                final Expression rhs = getter.asFunctionCall(rhsObj, false);
                predicateExpression = Structure.buildComparator(predicateExpression, lhs, rhs, false);
            }
        } else {
            final List<InstanceOrderingExpression.Component> reversedAttributes = new ArrayList<>(attributeOrder);
            Collections.reverse(reversedAttributes);
            for (final InstanceOrderingExpression.Component orderAttribute : reversedAttributes) {
                final Function getter = translator.getAttributeGetter(orderAttribute.getAttribute());
                final Expression lhs = getter.asFunctionCall();
                final Expression rhs = getter.asFunctionCall(rhsObj, false);
                predicateExpression =
                        Structure.buildComparator(predicateExpression, lhs, rhs, orderAttribute.isReverse());
            }
        }
        predicateFn.getCode().appendStatement(new ReturnStatement(predicateExpression));
        predicateFn.declareInClass(true);
        predicateFn.setReturnType(new TypeUsage(FundamentalType.BOOL));

        return predicateFn;
    }

    Population addPopulation() {
        population = new Population(translator);
        return population;
    }

    Function addPopulationForwarder(final Function populationFunction, final DeclarationGroup group) {
        return addPopulationForwarder(populationFunction.getName(), populationFunction, group);
    }

    Function addPopulationForwarder(final String newName,
                                    final Function populationFunction,
                                    final DeclarationGroup group) {
        final Function forwarder = mainClass.createStaticFunction(group, newName, Visibility.PUBLIC);
        forwarder.setReturnType(populationFunction.getReturnType());

        final List<Expression> params = new ArrayList<>();
        for (final Variable populationParam : populationFunction.getParameters()) {
            final Variable param = forwarder.createParameter(populationParam.getType(), populationParam.getName());
            params.add(param.asExpression());
        }

        if (populationFunction.getReturnType() == TypeUsage.VOID) {
            forwarder.getCode().appendStatement(populationFunction.asFunctionCall(population.getSingleton(),
                                                                                  false,
                                                                                  params).asStatement());
        } else {
            forwarder.getCode().appendStatement(new ReturnStatement(populationFunction.asFunctionCall(population.getSingleton(),
                                                                                                      false,
                                                                                                      params)));
        }
        bodyFile.addFunctionDefinition(forwarder);

        return forwarder;
    }

    Function addRelationshipCorr(final RelationshipSpecification spec,
                                 final TypeUsage rhsType,
                                 final TypeUsage assocType) {
        final String
                navName =
                "correlate_" +
                spec.getRelationship().getName() +
                "_" +
                spec.getRole() +
                "_" +
                spec.getDestinationObject().getName();
        final Function correlator = mainClass.createMemberFunction(relationshipNavs, navName, Visibility.PUBLIC);

        correlator.setConst(true);
        correlator.setPure(true);
        correlator.createParameter(rhsType, "rhs");
        correlator.setReturnType(assocType);

        return correlator;
    }

    Function addRelationshipLink(final boolean unlink, final RelationshipSpecification forwardSpec) {
        return addRelationshipLink(unlink, forwardSpec, null, null);
    }

    Function addRelationshipLink(final boolean unlink,
                                 final RelationshipSpecification forwardSpec,
                                 final TypeUsage related) {
        return addRelationshipLink(unlink, forwardSpec, related, null);
    }

    Function addRelationshipLink(final boolean unlink,
                                 final RelationshipSpecification forwardSpec,
                                 final TypeUsage relatedType,
                                 final TypeUsage assocType) {
        final String
                linkName =
                (unlink ? "un" : "") +
                "link_" +
                forwardSpec.getRelationship().getName() +
                "_" +
                forwardSpec.getRole() +
                "_" +
                forwardSpec.getDestinationObject().getName();
        final Function linker = mainClass.createMemberFunction(relationshipLinkers, linkName, Visibility.PUBLIC);
        linker.setPure(true);

        if (relatedType != null) {
            linker.createParameter(relatedType, "rhs");
        }

        if (assocType != null) {
            linker.createParameter(assocType, "assoc");
        }

        return linker;
    }

    Function addRelationshipNav(final RelationshipSpecification spec, final TypeUsage related) {
        final String
                navName =
                "navigate_" +
                spec.getRelationship().getName() +
                "_" +
                spec.getRole() +
                "_" +
                spec.getDestinationObject().getName();
        final Function navigator = mainClass.createMemberFunction(relationshipNavs, navName, Visibility.PUBLIC);

        navigator.setConst(true);
        navigator.setPure(true);

        if (spec.getCardinality() == MultiplicityType.ONE) {
            navigator.setReturnType(related);
        } else {
            navigator.setReturnType(new TypeUsage(Architecture.set(related)));
        }

        return navigator;
    }

    Function addRelationshipCount(final RelationshipSpecification spec, final TypeUsage related) {
        final String
                cardName =
                "count_" +
                spec.getRelationship().getName() +
                "_" +
                spec.getRole() +
                "_" +
                spec.getDestinationObject().getName();
        final Function navigator = mainClass.createMemberFunction(relationshipCounts, cardName, Visibility.PUBLIC);

        navigator.setConst(true);
        navigator.setPure(true);
        navigator.setReturnType(new TypeUsage(Std.size_t));
        return navigator;
    }

    Function addService(final ObjectService service) {
        if (service.isInstance()) {
            return mainClass.createMemberFunction(instanceServices, Mangler.mangleName(service), Visibility.PUBLIC);
        } else {
            return mainClass.createStaticFunction(objectServices, Mangler.mangleName(service), Visibility.PUBLIC);
        }
    }

    Function addSetCurrentState(final boolean assigner, final EnumerationType stateEnum) {
        final TypeUsage type = new TypeUsage(stateEnum);
        Function setter;
        if (!assigner) {
            setter = mainClass.createMemberFunction(stateMachine, "setCurrentState", Visibility.PUBLIC);
            setter.setPure(true);
            setter.createParameter(type.getOptimalParameterType(), "newState");
        } else {
            setter = addPopulationForwarder(population.addSetAssignerState(stateEnum), setters);
        }
        return setter;
    }

    EnumerationType addStateEnum(final boolean isAssigner) {
        final EnumerationType stateEnum = new EnumerationType((isAssigner ? "Assigner" : "") + "Type");

        mainClass.addEnumeration(nestedTypes, stateEnum, Visibility.PUBLIC);

        return stateEnum;
    }

    Function addStateFunction(final State state) {
        if (state.getType() == State.Type.ASSIGNER ||
            state.getType() == State.Type.ASSIGNER_START ||
            state.getType() == State.Type.CREATION) {
            return mainClass.createStaticFunction(stateActions,
                                                  "state_" + Mangler.mangleName(state),
                                                  Visibility.PRIVATE);
        } else {
            return mainClass.createMemberFunction(stateActions,
                                                  "state_" + Mangler.mangleName(state),
                                                  Visibility.PRIVATE);
        }
    }

    Function getGetInstance() {
        return getInstance;
    }

    Scope getScope() {
        return scope;
    }

    Scope getServiceScope(final ObjectService service) {
        return serviceScopes.get(service);
    }

    Class getTheClass() {
        return mainClass;
    }

    private Expression architectureId;

    private final DeclarationGroup nestedTypes;

    private Population population;
    private final Map<ObjectService, Scope> serviceScopes = new HashMap<>();

    private final CodeFile bodyFile;
    private final DeclarationGroup constructors;
    private final DeclarationGroup consumeEvent;
    private final DeclarationGroup copyPreventers;
    private final DeclarationGroup creators;
    private final DeclarationGroup finders;
    private final DeclarationGroup findPredicates;

    private final DeclarationGroup generateEvent;
    private Function getInstance;
    private final DeclarationGroup getters;
    private final CodeFile headerFile;

    private final DeclarationGroup instanceServices;
    private final Class mainClass;

    private final String name;
    private final DeclarationGroup objectServices;
    private final DeclarationGroup orderByPredicates;
    private final DeclarationGroup processEvent;
    private final DeclarationGroup delayedCreateEvent;
    private final DeclarationGroup delayedGenerateEvent;

    private final DeclarationGroup idEnums;
    private final DeclarationGroup relationshipLinkers;
    private final DeclarationGroup relationshipNavs;
    private final DeclarationGroup relationshipCounts;

    private final DeclarationGroup retrieval;
    private final Scope scope;
    private final DeclarationGroup setters;
    private final DeclarationGroup stateActions;

    private final DeclarationGroup stateMachine;

    private final ObjectTranslator translator;
    private final DomainTranslator domainTranslator;

    private Function getIsDeleted;

    private CodeFile eventHeaderFile;

    public CodeFile getBodyFile() {
        return bodyFile;
    }

    private CodeFile getEventHeaderFile() {
        if (eventHeaderFile == null) {
            eventHeaderFile =
                    domainTranslator.getLibrary().createPrivateHeader(Mangler.mangleFile(translator.getObjectDeclaration()) +
                                                                      "Events");
        }
        return eventHeaderFile;
    }

    public CodeFile getHeaderFile() {
        return headerFile;
    }

}
