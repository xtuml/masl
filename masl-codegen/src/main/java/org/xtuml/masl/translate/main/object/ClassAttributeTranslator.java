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
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;

import java.util.HashMap;
import java.util.Map;

public abstract class ClassAttributeTranslator {

    private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;

    private static final Map<ObjectDeclaration, ClassAttributeTranslator> translators = new HashMap<>();
    public static final String CURRENT_STATE = "currentState";
    public static final String ASSIGNER_STATE = "assignerState";

    static ClassAttributeTranslator getTranslator(final ObjectDeclaration object) {
        return translators.get(object);
    }

    public ClassAttributeTranslator(final ConcreteObjectTranslator concreteObj, final ObjectDeclaration object) {
        mainDomainTranslator = DomainTranslator.getInstance(object.getDomain());
        mainObjectTranslator = mainDomainTranslator.getObjectTranslator(object);
        this.concreteObj = concreteObj;
        translators.put(object, this);
    }

    public void addMainClassDeclarationGroups() {
        attributes = getMainClass().createDeclarationGroup("Storage for each object attribute");
    }

    public void addFactoryClassDeclarationGroups() {
        factoryAttributes = getFactoryClass().createDeclarationGroup("Storage for each object attribute");
    }

    public Variable addArchitectureId(final Function getter, final Expression initialValue) {
        final Variable
                archId =
                getMainClass().createMemberVariable(attributes,
                                                    "architectureId",
                                                    mainObjectTranslator.getIdType(),
                                                    Visibility.PRIVATE);

        getter.getCode().appendStatement(new ReturnStatement(archId.asExpression()));
        getter.declareInClass(true);
        concreteObj.getConstructor().setInitialValue(archId, initialValue);

        return archId;

    }

    protected abstract TypeUsage getType(BasicType type);

    public Variable addAttribute(final AttributeDeclaration declaration,
                                 final Function getter,
                                 final Variable constructorParameter,
                                 final Function setter) {
        final TypeUsage type = getType(declaration.getType());
        final Variable
                attribute =
                getMainClass().createMemberVariable(attributes,
                                                    Mangler.mangleName(declaration),
                                                    type,
                                                    Visibility.PRIVATE);
        getter.getCode().appendStatement(new ReturnStatement(attribute.asExpression()));
        getter.declareInClass(true);

        concreteObj.getConstructor().setInitialValue(attribute, constructorParameter.asExpression());

        if (setter != null) {
            setter.getCode().appendExpression(new BinaryExpression(new BinaryExpression(getMainClass().getThis().asExpression(),
                                                                                        BinaryOperator.PTR_REF,
                                                                                        attribute.asExpression()),
                                                                   BinaryOperator.ASSIGN,
                                                                   (setter.getParameters().get(0)).asExpression()));
            setter.declareInClass(true);

        }

        return attribute;
    }

    public Variable addCurrentState(final TypeUsage stateType, final Function getter, final Function setter) {
        final Variable
                currentState =
                getMainClass().createMemberVariable(attributes, CURRENT_STATE, stateType, Visibility.PRIVATE);

        final Variable
                currentStateParam =
                concreteObj.getConstructor().createParameter(stateType.getOptimalParameterType(), CURRENT_STATE);
        concreteObj.getConstructor().setInitialValue(currentState, currentStateParam.asExpression());
        getter.getCode().appendStatement(new ReturnStatement(currentState.asExpression()));
        getter.declareInClass(true);

        setter.getCode().appendStatement(new ExpressionStatement(new BinaryExpression(currentState.asExpression(),
                                                                                      BinaryOperator.ASSIGN,
                                                                                      (setter.getParameters().get(0)).asExpression())));
        setter.declareInClass(true);

        return currentState;
    }

    public Variable addAssignerState(final TypeUsage stateType, final Function getter, final Function setter) {
        final Variable
                currentState =
                getFactoryClass().createMemberVariable(factoryAttributes,
                                                       ASSIGNER_STATE,
                                                       stateType,
                                                       Visibility.PRIVATE);

        getter.getCode().appendStatement(new ReturnStatement(currentState.asExpression()));
        getter.declareInClass(true);

        setter.getCode().appendStatement(new ExpressionStatement(new BinaryExpression(currentState.asExpression(),
                                                                                      BinaryOperator.ASSIGN,
                                                                                      (setter.getParameters().get(0)).asExpression())));
        setter.declareInClass(true);

        return currentState;

    }

    public void addUniqueId(final AttributeDeclaration declaration, final Function getter, final Function user) {
        final TypeUsage type = mainDomainTranslator.getTypes().getType(declaration.getType());
        final Variable
                nextUniqueId =
                getFactoryClass().createMemberVariable(factoryAttributes,
                                                       "nextUniqueId_" + Mangler.mangleName(declaration),
                                                       type,
                                                       Visibility.PRIVATE);

        getter.getCode().appendStatement(new ReturnStatement(nextUniqueId.asExpression()));
        getter.declareInClass(true);

        final Expression usedId = user.getParameters().get(0).asExpression();

        final Expression
                newId =
                new BinaryExpression(Std.max(usedId, nextUniqueId.asExpression()), BinaryOperator.PLUS, Literal.ONE);

        user.getCode().appendStatement(new BinaryExpression(nextUniqueId.asExpression(),
                                                            BinaryOperator.ASSIGN,
                                                            newId).asStatement());
        user.declareInClass(true);
    }

    public CodeFile getBodyFile() {
        return concreteObj.getBodyFile();
    }

    public Class getMainClass() {
        return concreteObj.getMainClass();
    }

    public Class getFactoryClass() {
        return concreteObj.getPopulationClass();
    }

    private DeclarationGroup attributes;
    private DeclarationGroup factoryAttributes;

    private final ConcreteObjectTranslator concreteObj;

}
