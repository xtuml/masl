/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.object;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;

import java.util.ArrayList;
import java.util.List;

public abstract class ConcreteObjectTranslator {

    protected ConcreteObjectTranslator(final ObjectDeclaration objectDeclaration) {
        this.objectDeclaration = objectDeclaration;
        mainDomainTranslator = DomainTranslator.getInstance(objectDeclaration.getDomain());
        mainObjectTranslator = mainDomainTranslator.getObjectTranslator(objectDeclaration);
    }

    public abstract void addRelationship(RelationshipSpecification spec, RelationshipSpecification assocSpec);

    public abstract Expression createNewInstance(List<Expression> params);

    public abstract CodeFile getBodyFile();

    public abstract CodeFile getPopulationBodyFile();

    public Function getConstructor() {
        return constructor;
    }

    public Class getMainClass() {
        return mainClass;
    }

    public Class getPopulationClass() {
        return populationClass;
    }

    public Function getPopulationConstructor() {
        return populationConstructor;
    }

    public abstract List<Expression> getPopulationConstructorSuperclassParams();

    public void translate() {
        addClasses();
        addPopulation();
        addConstructor();
        addArchitectureId();
        addAttributes();
        addCurrentState();
        addOther();

    }

    public void translateRelationships() {
        addRelationships();

        // Need to do find functions after relationships, as they may need to
        // navigate
        addFindFunctions();
        addIndexes();
    }

    protected abstract void addArchitectureId(Function getter);

    protected abstract void addAssignerState(TypeUsage stateType, Function getter, Function setter);

    protected abstract Variable addAttribute(AttributeDeclaration declaration,
                                             Function getter,
                                             Variable constructorParameter,
                                             Function setter);

    protected abstract void addCurrentState(TypeUsage stateType, Function getter, Function setter);

    protected abstract void addFindFunction(org.xtuml.masl.metamodel.expression.Expression predicate,
                                            Function function,
                                            FindExpression.Type type);

    protected void addOther() {
    }

    protected abstract void addMainClass(Class mainClass);

    protected abstract void addPopulationClass(Class mainClass);

    protected abstract void addRelationshipCorr(RelationshipSpecification rel, Function correlator);

    protected abstract void addRelationshipLink(RelationshipSpecification rel, Function linker, Function unlinker);

    protected abstract void addRelationshipNav(RelationshipSpecification rel, Function navigator);

    protected abstract void addRelationshipNav(RelationshipSpecification rel,
                                               Function navigator,
                                               org.xtuml.masl.metamodel.expression.Expression predicate);

    protected abstract void addRelationshipCount(RelationshipSpecification rel, Function navigator);

    protected abstract void addUniqueId(AttributeDeclaration declaration, Function getter, Function user);

    protected abstract CodeFile getHeaderFile();

    protected abstract Namespace getNamespace();

    protected abstract CodeFile getPopulationHeaderFile();

    protected abstract Class getPopulationSuperclass();

    protected abstract Function getIdentifierCheck(IdentifierDeclaration identifier);

    protected void addImplementationAttributes() {
    }

    protected void addClasses() {
        final String name = Mangler.mangleName(objectDeclaration);

        mainClass = new Class(name, getNamespace());
        populationClass = new Class(name + "Population", getNamespace());

        mainClass.addSuperclass(mainObjectTranslator.getMainClass(), Visibility.PUBLIC);
        getHeaderFile().addClassDeclaration(mainClass);

        constructors = mainClass.createDeclarationGroup("Constructors and Destructors");
        setters = mainClass.createDeclarationGroup("Setters for each object attribute");
        getters = mainClass.createDeclarationGroup("Getters for each object attribute");
        relationshipNavs = mainClass.createDeclarationGroup("Relationship Navigators");
        relationshipCounts = mainClass.createDeclarationGroup("Relationship Counters");
        relationshipLinkers = mainClass.createDeclarationGroup("Relationship Linkers");

        addMainClass(mainClass);

        populationCreators = populationClass.createDeclarationGroup("Instance Creation");
        populationGetters = populationClass.createDeclarationGroup("Getters for each object attribute");
        populationFinders = populationClass.createDeclarationGroup("Find routines");
        populationRegistration = populationClass.createDeclarationGroup("Singleton Registration");

        addPopulationClass(populationClass);

    }

    void addConstructor() {
        constructor = mainClass.createConstructor(constructors, Visibility.PUBLIC);
        getBodyFile().addFunctionDefinition(constructor);
    }

    void addFindFunctions() {
        for (final Population.FindFunction func : mainObjectTranslator.getPopulation().getFindFunctions()) {
            // Null predicate finds are implemented in the Population
            // superclass
            if (func.predicate != null) {
                final Function
                        findFn =
                        populationClass.redefineFunction(populationFinders, func.function, Visibility.PUBLIC);
                findFn.setComment("MASL find: " + func.predicate.toString());

                addFindFunction(func.predicate, findFn, func.type);
            }
        }

    }

    private Function getPopulation;

    protected Function getGetPopulation() {
        return getPopulation;
    }

    void addPopulation() {

        // mainClass.addNestedClass(nestedTypes, populationClass,
        // Visibility.PROTECTED);

        getPopulationHeaderFile().addClassDeclaration(populationClass);

        final Class populationSuperclass = getPopulationSuperclass();

        populationClass.addSuperclass(populationSuperclass, Visibility.PUBLIC);

        populationConstructor = populationClass.createConstructor(populationCreators, Visibility.PRIVATE);
        getPopulationBodyFile().addFunctionDefinition(populationConstructor);
        populationConstructor.setSuperclassArgs(populationSuperclass, getPopulationConstructorSuperclassParams());

        final Function
                creator =
                populationClass.redefineFunction(populationCreators,
                                                 mainObjectTranslator.getPopulation().getCreateInstance(),
                                                 Visibility.PUBLIC);
        getPopulationBodyFile().addFunctionDefinition(creator);
        final List<Expression> params = new ArrayList<Expression>();
        for (final Variable param : creator.getParameters()) {
            params.add(param.asExpression());
        }

        Expression identifierTaken = null;
        for (final IdentifierDeclaration identifier : objectDeclaration.getIdentifiers()) {
            boolean needsCheck = true;
            final List<Expression> checkParams = new ArrayList<Expression>();
            for (final AttributeDeclaration att : identifier.getAttributes()) {
                // If attribute is flagged as unique, then there is no need to check it,
                // as we can already guarantee uniqueness
                if (att.isUnique()) {
                    needsCheck = false;
                }
                checkParams.add(mainObjectTranslator.getPopulation().getCreateInstanceParam(att));
            }
            if (needsCheck) {
                final Expression thisCheck = getIdentifierCheck(identifier).asFunctionCall(checkParams);
                identifierTaken =
                        identifierTaken == null ?
                        thisCheck :
                        new BinaryExpression(identifierTaken, BinaryOperator.OR, thisCheck);
            }
        }

        if (identifierTaken != null) {
            creator.getCode().appendStatement(new IfStatement(identifierTaken,
                                                              new ThrowStatement(Architecture.programError.callConstructor(
                                                                      Literal.createStringLiteral(
                                                                              "identifier already in use")))));
        }

        final Variable
                instance =
                new Variable(creator.getReturnType(), "instance", new Expression[]{createNewInstance(params)});
        creator.getCode().appendStatement(instance.asStatement());
        creator.getCode().appendExpression(new Function("addInstance").asFunctionCall(instance.asExpression()));
        creator.getCode().appendStatement(new ReturnStatement(instance.asExpression()));

        getPopulation =
                populationClass.createStaticFunction(populationRegistration, "getPopulation", Visibility.PUBLIC);
        getPopulation.setReturnType(new TypeUsage(populationClass, TypeUsage.Reference));
        final Variable population = new Variable(new TypeUsage(populationClass), "population");
        population.setStatic(true);
        getPopulation.getCode().appendStatement(new VariableDefinitionStatement(population));
        // Variable init = new Variable(new TypeUsage(FundamentalType.BOOL),
        // "initialised",new
        // Function("initialise").asFunctionCall(population.asExpression(),false));
        // init.setStatic(true);
        // getPopulation.getCode().appendStatement(new
        // VariableDefinitionStatement(init));
        getPopulation.getCode().appendStatement(new ReturnStatement(population.asExpression()));
        getPopulationBodyFile().addFunctionDefinition(getPopulation);

        final Function registerSingleton = new Function("registerSingleton").inheritInto(populationClass);

        final Variable
                registered =
                populationClass.createStaticVariable(populationRegistration,
                                                     "registered",
                                                     new TypeUsage(FundamentalType.BOOL),
                                                     registerSingleton.asFunctionCall(getPopulation.asFunctionPointer()),
                                                     Visibility.PRIVATE);
        getPopulationBodyFile().addVariableDefinition(registered);

    }

    private void addArchitectureId() {
        final Function getter = mainClass.redefineFunction(getters, mainObjectTranslator.getGetId(), Visibility.PUBLIC);
        addArchitectureId(getter);
    }

    private void addAttributes() {
        for (final AttributeDeclaration att : objectDeclaration.getAttributes()) {
            final TypeUsage type = mainDomainTranslator.getTypes().getType(att.getType());

            if (att.isIdentifier() || !att.isReferential()) {

                final Function
                        getter =
                        mainClass.redefineFunction(getters,
                                                   mainObjectTranslator.getAttributeGetter(att),
                                                   Visibility.PUBLIC);
                final Function
                        setter =
                        att.isIdentifier() ?
                        null :
                        mainClass.redefineFunction(setters,
                                                   mainObjectTranslator.getAttributeSetter(att),
                                                   Visibility.PUBLIC);

                final Variable
                        constructorParam =
                        constructor.createParameter(type.getOptimalParameterType(), Mangler.mangleName(att));

                addAttribute(att, getter, constructorParam, setter);

                if (att.isUnique()) {
                    final Function
                            uniqueIdGetter =
                            populationClass.redefineFunction(populationCreators,
                                                             mainObjectTranslator.getPopulation().getGetUniqueId(att),
                                                             Visibility.PUBLIC);

                    final Function
                            uniqueIdUser =
                            populationClass.redefineFunction(populationCreators,
                                                             mainObjectTranslator.getPopulation().getUseUniqueId(att),
                                                             Visibility.PUBLIC);

                    addUniqueId(att, uniqueIdGetter, uniqueIdUser);
                }

            }

        }
        addImplementationAttributes();
    }

    protected void addIndexes() {
    }

    private void addCurrentState() {
        if (objectDeclaration.hasAssignerState()) {
            final StateMachineTranslator mainStateMachine = mainObjectTranslator.getAssignerFsm();
            final TypeUsage csType = new TypeUsage(mainStateMachine.getStateEnum());

            final Function
                    getter =
                    populationClass.redefineFunction(populationGetters,
                                                     mainObjectTranslator.getPopulation().getGetCurrentState(),
                                                     Visibility.PUBLIC);

            final Function
                    setter =
                    populationClass.redefineFunction(populationGetters,
                                                     mainObjectTranslator.getPopulation().getSetCurrentState(),
                                                     Visibility.PUBLIC);

            addAssignerState(csType, getter, setter);

        }

        if (objectDeclaration.hasCurrentState()) {
            final StateMachineTranslator mainStateMachine = mainObjectTranslator.getNormalFsm();
            final TypeUsage csType = new TypeUsage(mainStateMachine.getStateEnum());

            final Function
                    getter =
                    mainClass.redefineFunction(getters,
                                               mainObjectTranslator.getNormalFsm().getGetCurrentState(),
                                               Visibility.PUBLIC);

            final Function
                    setter =
                    mainClass.redefineFunction(setters,
                                               mainObjectTranslator.getNormalFsm().getSetCurrentState(),
                                               Visibility.PUBLIC);

            addCurrentState(csType, getter, setter);

        }

    }

    private void addRelationships() {
        for (final RelationshipSpecification rel : objectDeclaration.getRelationships()) {
            final RelationshipTranslator.SubclassOverrides
                    relTrans =
                    mainObjectTranslator.getRelationshipTranslator(rel).getSubclassOverrides();

            final Function
                    navigator =
                    mainClass.redefineFunction(relationshipNavs, relTrans.getNavigateFunction(), Visibility.PUBLIC);

            addRelationshipNav(rel, navigator);

            for (final org.xtuml.masl.metamodel.expression.Expression predicate : relTrans.getConditionalNavigatePredicates()) {
                final Function
                        condNavigator =
                        mainClass.redefineFunction(relationshipNavs,
                                                   relTrans.getNavigateFunction(predicate),
                                                   Visibility.PUBLIC);
                addRelationshipNav(rel, condNavigator, predicate);
            }

            final Function
                    counter =
                    mainClass.redefineFunction(relationshipCounts, relTrans.getCountFunction(), Visibility.PUBLIC);

            addRelationshipCount(rel, counter);

            final Function mainLink = relTrans.getSingleLinkFunction();
            final Function mainUnlink = relTrans.getSingleUnlinkFunction();

            if (mainLink != null && mainUnlink != null) {
                final Function linker = mainClass.redefineFunction(relationshipLinkers, mainLink, Visibility.PUBLIC);

                final Function
                        unlinker =
                        mainClass.redefineFunction(relationshipLinkers, mainUnlink, Visibility.PUBLIC);

                addRelationshipLink(rel, linker, unlinker);
            }

            final Function mainCorr = relTrans.getSingleCorrelateFunction();

            if (mainCorr != null) {
                final Function correlator = mainClass.redefineFunction(relationshipNavs, mainCorr, Visibility.PUBLIC);

                addRelationshipCorr(rel, correlator);
            }
        }

    }

    protected Function constructor = null;
    protected Class mainClass;
    protected final ObjectTranslator mainObjectTranslator;
    protected final DomainTranslator mainDomainTranslator;
    protected final ObjectDeclaration objectDeclaration;
    protected Class populationClass;
    private DeclarationGroup constructors;
    private DeclarationGroup getters;
    private Function populationConstructor;
    private DeclarationGroup populationCreators;
    private DeclarationGroup populationFinders;
    private DeclarationGroup populationGetters;
    private DeclarationGroup populationRegistration;
    private DeclarationGroup relationshipLinkers;
    private DeclarationGroup relationshipNavs;
    private DeclarationGroup relationshipCounts;
    private DeclarationGroup setters;

    public DomainTranslator getMainDomainTranslator() {
        return mainDomainTranslator;
    }

    public ObjectTranslator getMainObjectTranslator() {
        return mainObjectTranslator;
    }

}
