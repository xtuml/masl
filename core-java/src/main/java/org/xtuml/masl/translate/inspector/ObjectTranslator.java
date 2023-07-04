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
package org.xtuml.masl.translate.inspector;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.SwitchStatement.CaseCondition;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ObjectTranslator {

    private final ObjectDeclaration object;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator objTrans;

    private final CodeFile codeFile;
    private final CodeFile headerFile;
    private final Class handlerClass;

    private final Map<ObjectService, ActionTranslator> serviceTranslators = new HashMap<>();
    private final Map<State, ActionTranslator> stateTranslators = new HashMap<>();
    private final Map<EventDeclaration, EventTranslator> eventTranslators = new HashMap<>();
    private final DomainTranslator domainTranslator;
    private final DeclarationGroup relationshipNavigators;
    private final Namespace namespace;

    ObjectTranslator(final ObjectDeclaration object) {
        domainTranslator = DomainTranslator.getInstance(object.getDomain());
        namespace = new Namespace(Mangler.mangleName(object), domainTranslator.getNamespace());

        this.object = object;

        objTrans = org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);
        this.codeFile = domainTranslator.getLibrary().createBodyFile("Inspector" + Mangler.mangleFile(object));
        this.headerFile = domainTranslator.getLibrary().createPrivateHeader("Inspector" + Mangler.mangleFile(object));

        this.handlerClass = new Class(Mangler.mangleName(object) + "Handler", namespace);
        headerFile.addClassDeclaration(handlerClass);

        final DeclarationGroup constructors = handlerClass.createDeclarationGroup("Constructors");
        relationshipNavigators = handlerClass.createDeclarationGroup("Relationship Navigators");

        for (final ObjectService service : object.getServices()) {
            final ActionTranslator trans = new ActionTranslator(service, this);
            serviceTranslators.put(service, trans);
            trans.translate();
        }

        for (final State state : object.getStates()) {
            final ActionTranslator trans = new ActionTranslator(state, this);
            stateTranslators.put(state, trans);
            trans.translate();
        }

        for (final EventDeclaration event : object.getAllEvents()) {
            final EventTranslator trans = new EventTranslator(event, this);
            eventTranslators.put(event, trans);
            trans.translate();
        }

        final Function constructor = handlerClass.createConstructor(constructors, Visibility.PUBLIC);

        codeFile.addFunctionDefinition(constructor);

        final Class actionPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.actionHandlerClass));
        final Function registerServiceHandler = new Function("registerServiceHandler");
        for (final ObjectService service : object.getServices()) {
            final ActionTranslator servTrans = serviceTranslators.get(service);
            // If a service uses a prohibited type then it will not have
            // a corresponding serviceTranslator.
            if (servTrans != null) {
                final Expression id = objTrans.getServiceTranslator(service).getServiceId();
                final Class servHandlerClass = servTrans.getHandlerClass();

                constructor.getCode().appendExpression(registerServiceHandler.asFunctionCall(id,
                                                                                             actionPtrType.callConstructor(
                                                                                                     new NewExpression(
                                                                                                             new TypeUsage(
                                                                                                                     servHandlerClass)))));
            }
        }

        final Function registerStateHandler = new Function("registerStateHandler");
        for (final State state : object.getStates()) {
            final ActionTranslator stateTrans = stateTranslators.get(state);
            final Expression id = objTrans.getStateActionTranslator(state).getStateId();
            final Class stateHandlerClass = stateTrans.getHandlerClass();

            constructor.getCode().appendExpression(registerStateHandler.asFunctionCall(id,
                                                                                       actionPtrType.callConstructor(new NewExpression(
                                                                                               new TypeUsage(
                                                                                                       stateHandlerClass)))));

        }

        final Class eventPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.eventHandlerClass));
        final Function registerEventHandler = new Function("registerEventHandler");
        for (final EventDeclaration event : object.getAllEvents()) {
            final EventTranslator eventTrans = eventTranslators.get(event);
            final Expression id = objTrans.getEventTranslator(event).getEventId();
            final Class eventHandlerClass = eventTrans.getHandlerClass();

            constructor.getCode().appendExpression(registerEventHandler.asFunctionCall(id,
                                                                                       eventPtrType.callConstructor(new NewExpression(
                                                                                               new TypeUsage(
                                                                                                       eventHandlerClass)))));

        }

    }

    void translate() {
        addHandler();
        addObjectWriter();
        addObjectCreator();
        addIdentifierGetter();
        addObjectPtrReader();
        addObjectNavigator();
    }

    void addHandler() {
        handlerClass.addSuperclass(Inspector.getObjectHandlerClass(objTrans.getMainClass()), Visibility.PUBLIC);
    }

    Class getHandlerClass() {
        return handlerClass;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    private void addObjectWriter() {
        final Class mainClass = objTrans.getMainClass();

        final Function
                write =
                Inspector.bufferedOutputStream.specialiseMemberFunction("write",
                                                                        TemplateSpecialisation.create(new TypeUsage(
                                                                                mainClass)));

        final Expression
                instance =
                write.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "instance").asExpression();

        write.getCode().appendStatement(new Function("write").asFunctionCall(objTrans.getGetId().asFunctionCall(instance,
                                                                                                                false)).asStatement());

        for (final AttributeDeclaration att : object.getAttributes()) {
            write.getCode().appendStatement(writeAttribute(att, instance));
        }

        if (object.hasCurrentState()) {
            write.getCode().appendStatement(writeCurrentState(instance));
        }

        for (final RelationshipSpecification rel : object.getRelationships()) {
            write.getCode().appendStatement(writeObjectRel(rel, instance));
        }

        codeFile.addFunctionDefinition(write);

    }

    private void addObjectCreator() {
        final Function
                creator =
                handlerClass.createMemberFunction(relationshipNavigators, "createInstance", Visibility.PUBLIC);
        creator.setConst(true);
        final Expression
                channel =
                creator.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                        "channel").asExpression();

        final List<Expression> createParams = new ArrayList<>();
        Expression reader = channel;

        for (final AttributeDeclaration att : object.getAttributes()) {
            if (att.isIdentifier() || !att.isReferential()) {
                if (att.getType().getBasicType().getActualType() == ActualType.TIMER) {
                    createParams.add(Architecture.Timer.createTimer);
                } else {
                    final Variable
                            var =
                            new Variable(Types.getInstance().getType(att.getType()), Mangler.mangleName(att));
                    createParams.add(var.asExpression());
                    creator.getCode().appendStatement(var.asStatement());
                    reader = new BinaryExpression(reader, BinaryOperator.RIGHT_SHIFT, var.asExpression());
                }
            }
        }

        if (object.hasCurrentState()) {
            final Variable var = new Variable(new TypeUsage(FundamentalType.INT), "currentState");
            createParams.add(objTrans.getNormalFsm().getStateEnum().callConstructor(var.asExpression()));
            creator.getCode().appendStatement(var.asStatement());
            reader = new BinaryExpression(reader, BinaryOperator.RIGHT_SHIFT, var.asExpression());
        }

        creator.getCode().appendStatement(reader.asStatement());

        final Variable
                instance =
                new Variable(objTrans.getPointerType(),
                             "instance",
                             objTrans.getCreateInstance().asFunctionCall(createParams));

        creator.getCode().appendStatement(instance.asStatement());
        creator.getCode().appendStatement(new BinaryExpression(channel,
                                                               BinaryOperator.LEFT_SHIFT,
                                                               objTrans.getGetId().asFunctionCall(instance.asExpression(),
                                                                                                  true)).asStatement());

        codeFile.addFunctionDefinition(creator);

    }

    private void addObjectPtrReader() {
        final Function
                read =
                Inspector.bufferedInputStream.specialiseMemberFunction("read",
                                                                       TemplateSpecialisation.create(objTrans.getPointerType()));

        final Expression
                instance =
                read.createParameter(objTrans.getPointerType().getReferenceType(), "instance").asExpression();

        final Variable valid = new Variable(new TypeUsage(FundamentalType.BOOL), "valid");
        read.getCode().appendStatement(valid.asStatement());
        read.getCode().appendStatement(new Function("read").asFunctionCall(valid.asExpression()).asStatement());

        final CodeBlock doIfValid = new CodeBlock();
        final CodeBlock doIfNotValid = new CodeBlock();
        final IfStatement ifValid = new IfStatement(valid.asExpression(), doIfValid, doIfNotValid);
        read.getCode().appendStatement(ifValid);

        final Variable id = new Variable(new TypeUsage(Architecture.ID_TYPE), "archId");
        doIfValid.appendStatement(id.asStatement());
        doIfValid.appendStatement(new Function("read").asFunctionCall(id.asExpression()).asStatement());

        final Expression getter = objTrans.getGetInstance().asFunctionCall(id.asExpression());

        doIfValid.appendStatement(new BinaryExpression(instance, BinaryOperator.ASSIGN, getter).asStatement());

        doIfNotValid.appendStatement(new BinaryExpression(instance,
                                                          BinaryOperator.ASSIGN,
                                                          Architecture.nullPointer).asStatement());

        codeFile.addFunctionDefinition(read);

    }

    private Statement writeCurrentState(final Expression instance) {
        return new Function("write").asFunctionCall(Std.static_cast(new TypeUsage(FundamentalType.INT)).asFunctionCall(
                objTrans.getNormalFsm().getGetCurrentState().asFunctionCall(instance, false))).asStatement();
    }

    private Statement writeAttribute(final AttributeDeclaration att, final Expression instance) {
        Statement result = null;
        final Function write = new Function("write");
        if (!att.isIdentifier() && att.isReferential()) {
            final CodeBlock block = new CodeBlock();
            result = block;

            // Navigate the relationship to get the referenced attribute. It may be
            // that the concrete implementation for the object provides a more
            // efficient way of finding the data (eg by storing it locally) via its
            // getter, but as we
            // have to navigate the relationship anyway to check validity we might as
            // well go the hole hog.
            final ReferentialAttributeDefinition refAtt = att.getRefAttDefs().get(0);
            final Function
                    navigator =
                    objTrans.getRelationshipTranslator(refAtt.getRelationship()).getPublicAccessors().getNavigateFunction();
            final Variable
                    related =
                    new Variable(navigator.getReturnType(), "related", navigator.asFunctionCall(instance, false));
            block.appendStatement(related.asStatement());

            final CodeBlock doesntExistBlock = new CodeBlock();
            doesntExistBlock.appendStatement(write.asFunctionCall(Literal.FALSE).asStatement());

            final ObjectDeclaration destObj = refAtt.getRelationship().getDestinationObject();
            final AttributeDeclaration destAtt = refAtt.getDestinationAttribute();
            final Function
                    attGetter =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(destObj).getAttributeGetter(
                            destAtt);

            final CodeBlock existsBlock = new CodeBlock();
            existsBlock.appendStatement(write.asFunctionCall(Literal.TRUE).asStatement());
            existsBlock.appendStatement(write.asFunctionCall(attGetter.asFunctionCall(related.asExpression(),
                                                                                      true)).asStatement());

            final IfStatement check = new IfStatement(related.asExpression(), existsBlock, doesntExistBlock);

            block.appendStatement(check);
        } else {
            if (att.getType().getBasicType().getActualType() == ActualType.TIMER) {
                final Expression handle = objTrans.getAttributeGetter(att).asFunctionCall(instance, false);
                final Expression timer = Architecture.Timer.getTimer(handle);
                result = write.asFunctionCall(timer).asStatement();
            } else {
                result =
                        write.asFunctionCall(objTrans.getAttributeGetter(att).asFunctionCall(instance,
                                                                                             false)).asStatement();
            }
        }
        return result;
    }

    private Statement writeObjectRel(final RelationshipSpecification rel, final Expression instance) {
        final StatementGroup result = new StatementGroup();
        final Function countWrite = new Function("write");
        countWrite.addTemplateSpecialisation(new TypeUsage(FundamentalType.INT));
        final Function write = new Function("write");

        if (rel.getCardinality() == MultiplicityType.ONE) {
            final Function
                    navigator =
                    objTrans.getRelationshipTranslator(rel).getPublicAccessors().getNavigateFunction();
            result.appendStatement(write.asFunctionCall(navigator.asFunctionCall(instance, false)).asStatement());
        } else {
            final Function count = objTrans.getRelationshipTranslator(rel).getPublicAccessors().getCountFunction();
            result.appendStatement(countWrite.asFunctionCall(count.asFunctionCall(instance, false)).asStatement());
        }
        return result;
    }

    private void addObjectNavigator() {
        final Function
                write =
                handlerClass.createMemberFunction(relationshipNavigators, "writeRelatedInstances", Visibility.PUBLIC);
        write.setConst(true);
        final Expression
                channel =
                write.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                      "channel").asExpression();
        final Expression instance = write.createParameter(objTrans.getPointerType(), "instance").asExpression();
        final Expression relId = write.createParameter(new TypeUsage(FundamentalType.INT), "relId").asExpression();

        final List<CaseCondition> relChoices = new ArrayList<>();

        int relIdNo = 0;
        for (final RelationshipSpecification rel : object.getRelationships()) {
            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    destTranslator =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(rel.getDestinationObject());
            final Expression
                    destObjectHandler =
                    Inspector.getObjectHandler(domainTranslator.getMainTranslator().getDomainId(),
                                               destTranslator.getObjectId(),
                                               destTranslator.getMainClass());

            final Expression
                    navigation =
                    objTrans.getRelationshipTranslator(rel).getPublicAccessors().getNavigateFunction().asFunctionCall(
                            instance,
                            true);

            final Expression
                    nullResult =
                    (rel.getCardinality() == MultiplicityType.MANY) ?
                    Architecture.set(destTranslator.getPointerType()).callConstructor() :
                    destTranslator.createPointer();

            final Expression result = new ConditionalExpression(instance, navigation, nullResult);

            final Expression
                    writeInstances =
                    new Function("writeInstances").asFunctionCall(destObjectHandler, false, channel, result);

            final StatementGroup caseCode = new StatementGroup();
            caseCode.appendStatement(writeInstances.asStatement());
            caseCode.appendStatement(new BreakStatement());

            relChoices.add(new CaseCondition(new Literal(relIdNo++), caseCode));
        }

        write.getCode().appendStatement(new SwitchStatement(relId, relChoices));

        codeFile.addFunctionDefinition(write);

    }

    private void addIdentifierGetter() {
        final Function
                getter =
                handlerClass.createMemberFunction(relationshipNavigators, "getIdentifierText", Visibility.PUBLIC);
        getter.setConst(true);
        getter.setReturnType(new TypeUsage(Std.string));
        final Expression instance = getter.createParameter(objTrans.getPointerType(), "instance").asExpression();

        Expression text = null;

        for (final AttributeDeclaration att : object.getAttributes()) {
            if (att.isPreferredIdentifier()) {
                final Expression
                        attText =
                        Boost.lexicalCast(new TypeUsage(Std.string),
                                          objTrans.getAttributeGetter(att).asFunctionCall(instance, true));
                if (text == null) {
                    text = attText;
                } else {
                    text =
                            new BinaryExpression(new BinaryExpression(text,
                                                                      BinaryOperator.PLUS,
                                                                      Literal.createStringLiteral(",")),
                                                 BinaryOperator.PLUS,
                                                 attText);
                }
            }
        }

        getter.getCode().appendStatement(new ReturnStatement(text));

        codeFile.addFunctionDefinition(getter);

    }

    public CodeFile getCodeFile() {
        return codeFile;
    }

    public CodeFile getHeaderFile() {
        return headerFile;
    }

}
