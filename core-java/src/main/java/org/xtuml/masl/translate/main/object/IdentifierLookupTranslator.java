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
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.main.*;

import java.util.ArrayList;
import java.util.List;

public abstract class IdentifierLookupTranslator {

    public IdentifierLookupTranslator(final ConcreteObjectTranslator concreteObj,
                                      final ObjectDeclaration object,
                                      final IdentifierDeclaration identifier) {
        mainObjectTranslator = concreteObj.getMainObjectTranslator();
        this.concreteObj = concreteObj;
        this.identifier = identifier;

        final List<TypeUsage> keyTypes = new ArrayList<TypeUsage>();
        String name = "";
        for (final AttributeDeclaration attDec : identifier.getAttributes()) {
            keyTypes.add(Types.getInstance().getType(attDec.getType()));

            name = name + Mangler.mangleName(attDec);
        }
        lookupName = name;
        keyType = new BigTuple(keyTypes).getTupleType();
    }

    abstract public TypeUsage getStoredType();

    public Expression getStoredToObjPointer(final Expression result) {
        return result;
    }

    public Function addLookup() {

        lookupType = Boost.unordered_map(getKeyType(), getStoredType());
        lookupAtt =
                concreteObj.getPopulationClass().createMemberVariable(getDeclarationGroup(),
                                                                      lookupName + "_Lookup",
                                                                      new TypeUsage(lookupType),
                                                                      Visibility.PRIVATE);
        final List<Expression> createGetters = new ArrayList<Expression>();
        final List<Expression> deleteGetters = new ArrayList<Expression>();
        for (final AttributeDeclaration attDec : identifier.getAttributes()) {
            final Expression
                    createGetter =
                    mainObjectTranslator.getAttributeGetter(attDec).asFunctionCall(getInstanceCreatedInstance(), true);
            createGetters.add(createGetter);
            final Expression
                    deleteGetter =
                    mainObjectTranslator.getAttributeGetter(attDec).asFunctionCall(getInstanceDeletedInstance(), true);
            deleteGetters.add(deleteGetter);
        }

        final Expression createKey = BigTuple.getMakeTuple(createGetters);
        final Expression deleteKey = BigTuple.getMakeTuple(deleteGetters);

        getInstanceCreatedCode().appendExpression(new Function("insert").asFunctionCall(lookupAtt.asExpression(),
                                                                                        false,
                                                                                        lookupType.referenceNestedType(
                                                                                                "value_type").callConstructor(
                                                                                                createKey,
                                                                                                getInstanceCreatedInstance())));
        getInstanceDeletedCode().appendExpression(new Function("erase").asFunctionCall(lookupAtt.asExpression(),
                                                                                       false,
                                                                                       deleteKey));

        return findFunction;
    }

    public Function getFinder() {
        if (findFunction == null) {
            if (lookupAtt == null) {
                addLookup();
            }

            final Type objPtr = Architecture.objectPtr(new TypeUsage(mainObjectTranslator.getMainClass()));

            findFunction =
                    concreteObj.getPopulationClass().createMemberFunction(getDeclarationGroup(),
                                                                          "find_" + lookupName,
                                                                          Visibility.PROTECTED);
            findFunction.setConst(true);
            findFunction.setReturnType(new TypeUsage(objPtr));
            concreteObj.getPopulationBodyFile().addFunctionDefinition(findFunction);
            final List<Expression> params = new ArrayList<Expression>();
            for (final AttributeDeclaration attDec : identifier.getAttributes()) {
                final Variable
                        param =
                        findFunction.createParameter(Types.getInstance().getType(attDec.getType()).getOptimalParameterType(),
                                                     Mangler.mangleName(attDec));
                params.add(param.asExpression());
            }
            final Expression
                    find =
                    new Function("find").asFunctionCall(lookupAtt.asExpression(), false, BigTuple.getMakeTuple(params));
            final Variable
                    found =
                    new Variable(new TypeUsage(lookupType.referenceNestedType("const_iterator")), "result", find);
            final Statement failReturn = new ReturnStatement(objPtr.callConstructor());
            final Statement
                    foundReturn =
                    new ReturnStatement(getStoredToObjPointer(new Variable("second").asMemberReference(found.asExpression(),
                                                                                                       true)));
            final Statement
                    check =
                    new IfStatement(new BinaryExpression(found.asExpression(),
                                                         BinaryOperator.EQUAL,
                                                         new Function("end").asFunctionCall(lookupAtt.asExpression(),
                                                                                            false)),
                                    failReturn,
                                    foundReturn);

            findFunction.getCode().appendStatement(found.asStatement());
            findFunction.getCode().appendStatement(check);

        }
        return findFunction;
    }

    public Function getChecker() {
        if (checkFunction == null) {
            if (lookupAtt == null) {
                addLookup();
            }

            checkFunction =
                    concreteObj.getPopulationClass().createMemberFunction(getDeclarationGroup(),
                                                                          "exists_" + lookupName,
                                                                          Visibility.PROTECTED);
            checkFunction.setConst(true);
            checkFunction.setReturnType(new TypeUsage(FundamentalType.BOOL));
            concreteObj.getPopulationBodyFile().addFunctionDefinition(checkFunction);
            final List<Expression> params = new ArrayList<Expression>();
            for (final AttributeDeclaration attDec : identifier.getAttributes()) {
                final Variable
                        param =
                        checkFunction.createParameter(Types.getInstance().getType(attDec.getType()).getOptimalParameterType(),
                                                      Mangler.mangleName(attDec));
                params.add(param.asExpression());
            }
            final Expression
                    find =
                    new Function("find").asFunctionCall(lookupAtt.asExpression(), false, BigTuple.getMakeTuple(params));
            final Statement
                    result =
                    new ReturnStatement(new BinaryExpression(find,
                                                             BinaryOperator.NOT_EQUAL,
                                                             new Function("end").asFunctionCall(lookupAtt.asExpression(),
                                                                                                false)));

            checkFunction.getCode().appendStatement(result);

        }
        return checkFunction;
    }

    public abstract DeclarationGroup getDeclarationGroup();

    public abstract CodeBlock getInstanceCreatedCode();

    public abstract CodeBlock getInstanceDeletedCode();

    public abstract Expression getInstanceCreatedInstance();

    public abstract Expression getInstanceDeletedInstance();

    private TypeUsage getKeyType() {
        return keyType;
    }

    private final ConcreteObjectTranslator concreteObj;
    private final ObjectTranslator mainObjectTranslator;
    private final IdentifierDeclaration identifier;
    private final TypeUsage keyType;
    private final String lookupName;
    private Function findFunction = null;
    private Function checkFunction = null;
    private Variable lookupAtt;
    private Class lookupType;

}
