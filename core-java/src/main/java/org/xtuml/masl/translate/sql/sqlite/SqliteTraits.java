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
package org.xtuml.masl.translate.sql.sqlite;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.*;
import org.xtuml.masl.translate.main.object.EventTranslator;
import org.xtuml.masl.translate.sql.main.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SqliteTraits implements DatabaseTraits {

    public SqliteTraits() {
    }

    @Override
    public String getName() {
        return "Sqlite";
    }

    @Override
    public Namespace getNameSpace() {
        return SqliteDatabase.sqliteNamespace;
    }

    @Override
    public Class getBlobClass() {
        return SqliteDatabase.blobClass;
    }

    @Override
    public String getLibrarySuffix() {
        return "sqlite";
    }

    @Override
    public ObjectToTableTranslator createObjectToTableTranslator(final ObjectTranslator objectTranslator,
                                                                 final ObjectDeclaration objectDeclaration) {
        return new SqliteObjectToTableTranslator(objectTranslator, objectDeclaration);
    }

    @Override
    public BinaryRelationshipToTableTranslator createBinaryRelationshipToTableTranslator(final NormalRelationshipDeclaration relationshipDeclaration) {
        return new SqliteBinaryRelationshipToTableTranslator(relationshipDeclaration);
    }

    @Override
    public TenaryRelationshipToTableTranslator createTenaryRelationshipToTableTranslator(final AssociativeRelationshipDeclaration relationshipDeclaration) {
        return new SqliteTenaryRelationshipToTableTranslator(relationshipDeclaration);
    }

    @Override
    public SubTypeRelationshipToTableTranslator createSubTypeRelationshipToTableTranslator(final SubtypeRelationshipDeclaration relationshipDeclaration,
                                                                                           final ObjectDeclaration derivedObject) {
        return new SqliteSubTypeRelationshipToTableTranslator(relationshipDeclaration, derivedObject);
    }

    public static Function registerEventCodec = new Function("registerCodec");

    @Override
    public void addEventCode(final Namespace namespace,
                             final CodeFile codeFile,
                             final ObjectDeclaration object,
                             final EventDeclaration event) {
        String mangledName = Mangler.mangleName(event.getParentObject()) + "_" + Mangler.mangleName(event);
        if (event.getParentObject() != object) {
            mangledName = Mangler.mangleName(object) + "_" + mangledName;
        }

        final org.xtuml.masl.translate.main.object.ObjectTranslator
                objectTrans =
                org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);
        final EventTranslator eventTrans = objectTrans.getEventTranslator(event);

        final Function encodeFn = createEventEncoder(namespace, mangledName, event, eventTrans);
        codeFile.addFunctionDefinition(encodeFn);

        final Function decodeFn = createEventDecoder(namespace, mangledName, event, eventTrans);
        codeFile.addFunctionDefinition(decodeFn);

        final Expression domainId = objectTrans.getDomainTranslator().getDomainId();
        final Expression objectId = objectTrans.getObjectId();
        final Expression eventId = objectTrans.getEventId(event);

        final Variable
                register =
                new Variable(new TypeUsage(FundamentalType.BOOL),
                             "register" + mangledName,
                             namespace,
                             registerEventCodec.asFunctionCall(SqliteDatabase.eventParameterCodecs,
                                                               false,
                                                               domainId,
                                                               objectId,
                                                               eventId,
                                                               encodeFn.asFunctionPointer(),
                                                               decodeFn.asFunctionPointer()));

        codeFile.addVariableDefinition(register);

    }

    private Function createEventDecoder(final Namespace namespace,
                                        final String mangledName,
                                        final EventDeclaration event,
                                        final EventTranslator eventTrans) {
        final Function decodeFn = new Function("decode_" + mangledName, namespace);
        decodeFn.setReturnType(new TypeUsage(Architecture.event.getEventPtr()));
        final Variable blob = decodeFn.createParameter(new TypeUsage(getBlobClass(), TypeUsage.Reference), "blob");

        final List<Expression> createArgs = new ArrayList<>();

        if (event.getParameters().size() > 0) {
            final StatementGroup vars = new StatementGroup();
            decodeFn.getCode().appendStatement(vars);

            final TypeUsage iterator = new TypeUsage(getBlobClass().referenceNestedType("const_iterator"));
            final Expression bufBegin = new Function("begin").asFunctionCall(blob.asExpression(), false);

            final Class decoderType = ASN1.BERDecoder(iterator);
            final Variable
                    decoder =
                    new Variable(new TypeUsage(decoderType), "decoder", Collections.singletonList(bufBegin));
            decodeFn.getCode().appendStatement(decoder.asStatement());

            decodeFn.getCode().appendStatement(ASN1.checkHeader(decoder.asExpression(), ASN1.sequenceTag, true));
            final Variable childIt = ASN1.getChildIterator(decoder.asExpression(), iterator);
            decodeFn.getCode().appendStatement(childIt.asStatement());

            for (final ParameterDefinition param : event.getParameters()) {

                final Variable
                        var =
                        new Variable(Types.getInstance().getType(param.getType()), Mangler.mangleName(param));
                vars.appendStatement(var.asStatement());
                decodeFn.getCode().appendStatement(ASN1.checkChildPresent(decoder.asExpression(),
                                                                          childIt.asExpression()));
                decodeFn.getCode().appendStatement(ASN1.getChild(childIt.asExpression(), var.asExpression()));
                createArgs.add(var.asExpression());
            }
            decodeFn.getCode().appendStatement(ASN1.checkNoMoreChildren(decoder.asExpression(),
                                                                        childIt.asExpression()));
        }

        final Expression
                resultEvent =
                Architecture.event.getEventPtr().callConstructor(new NewExpression(new TypeUsage(eventTrans.getEventClass()),
                                                                                   createArgs));
        decodeFn.getCode().appendStatement(new ReturnStatement(resultEvent));
        return decodeFn;
    }

    private Function createEventEncoder(final Namespace namespace,
                                        final String mangledName,
                                        final EventDeclaration event,
                                        final EventTranslator eventTrans) {
        final Function encodeFn = new Function("encode_" + mangledName, namespace);
        final Variable eventVar = encodeFn.createParameter(new TypeUsage(Architecture.event.getEventPtr()), "event");
        final Variable blob = encodeFn.createParameter(new TypeUsage(getBlobClass(), TypeUsage.Reference), "blob");

        if (event.getParameters().size() > 0) {
            final TypeUsage eventPtr = new TypeUsage(Boost.getSharedPtrType(new TypeUsage(eventTrans.getEventClass())));

            final Variable
                    thisEventVar =
                    new Variable(eventPtr,
                                 "thisEvent",
                                 Boost.dynamic_pointer_cast(new TypeUsage(eventTrans.getEventClass()),
                                                            eventVar.asExpression()));
            encodeFn.getCode().appendStatement(thisEventVar.asStatement());

            final Variable
                    encoder =
                    new Variable(new TypeUsage(ASN1.DEREncoder),
                                 "encoder",
                                 Collections.singletonList(ASN1.sequenceTag));
            encodeFn.getCode().appendStatement(encoder.asStatement());

            for (final ParameterDefinition param : event.getParameters()) {
                encodeFn.getCode().appendStatement(ASN1.addChild(encoder.asExpression(),
                                                                 eventTrans.getParamGetter(param).asFunctionCall(
                                                                         thisEventVar.asExpression(),
                                                                         true)));
            }
            encodeFn.getCode().appendStatement(new Function("assign").asFunctionCall(blob.asExpression(),
                                                                                     false,
                                                                                     new Function("begin").asFunctionCall(
                                                                                             encoder.asExpression(),
                                                                                             false),
                                                                                     new Function("end").asFunctionCall(
                                                                                             encoder.asExpression(),
                                                                                             false)).asStatement());

        }
        return encodeFn;
    }

    @Override
    public ThrowStatement throwDatabaseException(final String error) {
        return SqliteDatabase.throwDatabaseException(error);
    }

    @Override
    public ThrowStatement throwDatabaseException(final Expression error) {
        return SqliteDatabase.throwDatabaseException(error);
    }

    @Override
    public SqlCritera createSqlCriteria(final ObjectDeclaration objectDecl, final String criteriaVarName) {
        return new SqliteCritera(objectDecl, criteriaVarName);
    }

}
