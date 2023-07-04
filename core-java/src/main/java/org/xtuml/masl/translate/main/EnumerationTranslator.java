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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.SwitchStatement.CaseCondition;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.type.EnumerateItem;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumerationTranslator {

    public EnumerationTranslator(final EnumerateType definition) {
        final TypeDeclaration declaration = definition.getTypeDeclaration();

        final Domain domain = declaration.getDomain();

        final DomainTranslator domainTranslator = DomainTranslator.getInstance(domain);

        headerFile = domainTranslator.getTypeHeaderFile(declaration.getVisibility());
        bodyFile = domainTranslator.getTypeBodyFile(declaration.getVisibility());

        maslEnumerates = definition.getItems();

        name = Mangler.mangleName(declaration);

        namespace = DomainNamespace.get(domain);
        clazz = new Class(name, namespace);
        headerFile.addClassDeclaration(clazz);

        type = new TypeUsage(clazz);

        enumConstants = clazz.createDeclarationGroup("Enumerates");
        enumeration = clazz.createDeclarationGroup("Enumeration");

        indexConversion = clazz.createDeclarationGroup("Index Conversions");
        valueConversion = clazz.createDeclarationGroup("Value Conversions");
        textConversion = clazz.createDeclarationGroup("Text Conversions");

        operators = clazz.createDeclarationGroup("Operators");

        final Function defaultConstructor = clazz.createConstructor(enumeration, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(defaultConstructor);

        addIndexLookup();
        addBeginEnd();
        addFirstLast();

        addTextLookup();
        addValueLookup();

        addOperators();

        addOStreamOperator(DomainNamespace.get(domain));
        addIStreamOperator(DomainNamespace.get(domain));

        addHashFunction();
        addASN1Codecs();

        defaultConstructor.setInitialValue(index, minIndex);

    }

    public Class getMainClass() {
        return clazz;
    }

    public Expression getEnumerator(final EnumerateItem item) {
        return enumerators.get(item);
    }

    public Expression getEnumeratorIndex(final EnumerateItem item) {
        return enumeratorIndexes.get(item);
    }

    public Expression getEnumeratorValue(final EnumerateItem item) {
        return enumeratorValues.get(item);
    }

    public Function getGetIndex() {
        return getIndex;
    }

    public Function getSetIndex() {
        return setIndex;
    }

    Statement getRangeError() {
        return new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                "Enumerate out of RangeExpression")));
    }

    TypeUsage getType() {
        return type;
    }

    private void addASN1Codecs() {
        addASN1Encode();
        addASN1Decode();
    }

    private void addASN1Encode() {
        final Function encode = ASN1.encodeValue();
        encode.setInlineModifier(true);
        final Expression
                value =
                encode.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "value").asExpression();

        encode.getCode().appendStatement(new ReturnStatement(ASN1.DEREncode.asFunctionCall(getGetIndex().asFunctionCall(
                value,
                false))));

        headerFile.addFunctionDefinition(encode);
    }

    private void addASN1Decode() {
        final ASN1.DecodeValue decodeValue = new ASN1.DecodeValue();

        final Function decode = decodeValue.function;

        final Expression
                value =
                decode.createParameter(new TypeUsage(clazz, TypeUsage.Reference), "value").asExpression();

        decode.getCode().appendStatement(ASN1.BERDecode.asFunctionCall(decodeValue.decoder.asExpression(),
                                                                       getSetIndex().asFunctionCall(value,
                                                                                                    false)).asStatement());

        headerFile.addFunctionDefinition(decode);
    }

    private void addHashFunction() {
        final Function function = new Function("hash_value", namespace);
        function.setReturnType(new TypeUsage(Std.size_t));

        final Expression
                value =
                function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "value").asExpression();

        function.getCode().appendStatement(new ReturnStatement(getGetIndex().asFunctionCall(value, false)));
        function.declareInClass(true);
        headerFile.addFunctionDeclaration(function);
    }

    private void addDecrement() {
        final Function function = clazz.createMemberFunction(operators, "operator--", Visibility.PUBLIC);

        function.setReturnType(new TypeUsage(clazz, TypeUsage.Reference));
        final Statement
                boundCheck =
                new IfStatement(new BinaryExpression(index.asExpression(), BinaryOperator.EQUAL, minIndex),
                                new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                                        "Enumerate out of RangeExpression"))));

        function.getCode().appendStatement(boundCheck);
        function.getCode().appendExpression(new BinaryExpression(index.asExpression(),
                                                                 BinaryOperator.ASSIGN,
                                                                 indexEnum.callConstructor(new BinaryExpression(index.asExpression(),
                                                                                                                BinaryOperator.MINUS,
                                                                                                                Literal.ONE))));
        function.getCode().appendStatement(new ReturnStatement(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                   clazz.getThis().asExpression())));

        bodyFile.addFunctionDefinition(function);
    }

    private void addDifference() {
        final Function function = clazz.createMemberFunction(operators, "operator-", Visibility.PUBLIC);

        final Variable rhs = function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "rhs");
        function.setConst(true);
        function.setReturnType(new TypeUsage(Std.int64));
        function.declareInClass(true);
        function.getCode().appendStatement(new ReturnStatement(new BinaryExpression(index.asExpression(),
                                                                                    BinaryOperator.MINUS,
                                                                                    index.asMemberReference(rhs.asExpression(),
                                                                                                            false))));

    }

    private void addEqualTo() {
        final Function function = clazz.createMemberFunction(operators, "operator==", Visibility.PUBLIC);

        final Variable rhs = function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "rhs");
        function.setConst(true);
        function.setReturnType(new TypeUsage(FundamentalType.BOOL));
        function.declareInClass(true);
        function.getCode().appendStatement(new ReturnStatement(new BinaryExpression(index.asExpression(),
                                                                                    BinaryOperator.EQUAL,
                                                                                    index.asMemberReference(rhs.asExpression(),
                                                                                                            false))));

    }

    private void addIncrement() {
        final Function function = clazz.createMemberFunction(operators, "operator++", Visibility.PUBLIC);

        function.setReturnType(new TypeUsage(clazz, TypeUsage.Reference));
        final Statement
                boundCheck =
                new IfStatement(new BinaryExpression(index.asExpression(), BinaryOperator.EQUAL, maxIndex),
                                getRangeError());

        function.getCode().appendStatement(boundCheck);
        function.getCode().appendExpression(new BinaryExpression(index.asExpression(),
                                                                 BinaryOperator.ASSIGN,
                                                                 indexEnum.callConstructor(new BinaryExpression(index.asExpression(),
                                                                                                                BinaryOperator.PLUS,
                                                                                                                Literal.ONE))));
        function.getCode().appendStatement(new ReturnStatement(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                   clazz.getThis().asExpression())));

        bodyFile.addFunctionDefinition(function);
    }

    private void addIndexLookup() {
        indexEnum = new EnumerationType("Index");
        clazz.addEnumeration(indexConversion, indexEnum, Visibility.PUBLIC);
        index = clazz.createMemberVariable(indexConversion, "index", new TypeUsage(indexEnum), Visibility.PRIVATE);

        final Function constructor = clazz.createConstructor(indexConversion, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(constructor);

        getIndex = clazz.createMemberFunction(indexConversion, "getIndex", Visibility.PUBLIC);

        getIndex.setReturnType(new TypeUsage(indexEnum));
        getIndex.declareInClass(true);
        getIndex.setConst(true);
        getIndex.getCode().appendStatement(new ReturnStatement(index.asExpression()));

        setIndex = clazz.createMemberFunction(indexConversion, "setIndex", Visibility.PUBLIC);

        setIndex.setReturnType(new TypeUsage(indexEnum, TypeUsage.Reference));
        setIndex.declareInClass(true);
        setIndex.getCode().appendStatement(new ReturnStatement(index.asExpression()));

        indexEnumerators = new ArrayList<EnumerationType.Enumerator>();

        for (final EnumerateItem maslEnumItem : maslEnumerates) {
            final EnumerationType.Enumerator
                    indexEnumItem =
                    indexEnum.addEnumerator("index_" + Mangler.mangleName(maslEnumItem), null);
            indexEnumerators.add(indexEnumItem);

            final Variable
                    enumeratorVar =
                    clazz.createStaticVariable(enumConstants,
                                               Mangler.mangleName(maslEnumItem),
                                               new TypeUsage(clazz, TypeUsage.Const),
                                               clazz.callConstructor(indexEnumItem.asExpression()),
                                               Visibility.PUBLIC);

            bodyFile.addVariableDefinition(enumeratorVar);
            enumerators.put(maslEnumItem, enumeratorVar.asExpression());
            enumeratorIndexes.put(maslEnumItem, indexEnumItem.asExpression());
        }

        maxIndex = indexEnumerators.get(indexEnumerators.size() - 1).asExpression();
        minIndex = indexEnumerators.get(0).asExpression();

        final Variable indexParam = constructor.createParameter(new TypeUsage(indexEnum), "index");
        constructor.setInitialValue(index, indexParam.asExpression());
        constructor.setExplicit(true);

    }

    private void addBeginEnd() {
        // Define a cpp typedef based on the templated SWA::RangeIterator class
        // to represent a const_iterator for the current Enumeration class.
        final TypedefType
                constIteratorType =
                new TypedefType("const_iterator", new TypeUsage(Architecture.rangeIterator(new TypeUsage(clazz))));
        clazz.addTypedef(indexConversion, constIteratorType, Visibility.PUBLIC);

        final Expression beginEnum = enumerators.get(maslEnumerates.get(0));
        final Expression endEnum = enumerators.get(maslEnumerates.get(maslEnumerates.size() - 1));

        final Function beginEnumFn = clazz.createMemberFunction(indexConversion, "beginEnum", Visibility.PUBLIC);
        beginEnumFn.setReturnType(new TypeUsage(constIteratorType));
        beginEnumFn.declareInClass(true);
        beginEnumFn.setStatic(true);
        beginEnumFn.getCode().appendStatement(new ReturnStatement(constIteratorType.callConstructor(beginEnum,
                                                                                                    endEnum)));

        final Function endEnumFn = clazz.createMemberFunction(indexConversion, "endEnum", Visibility.PUBLIC);
        endEnumFn.setReturnType(new TypeUsage(constIteratorType));
        endEnumFn.declareInClass(true);
        endEnumFn.setStatic(true);
        endEnumFn.getCode().appendStatement(new ReturnStatement(constIteratorType.callConstructor(endEnum)));

    }

    private void addFirstLast() {
        final Expression beginEnum = enumerators.get(maslEnumerates.get(0));
        final Expression endEnum = enumerators.get(maslEnumerates.get(maslEnumerates.size() - 1));

        final Function beginEnumFn = clazz.createMemberFunction(enumeration, "getFirst", Visibility.PUBLIC);
        beginEnumFn.setReturnType(new TypeUsage(clazz));
        beginEnumFn.declareInClass(true);
        beginEnumFn.setStatic(true);
        beginEnumFn.getCode().appendStatement(new ReturnStatement(beginEnum));

        final Function endEnumFn = clazz.createMemberFunction(enumeration, "getLast", Visibility.PUBLIC);
        endEnumFn.setReturnType(new TypeUsage(clazz));
        endEnumFn.declareInClass(true);
        endEnumFn.setStatic(true);
        endEnumFn.getCode().appendStatement(new ReturnStatement(endEnum));
    }

    private void addLessThan() {
        final Function function = clazz.createMemberFunction(operators, "operator<", Visibility.PUBLIC);

        final Variable rhs = function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "rhs");
        function.setConst(true);
        function.setReturnType(new TypeUsage(FundamentalType.BOOL));
        function.declareInClass(true);
        function.getCode().appendStatement(new ReturnStatement(new BinaryExpression(index.asExpression(),
                                                                                    BinaryOperator.LESS_THAN,
                                                                                    index.asMemberReference(rhs.asExpression(),
                                                                                                            false))));

    }

    private void addOperators() {
        // USe Boost utils to add all extra comparision and ++/-- operators
        clazz.addSuperclass(Boost.lessThanComparable(new TypeUsage(clazz),
                                                     Boost.equalityComparable(new TypeUsage(clazz),
                                                                              Boost.incrementable(new TypeUsage(clazz),
                                                                                                  Boost.decrementable(
                                                                                                          new TypeUsage(
                                                                                                                  clazz))))),
                            Visibility.PRIVATE);

        addLessThan();
        addEqualTo();
        addIncrement();
        addDecrement();
        addDifference();
    }

    private Function addOStreamOperator(final Namespace namespace) {
        final Function streamOperator = new Function("operator<<", namespace);
        streamOperator.setReturnType(new TypeUsage(Std.ostream, TypeUsage.Reference));
        final Expression
                stream =
                streamOperator.createParameter(new TypeUsage(Std.ostream, TypeUsage.Reference),
                                               "stream").asExpression();
        final Expression
                obj =
                streamOperator.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "obj").asExpression();

        final Expression
                result =
                new BinaryExpression(stream, BinaryOperator.LEFT_SHIFT, getText.asFunctionCall(obj, false));
        streamOperator.getCode().appendStatement(new ReturnStatement(result));

        headerFile.addFunctionDeclaration(streamOperator);
        bodyFile.addFunctionDefinition(streamOperator);

        return streamOperator;
    }

    private Function addIStreamOperator(final Namespace namespace) {
        final Function streamOperator = new Function("operator>>", namespace);
        streamOperator.setReturnType(new TypeUsage(Std.istream, TypeUsage.Reference));
        final Expression
                stream =
                streamOperator.createParameter(new TypeUsage(Std.istream, TypeUsage.Reference),
                                               "stream").asExpression();
        final Expression
                obj =
                streamOperator.createParameter(new TypeUsage(clazz, TypeUsage.Reference), "obj").asExpression();

        final Variable text = new Variable(new TypeUsage(Std.string), "text");
        final Expression read = new BinaryExpression(stream, BinaryOperator.RIGHT_SHIFT, text.asExpression());
        final Expression
                assignment =
                new BinaryExpression(obj, BinaryOperator.ASSIGN, clazz.callConstructor(text.asExpression()));

        streamOperator.getCode().appendStatement(text.asStatement());
        streamOperator.getCode().appendStatement(new ExpressionStatement(read));
        streamOperator.getCode().appendStatement(new ExpressionStatement(assignment));
        streamOperator.getCode().appendStatement(new ReturnStatement(stream));

        headerFile.addFunctionDeclaration(streamOperator);
        bodyFile.addFunctionDefinition(streamOperator);

        return streamOperator;
    }

    private Function getText;

    private void addTextLookup() {
        final Function constructor = clazz.createConstructor(textConversion, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(constructor);

        getText = clazz.createMemberFunction(textConversion, "getText", Visibility.PUBLIC);

        getText.setReturnType(new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst));
        getText.declareInClass(true);
        getText.setConst(true);

        final List<Literal> literals = new ArrayList<Literal>();

        for (final EnumerateItem maslEnumerate : maslEnumerates) {
            literals.add(Literal.createStringLiteral(maslEnumerate.getName()));
        }

        textLookupVar =
                clazz.createStaticVariable(textConversion,
                                           "textLookup",
                                           new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst),
                                           new AggregateInitialiser(literals),
                                           Visibility.PRIVATE);
        textLookupVar.setArray();
        bodyFile.addVariableDefinition(textLookupVar);
        getText.getCode().appendStatement(new ReturnStatement(new ArrayAccess(textLookupVar.asExpression(),
                                                                              index.asExpression())));

        final Function fromText = createFromText();
        final Variable text = constructor.createParameter(new TypeUsage(Std.string, TypeUsage.ConstReference), "text");
        constructor.setInitialValue(index, fromText.asFunctionCall(text.asExpression()));
        constructor.setExplicit(true);

    }

    private void addValueLookup() {
        valueEnum = new EnumerationType("Value");
        clazz.addEnumeration(valueConversion, valueEnum, Visibility.PUBLIC);

        final Function constructor = clazz.createConstructor(valueConversion, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(constructor);

        final Function getValue = clazz.createMemberFunction(valueConversion, "getValue", Visibility.PUBLIC);

        getValue.setReturnType(new TypeUsage(valueEnum));
        getValue.declareInClass(true);
        getValue.setConst(true);

        valueEnumerators = new ArrayList<EnumerationType.Enumerator>();
        final List<Expression> values = new ArrayList<Expression>();

        for (final EnumerateItem maslEnum : maslEnumerates) {
            Expression specifiedValue = null;
            if (maslEnum.getValue() != null) {
                specifiedValue = ExpressionTranslator.createTranslator(maslEnum.getValue(), null).getReadExpression();
            }
            final EnumerationType.Enumerator
                    valueEnumItem =
                    valueEnum.addEnumerator("value_" + Mangler.mangleName(maslEnum), specifiedValue);
            valueEnumerators.add(valueEnumItem);
            values.add(valueEnumItem.asExpression());
            enumeratorValues.put(maslEnum, valueEnumItem.asExpression());
        }

        valueLookupVar =
                clazz.createStaticVariable(valueConversion,
                                           "valueLookup",
                                           new TypeUsage(valueEnum),
                                           new AggregateInitialiser(values),
                                           Visibility.PRIVATE);
        valueLookupVar.setArray();
        bodyFile.addVariableDefinition(valueLookupVar);
        getValue.getCode().appendStatement(new ReturnStatement(new ArrayAccess(valueLookupVar.asExpression(),
                                                                               index.asExpression())));

        final Function fromValue = createFromValue();

        final Variable value = constructor.createParameter(new TypeUsage(valueEnum), "value");
        constructor.setInitialValue(index, fromValue.asFunctionCall(value.asExpression()));
        constructor.setExplicit(true);

        final Function intConstructor = clazz.createConstructor(valueConversion, Visibility.PUBLIC);

        final Variable intValue = intConstructor.createParameter(new TypeUsage(Std.int32), "value");
        intConstructor.setInitialValue(index,
                                       fromValue.asFunctionCall(valueEnum.callConstructor(intValue.asExpression())));
        intConstructor.setExplicit(true);
        bodyFile.addFunctionDefinition(intConstructor);

        final Function cast = clazz.createCastFunction(valueConversion, new TypeUsage(Std.int32), Visibility.PUBLIC);
        cast.declareInClass(true);
        cast.setConst(true);
        cast.getCode().appendStatement(new ReturnStatement(Std.static_cast(new TypeUsage(Std.int32)).asFunctionCall(
                getValue.asFunctionCall())));

    }

    private Function createFromText() {
        final TypedefType
                lookupTableType =
                new TypedefType("TextLookupTable",
                                new TypeUsage(Std.map(new TypeUsage(Std.string), new TypeUsage(indexEnum))));
        clazz.addTypedef(textConversion, lookupTableType, Visibility.PRIVATE);

        final Function
                getLookupTable =
                clazz.createStaticFunction(textConversion, "getLookupTable", Visibility.PRIVATE);
        getLookupTable.setReturnType(new TypeUsage(lookupTableType));

        final Variable lookup = new Variable(new TypeUsage(lookupTableType), "lookup");
        getLookupTable.getCode().appendStatement(new VariableDefinitionStatement(lookup));
        for (int i = 0; i < maslEnumerates.size(); ++i) {
            final Expression
                    index =
                    new ArrayAccess(textLookupVar.asExpression(), indexEnumerators.get(i).asExpression());
            final Expression
                    mapEntry =
                    lookupTableType.referenceNestedType("value_type").callConstructor(index,
                                                                                      indexEnumerators.get(i).asExpression());

            getLookupTable.getCode().appendExpression(new Function("insert").asFunctionCall(lookup.asExpression(),
                                                                                            false,
                                                                                            mapEntry));
        }
        getLookupTable.getCode().appendStatement(new ReturnStatement(lookup.asExpression()));

        bodyFile.addFunctionDefinition(getLookupTable);

        final Function fromText = clazz.createStaticFunction(textConversion, "fromText", Visibility.PRIVATE);
        final Variable text = fromText.createParameter(new TypeUsage(Std.string, TypeUsage.ConstReference), "text");

        final Variable
                lookupStatic =
                new Variable(new TypeUsage(lookupTableType), "lookup", getLookupTable.asFunctionCall());
        lookupStatic.setStatic(true);

        fromText.getCode().appendStatement(new VariableDefinitionStatement(lookupStatic));

        final Variable
                pos =
                new Variable(new TypeUsage(lookupTableType.referenceNestedType("const_iterator")),
                             "pos",
                             new Function("find").asFunctionCall(lookupStatic.asExpression(),
                                                                 false,
                                                                 text.asExpression()));
        fromText.getCode().appendStatement(new VariableDefinitionStatement(pos));

        fromText.getCode().appendStatement(new IfStatement(new BinaryExpression(pos.asExpression(),
                                                                                BinaryOperator.EQUAL,
                                                                                new Function("end").asFunctionCall(
                                                                                        lookupStatic.asExpression(),
                                                                                        false)), getRangeError()));

        fromText.getCode().appendStatement(new ReturnStatement(new Variable("second").asMemberReference(pos.asExpression(),
                                                                                                        true)));
        fromText.setReturnType(new TypeUsage(indexEnum));

        bodyFile.addFunctionDefinition(fromText);

        return fromText;
    }

    private Function createFromValue() {

        final Function fromValue = clazz.createStaticFunction(valueConversion, "fromValue", Visibility.PRIVATE);
        final Variable value = fromValue.createParameter(new TypeUsage(valueEnum), "value");

        final List<CaseCondition> cases = new ArrayList<CaseCondition>();

        for (int i = 0; i < maslEnumerates.size(); ++i) {
            final Expression discriminator = valueEnumerators.get(i).asExpression();
            final Statement result = new ReturnStatement(indexEnumerators.get(i).asExpression());

            cases.add(new SwitchStatement.CaseCondition(discriminator, result));
        }

        fromValue.getCode().appendStatement(new SwitchStatement(value.asExpression(), cases, getRangeError()));
        fromValue.setReturnType(new TypeUsage(indexEnum));
        bodyFile.addFunctionDefinition(fromValue);

        return fromValue;
    }

    public Type getIndexEnum() {
        return indexEnum;
    }

    CodeFile bodyFile;

    CodeFile headerFile;

    final private Class clazz;
    final private DeclarationGroup enumConstants;

    final private DeclarationGroup enumeration;
    final private Map<EnumerateItem, Expression> enumeratorValues = new HashMap<EnumerateItem, Expression>();
    final private Map<EnumerateItem, Expression> enumeratorIndexes = new HashMap<EnumerateItem, Expression>();
    final private Map<EnumerateItem, Expression> enumerators = new HashMap<EnumerateItem, Expression>();

    private Function getIndex;
    private Function setIndex;
    private Variable index;
    final private DeclarationGroup indexConversion;

    private EnumerationType indexEnum;

    private List<EnumerationType.Enumerator> indexEnumerators;
    private final List<? extends EnumerateItem> maslEnumerates;

    private Expression maxIndex;
    private Expression minIndex;

    final private String name;
    final private DeclarationGroup operators;
    final private DeclarationGroup textConversion;
    private Variable textLookupVar;
    final private TypeUsage type;
    final private DeclarationGroup valueConversion;
    private EnumerationType valueEnum;

    private List<EnumerationType.Enumerator> valueEnumerators;
    private Variable valueLookupVar;
    private final Namespace namespace;
}
