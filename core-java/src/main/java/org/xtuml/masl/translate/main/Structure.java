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
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.expression.IntegerLiteral;
import org.xtuml.masl.metamodel.expression.LiteralExpression;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.*;

public class Structure {

    private final DomainTranslator domainTranslator;
    private final DeclarationGroup typedefs;

    public Structure(final TypeDeclaration declaration) {
        structure = (StructureType) declaration.getTypeDefinition();
        final Domain domain = declaration.getDomain();
        domainTranslator = DomainTranslator.getInstance(domain);
        headerFile = domainTranslator.getTypeHeaderFile(declaration.getVisibility());
        bodyFile = domainTranslator.getTypeBodyFile(declaration.getVisibility());
        name = Mangler.mangleName(declaration);
        namespace = DomainNamespace.get(domain);
        clazz = new Class(name, namespace);
        headerFile.addClassDeclaration(clazz);
        type = new TypeUsage(clazz);

        typedefs = clazz.createDeclarationGroup("Types");
        getters = clazz.createDeclarationGroup("Attribute Getters");
        setters = clazz.createDeclarationGroup("Attribute Setter");
        constructors = clazz.createDeclarationGroup("Constructors");
        members = clazz.createDeclarationGroup("Private Members");

        streamOperator = new Function("operator<<", namespace);

    }

    public void translate() {
        addAttributes();
        addDefaultConstructor();
        addElementConstructor();
        addTupleTypedef();
        addTupleConstructor();
        addStructureConstructor();
        addTupleAssignment();
        // addGenericTupleConstructor();
        // addGenericTupleAssignment();
        addToTuple();

        addLessThan();
        addEquality();
        addHashFunction();
        addASN1Codecs();
        addJsonCodecs();

        clazz.addSuperclass(Boost.lessThanComparable(new TypeUsage(clazz),
                                                     Boost.equalityComparable(new TypeUsage(clazz))),
                            Visibility.PRIVATE);

        translateStreamOperator();

    }

    public CodeFile getBodyFile() {
        return bodyFile;
    }

    public Class getMainClass() {
        return clazz;
    }

    public Function getGetter(final StructureElement attribute) {
        return getterFunctions.get(attribute);
    }

    public Function getSetter(final StructureElement attribute) {
        return setterFunctions.get(attribute);
    }

    public Variable getMember(final StructureElement attribute) {
        return memberVariables.get(attribute);
    }

    private void addHashFunction() {
        final Function function = new Function("hash_value", namespace);
        function.setReturnType(new TypeUsage(Std.size_t));

        final Expression
                value =
                function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "value").asExpression();

        final Variable seed = new Variable(new TypeUsage(Std.size_t), "seed", Literal.ZERO);

        function.getCode().appendStatement(seed.asStatement());
        for (final StructureElement att : structure.getElements()) {
            function.getCode().appendStatement(Boost.hash_combine.asFunctionCall(seed.asExpression(),
                                                                                 getGetter(att).asFunctionCall(value,
                                                                                                               false)).asStatement());
        }

        function.getCode().appendStatement(new ReturnStatement(seed.asExpression()));
        headerFile.addFunctionDeclaration(function);
        bodyFile.addFunctionDefinition(function);
    }

    private void addASN1Codecs() {
        addASN1Encode();
        addASN1Decode();
    }

    private void addASN1Encode() {
        final Function encode = ASN1.encodeValue();

        final Expression
                value =
                encode.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "value").asExpression();

        final Variable
                encoder =
                new Variable(new TypeUsage(ASN1.DEREncoder), "encoder", Collections.singletonList(ASN1.sequenceTag));
        encode.getCode().appendStatement(encoder.asStatement());

        encode.getCode().appendStatement(new Function("reserve").asFunctionCall(encoder.asExpression(),
                                                                                false,
                                                                                new Literal(structure.getElements().size())).asStatement());

        for (final StructureElement att : structure.getElements()) {
            encode.getCode().appendStatement(ASN1.addChild(encoder.asExpression(),
                                                           getGetter(att).asFunctionCall(value, false)));
        }

        encode.getCode().appendStatement(new ReturnStatement(encoder.asExpression()));

        headerFile.addFunctionDeclaration(encode);
        bodyFile.addFunctionDefinition(encode);
    }

    private void addASN1Decode() {

        final ASN1.DecodeValue decodeValue = new ASN1.DecodeValue();

        final Function decode = decodeValue.function;

        final Expression
                value =
                decode.createParameter(new TypeUsage(clazz, TypeUsage.Reference), "value").asExpression();

        decode.getCode().appendStatement(ASN1.checkHeader(decodeValue.decoder.asExpression(), ASN1.sequenceTag, true));

        decode.getCode().appendStatement(decodeValue.childIt.asStatement());
        for (final StructureElement att : structure.getElements()) {
            decode.getCode().appendStatement(ASN1.checkChildPresent(decodeValue.decoder.asExpression(),
                                                                    decodeValue.childIt.asExpression()));
            decode.getCode().appendStatement(ASN1.getChild(decodeValue.childIt.asExpression(),
                                                           getSetter(att).asFunctionCall(value, false)));
        }
        decode.getCode().appendStatement(ASN1.checkNoMoreChildren(decodeValue.decoder.asExpression(),
                                                                  decodeValue.childIt.asExpression()));
        headerFile.addFunctionDefinition(decode);
    }

    private void addJsonCodecs() {
        addToJson();
        addFromJson();
    }

    // Interpret "sequence (1) of X" as optional
    private boolean isOptional(BasicType t) {
        if (t.getBasicType() instanceof SequenceType seq && seq.getBound() != null) {
            LiteralExpression bound = seq.getBound().evaluate();
            if (bound instanceof IntegerLiteral ibound) {
                return ibound.getValue().equals(1L);
            }
        }
        return false;
    }

    private void addToJson() {
        final Function function = new Function("to_json", namespace);
        headerFile.addFunctionDeclaration(function);
        bodyFile.addFunctionDefinition(function);
        Expression
                j =
                function.createParameter(new TypeUsage(NlohmannJson.json, TypeUsage.Reference), "j").asExpression();
        Expression v = function.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "v").asExpression();
        for (final StructureElement att : structure.getElements()) {
            String
                    name =
                    att.getPragmas().hasPragma("json_name") ? att.getPragmas().getValue("json_name") : att.getName();
            Expression ev = getGetter(att).asFunctionCall(v, false);
            Expression jv = new ArrayAccess(j, Literal.createStringLiteral(name));
            if (isOptional(att.getType())) {
                CodeBlock block = new CodeBlock();
                block.appendExpression(new BinaryExpression(jv,
                                                            BinaryOperator.ASSIGN,
                                                            new ArrayAccess(ev, Literal.ONE)));
                function.getCode().appendStatement(new IfStatement(new UnaryExpression(UnaryOperator.NOT,
                                                                                       new Function("empty").asFunctionCall(
                                                                                               ev,
                                                                                               false)), block));
            } else {
                function.getCode().appendExpression(new BinaryExpression(jv, BinaryOperator.ASSIGN, ev));
            }

        }
    }

    private void addFromJson() {
        final Function function = new Function("from_json", namespace);
        headerFile.addFunctionDeclaration(function);
        bodyFile.addFunctionDefinition(function);
        Expression
                j =
                function.createParameter(new TypeUsage(NlohmannJson.json, TypeUsage.ConstReference),
                                         "j").asExpression();
        Expression v = function.createParameter(new TypeUsage(clazz, TypeUsage.Reference), "v").asExpression();
        function.getCode().appendExpression(new BinaryExpression(v, BinaryOperator.ASSIGN, clazz.callConstructor()));
        for (final StructureElement att : structure.getElements()) {
            String
                    name =
                    att.getPragmas().hasPragma("json_name") ? att.getPragmas().getValue("json_name") : att.getName();
            Expression ev = getSetter(att).asFunctionCall(v, false);
            Expression n = Literal.createStringLiteral(name);
            Expression jv = new ArrayAccess(j, Literal.createStringLiteral(name));

            CodeBlock block = new CodeBlock();
            function.getCode().appendStatement(new IfStatement(new Function("contains").asFunctionCall(j, false, n),
                                                               block));

            if (isOptional(att.getType())) {
                TypeUsage et = domainTranslator.getTypes().getType(((SequenceType) att.getType()).getContainedType());
                block.appendExpression(new Function("push_back").asFunctionCall(ev, false, NlohmannJson.get(jv, et)));
            } else {
                block.appendExpression(NlohmannJson.get_to(jv, ev));
            }
        }
    }

    Function addDefaultConstructor() {
        final Function defaultConstructor = clazz.createConstructor(constructors, Visibility.PUBLIC);
        bodyFile.addFunctionDefinition(defaultConstructor);

        for (final StructureElement att : structure.getElements()) {
            final Variable member = memberVariables.get(att);
            if (att.getDefault() != null) {
                defaultConstructor.setInitialValue(member,
                                                   ExpressionTranslator.createTranslator(att.getDefault(),
                                                                                         null).getReadExpression());
            }
        }
        return defaultConstructor;
    }

    Function addElementConstructor() {
        if (structure.getElements().size() > 0) {
            final Function elementConstructor = clazz.createConstructor(constructors, Visibility.PUBLIC);
            for (final StructureElement att : structure.getElements()) {
                final TypeUsage type = domainTranslator.getTypes().getType(att.getType()).getOptimalParameterType();
                final Variable member = memberVariables.get(att);
                final String attName = Mangler.mangleName(att);
                elementConstructor.setInitialValue(member,
                                                   elementConstructor.createParameter(type, attName).asExpression());
            }

            bodyFile.addFunctionDefinition(elementConstructor);
            return elementConstructor;
        } else {
            return null;
        }
    }

    private BigTuple getTupleType() {
        final List<TypeUsage> tupleTypes = new ArrayList<>();

        for (final StructureElement element : structure.getElements()) {
            tupleTypes.add(Types.getInstance().getType(element.getType()));
        }

        return new BigTuple(tupleTypes);

    }

    Function addTupleConstructor() {
        final Function tupleConstructor = clazz.createConstructor(constructors, Visibility.PUBLIC);
        tupleConstructor.declareInClass(true);

        final BigTuple tuple = getTupleType();

        final Variable
                aggregate =
                tupleConstructor.createParameter(new TypeUsage(tupleTypedef.asClass()).getOptimalParameterType(),
                                                 "aggregate");

        int i = 0;
        for (final StructureElement element : structure.getElements()) {
            final Variable member = memberVariables.get(element);

            tupleConstructor.setInitialValue(member, tuple.getTupleGetter(aggregate.asExpression(), i++));
        }

        return tupleConstructor;
    }

    Function addStructureConstructor() {
        final Function structureConstructor = clazz.createConstructor(constructors, Visibility.PUBLIC);
        structureConstructor.declareInClass(true);

        final TemplateType tt = new TemplateType("T");
        structureConstructor.addTemplateParameter(new TypenameTemplateParameter(tt));

        final Expression
                rhs =
                structureConstructor.createParameter(new TypeUsage(tt).getOptimalParameterType(), "rhs").asExpression();
        structureConstructor.createParameter(new TypeUsage(tt.asClass().referenceNestedType("TupleType"),
                                                           TypeUsage.PointerToConst), "dummy", Literal.ZERO);

        structureConstructor.getCode().appendStatement(new BinaryExpression(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                                clazz.getThis().asExpression()),
                                                                            BinaryOperator.ASSIGN,
                                                                            new Function("toTuple").asFunctionCall(rhs,
                                                                                                                   false)).asStatement());

        return structureConstructor;
    }

    Function addGenericTupleConstructor() {
        final Function tupleConstructor = clazz.createConstructor(constructors, Visibility.PUBLIC);
        tupleConstructor.declareInClass(true);

        final List<TypeUsage> tupleTypes = new ArrayList<>();
        for (int i = 1; i <= structure.getElements().size(); ++i) {
            final TemplateType tt = new TemplateType("T" + i);
            tupleConstructor.addTemplateParameter(new TypenameTemplateParameter(tt));
            tupleTypes.add(new TypeUsage(tt));
        }
        final BigTuple tuple = new BigTuple(tupleTypes);

        final Variable
                aggregate =
                tupleConstructor.createParameter(tuple.getTupleType().getOptimalParameterType(), "aggregate");

        int i = 0;
        for (final StructureElement element : structure.getElements()) {
            final Variable member = memberVariables.get(element);

            tupleConstructor.setInitialValue(member, tuple.getTupleGetter(aggregate.asExpression(), i++));
        }

        return tupleConstructor;
    }

    Function addGenericTupleAssignment() {
        final Function tupleAssignment = clazz.createMemberFunction(constructors, "operator=", Visibility.PUBLIC);
        tupleAssignment.setReturnType(new TypeUsage(clazz, TypeUsage.Reference));
        tupleAssignment.declareInClass(true);

        final List<TypeUsage> tupleTypes = new ArrayList<>();
        for (int i = 1; i <= structure.getElements().size(); ++i) {
            final TemplateType tt = new TemplateType("T" + i);
            tupleAssignment.addTemplateParameter(new TypenameTemplateParameter(tt));
            tupleTypes.add(new TypeUsage(tt));
        }
        final BigTuple tuple = new BigTuple(tupleTypes);

        final Variable
                aggregate =
                tupleAssignment.createParameter(tuple.getTupleType().getOptimalParameterType(), "aggregate");

        int i = 0;
        for (final StructureElement element : structure.getElements()) {
            final Variable member = memberVariables.get(element);
            tupleAssignment.getCode().appendStatement(new BinaryExpression(member.asExpression(),
                                                                           BinaryOperator.ASSIGN,
                                                                           tuple.getTupleGetter(aggregate.asExpression(),
                                                                                                i++)).asStatement());
        }
        tupleAssignment.getCode().appendStatement(new ReturnStatement(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                          clazz.getThis().asExpression())));
        return tupleAssignment;
    }

    Function addTupleAssignment() {
        final Function tupleAssignment = clazz.createMemberFunction(constructors, "operator=", Visibility.PUBLIC);
        tupleAssignment.setReturnType(new TypeUsage(clazz, TypeUsage.Reference));
        tupleAssignment.declareInClass(true);

        final BigTuple tuple = getTupleType();

        final Variable
                aggregate =
                tupleAssignment.createParameter(new TypeUsage(tupleTypedef.asClass()).getOptimalParameterType(),
                                                "aggregate");

        int i = 0;
        for (final StructureElement element : structure.getElements()) {
            final Variable member = memberVariables.get(element);
            tupleAssignment.getCode().appendStatement(new BinaryExpression(member.asExpression(),
                                                                           BinaryOperator.ASSIGN,
                                                                           tuple.getTupleGetter(aggregate.asExpression(),
                                                                                                i++)).asStatement());
        }
        tupleAssignment.getCode().appendStatement(new ReturnStatement(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                          clazz.getThis().asExpression())));
        return tupleAssignment;
    }

    private void addTupleTypedef() {
        final BigTuple tuple = getTupleType();
        tupleTypedef = new TypedefType("TupleType", tuple.getTupleType());
        clazz.addTypedef(typedefs, tupleTypedef, Visibility.PUBLIC);
    }

    private void addToTuple() {
        final List<Expression> tupleArgs = new ArrayList<>();
        for (final StructureElement element : structure.getElements()) {
            tupleArgs.add(memberVariables.get(element).asExpression());
        }

        final BigTuple tuple = getTupleType();
        final Expression tupleValue = tuple.callConstructor(tupleArgs);

        final Function toTuple = clazz.createMemberFunction(getters, "toTuple", Visibility.PUBLIC);
        toTuple.setReturnType(new TypeUsage(tupleTypedef.asClass()));
        toTuple.setConst(true);
        toTuple.declareInClass(true);

        toTuple.getCode().appendStatement(new ReturnStatement(tupleValue));
    }

    TypeUsage getType() {
        return type;
    }

    private void addAttributes() {
        for (final StructureElement att : structure.getElements()) {
            final TypeUsage type = domainTranslator.getTypes().getType(att.getType());
            memberVariables.put(att,
                                clazz.createMemberVariable(members, Mangler.mangleName(att), type, Visibility.PRIVATE));

            getterFunctions.put(att, addGetter(att));
            setterFunctions.put(att, addSetter(att));
        }
    }

    private Function addGetter(final StructureElement att) {
        final TypeUsage type = domainTranslator.getTypes().getType(att.getType());
        final Expression member = memberVariables.get(att).asExpression();
        final String attName = Mangler.mangleName(att);
        final Function getter = clazz.createMemberFunction(getters, "get_" + attName, Visibility.PUBLIC);
        getter.getCode().appendStatement(new ReturnStatement(member));
        getter.setReturnType(type.getOptimalParameterType());
        getter.declareInClass(true);
        getter.setConst(true);
        return getter;
    }

    private Function addSetter(final StructureElement att) {
        final TypeUsage type = domainTranslator.getTypes().getType(att.getType());
        final Expression member = memberVariables.get(att).asExpression();
        final String attName = Mangler.mangleName(att);
        final Function setter = clazz.createMemberFunction(setters, "set_" + attName, Visibility.PUBLIC);
        setter.setReturnType(type.getReferenceType());
        setter.getCode().appendStatement(new ReturnStatement(new BinaryExpression(clazz.getThis().asExpression(),
                                                                                  BinaryOperator.PTR_REF,
                                                                                  member)));
        setter.declareInClass(true);
        return setter;
    }

    private Function addLessThan() {
        final Function comparator = clazz.createMemberFunction(constructors, "operator<", Visibility.PUBLIC);
        comparator.setReturnType(new TypeUsage(FundamentalType.BOOL));
        comparator.setConst(true);
        final Variable rhs = comparator.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "rhs");

        Expression predicate = null;
        if (structure.getElements().isEmpty()) {
            predicate = Literal.FALSE;
        } else {
            final List<StructureElement> reverseElements = new ArrayList<>(structure.getElements());
            Collections.reverse(reverseElements);

            for (final StructureElement att : reverseElements) {
                final Expression lhsElt = memberVariables.get(att).asExpression();
                final Expression rhsElt = memberVariables.get(att).asMemberReference(rhs.asExpression(), false);

                predicate = buildComparator(predicate, lhsElt, rhsElt, false);
            }
        }
        comparator.getCode().appendStatement(new ReturnStatement(predicate));
        bodyFile.addFunctionDefinition(comparator);
        return comparator;
    }

    /**
     * Builds up a comparison predicate. This function should be call repeatedly for
     * each element that needs to take part in the comparison, <em>starting with the
     * least significant element</em>, and passing in the result from the previous
     * time as the predicate parameter. Note that starting with the least
     * significant element probably means iterating in reverse order.
     * <p>
     * <p>
     * the comparison predicate for the less significant elements
     * <p>
     * the lhs expression to compare
     * <p>
     * the rhs expression to compare
     * <p>
     * if true then use greater than instead of less than for the comparison
     *
     * @return
     */
    static public Expression buildComparator(final Expression predicate,
                                             final Expression lhs,
                                             final Expression rhs,
                                             final boolean reverse) {
        final BinaryOperator comparator = reverse ? BinaryOperator.GREATER_THAN : BinaryOperator.LESS_THAN;
        final Expression eltCompare = new BinaryExpression(lhs, comparator, rhs);
        final Expression
                reverseEltCompare =
                new UnaryExpression(UnaryOperator.NOT, new BinaryExpression(rhs, comparator, lhs));

        return predicate == null ?
               eltCompare :
               new BinaryExpression(eltCompare,
                                    BinaryOperator.OR,
                                    new BinaryExpression(reverseEltCompare, BinaryOperator.AND, predicate));

    }

    private Function addEquality() {
        final Function comparator = clazz.createMemberFunction(constructors, "operator==", Visibility.PUBLIC);
        comparator.setReturnType(new TypeUsage(FundamentalType.BOOL));
        comparator.setConst(true);
        final Variable rhs = comparator.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "rhs");

        Expression result = null;
        if (structure.getElements().isEmpty()) {
            result = Literal.TRUE;
        } else {
            for (final StructureElement att : structure.getElements()) {
                final Expression lhsElt = memberVariables.get(att).asExpression();
                final Expression rhsElt = memberVariables.get(att).asMemberReference(rhs.asExpression(), false);

                final Expression eltCompare = new BinaryExpression(lhsElt, BinaryOperator.EQUAL, rhsElt);

                if (result == null) {
                    result = eltCompare;
                } else {
                    result = new BinaryExpression(result, BinaryOperator.AND, eltCompare);
                }
            }
        }
        comparator.getCode().appendStatement(new ReturnStatement(result));
        bodyFile.addFunctionDefinition(comparator);
        return comparator;

    }

    private void translateStreamOperator() {
        streamOperator.setReturnType(new TypeUsage(Std.ostream, TypeUsage.Reference));
        final Expression
                stream =
                streamOperator.createParameter(new TypeUsage(Std.ostream, TypeUsage.Reference),
                                               "stream").asExpression();
        final Expression
                obj =
                streamOperator.createParameter(new TypeUsage(clazz, TypeUsage.ConstReference), "obj").asExpression();

        Expression separator = Literal.createStringLiteral("(");
        final Expression comma = Literal.createStringLiteral(",");

        Expression result = stream;

        for (final StructureElement att : structure.getElements()) {
            final Expression getter = getterFunctions.get(att).asFunctionCall(obj, false);

            result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, separator);
            result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, getter);
            separator = comma;
        }
        result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, Literal.createStringLiteral(")"));
        streamOperator.getCode().appendStatement(new ReturnStatement(result));

        headerFile.addFunctionDeclaration(streamOperator);
        bodyFile.addFunctionDefinition(streamOperator);
    }

    private final CodeFile bodyFile;
    private final Class clazz;
    private final DeclarationGroup constructors;
    private final Map<StructureElement, Function> getterFunctions = new HashMap<>();
    private final DeclarationGroup getters;
    private final CodeFile headerFile;
    private final DeclarationGroup members;

    private final Map<StructureElement, Variable> memberVariables = new HashMap<>();
    private final String name;

    private final Map<StructureElement, Function> setterFunctions = new HashMap<>();
    private final DeclarationGroup setters;
    private final StructureType structure;
    private final TypeUsage type;
    private final Function streamOperator;
    private final Namespace namespace;
    private TypedefType tupleTypedef;

    public Function getStreamOperator() {
        return streamOperator;
    }

    public StructureType getStructure() {
        return structure;
    }

}
