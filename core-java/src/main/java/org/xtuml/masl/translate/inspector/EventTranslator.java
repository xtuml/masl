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
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Types;

import java.util.ArrayList;
import java.util.List;

class EventTranslator {

    EventTranslator(final EventDeclaration event, final ObjectTranslator objectTranslator) {
        this.params = event.getParameters();
        object = event.getParentObject();
        mainObjectTranslator = org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);
        mainEventTranslator = mainObjectTranslator.getEventTranslator(event);

        this.codeFile = objectTranslator.getCodeFile();

        this.handlerClass =
                new Class(Mangler.mangleName(object) + "_" + Mangler.mangleName(event) + "Handler",
                          objectTranslator.getNamespace());
        codeFile.addClassDeclaration(handlerClass);
        group = handlerClass.createDeclarationGroup();

        createEventFunction = mainEventTranslator.getCreateFunction();
        isInstance = event.getType() == EventDeclaration.Type.NORMAL;
    }

    Class getHandlerClass() {
        return handlerClass;
    }

    void translate() {
        handlerClass.addSuperclass(Inspector.eventHandlerClass, Visibility.PUBLIC);
        addGetEvent();
        addWriteParameters();
    }

    private void addGetEvent() {
        final Function getEvent = handlerClass.createMemberFunction(group, "getEvent", Visibility.PUBLIC);
        getEvent.setReturnType(new TypeUsage(Architecture.event.getEventPtr()));
        getEvent.setConst(true);
        codeFile.addFunctionDefinition(getEvent);
        final Expression
                channel =
                getEvent.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                         "channel").asExpression();

        final Variable
                sourceObject =
                new Variable(new TypeUsage(Std.int32),
                             "sourceObjId",
                             new UnaryExpression(UnaryOperator.MINUS, Literal.ONE));
        final Variable sourceInstance = new Variable(new TypeUsage(Std.int32), "sourceInstanceId", Literal.ZERO);
        final Variable hasSource = new Variable(new TypeUsage(FundamentalType.BOOL), "hasSource");

        getEvent.getCode().appendStatement(sourceObject.asStatement());
        getEvent.getCode().appendStatement(sourceInstance.asStatement());
        getEvent.getCode().appendStatement(hasSource.asStatement());
        getEvent.getCode().appendStatement(new BinaryExpression(channel,
                                                                BinaryOperator.RIGHT_SHIFT,
                                                                hasSource.asExpression()).asStatement());
        final CodeBlock readSource = new CodeBlock();
        readSource.appendStatement(new BinaryExpression(channel,
                                                        BinaryOperator.RIGHT_SHIFT,
                                                        sourceObject.asExpression()).asStatement());
        readSource.appendStatement(new BinaryExpression(channel,
                                                        BinaryOperator.RIGHT_SHIFT,
                                                        sourceInstance.asExpression()).asStatement());

        getEvent.getCode().appendStatement(new IfStatement(hasSource.asExpression(), readSource));

        Variable thisPtr = null;

        if (isInstance) {
            thisPtr =
                    new Variable(org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object).getPointerType(),
                                 "thisVar");
            getEvent.getCode().appendStatement(thisPtr.asStatement());
            getEvent.getCode().appendStatement(new BinaryExpression(channel,
                                                                    BinaryOperator.RIGHT_SHIFT,
                                                                    thisPtr.asExpression()).asStatement());

        }

        final List<Expression> createArgs = new ArrayList<Expression>();

        for (final ParameterDefinition param : params) {
            final TypeUsage type = Types.getInstance().getType(param.getType());
            if (canRead(param.getType().getBasicType())) {
                final Variable arg = new Variable(type, Mangler.mangleName(param));
                getEvent.getCode().appendStatement(arg.asStatement());
                getEvent.getCode().appendStatement(new BinaryExpression(channel,
                                                                        BinaryOperator.RIGHT_SHIFT,
                                                                        arg.asExpression()).asStatement());
                createArgs.add(arg.asExpression());
            } else {

                final Variable arg = new Variable(type, Mangler.mangleName(param));
                getEvent.getCode().appendStatement(arg.asStatement());
                createArgs.add(arg.asExpression());
            }
        }

        createArgs.add(sourceObject.asExpression());
        createArgs.add(sourceInstance.asExpression());

        final Expression
                createExpression =
                isInstance ?
                new ConditionalExpression(thisPtr.asExpression(),
                                          createEventFunction.asFunctionCall(thisPtr.asExpression(), true, createArgs),
                                          Architecture.event.getEventPtr().callConstructor()) :
                createEventFunction.asFunctionCall(createArgs);

        getEvent.getCode().appendStatement(new ReturnStatement(createExpression));

    }

    private void addWriteParameters() {
        final Function writeParams = handlerClass.createMemberFunction(group, "writeParameters", Visibility.PUBLIC);

        writeParams.setConst(true);
        codeFile.addFunctionDefinition(writeParams);
        final Expression
                event =
                writeParams.createParameter(new TypeUsage(Architecture.event.getClazz(), TypeUsage.ConstReference),
                                            "event").asExpression();
        final Expression
                stream =
                writeParams.createParameter(new TypeUsage(Inspector.bufferedOutputStream, TypeUsage.Reference),
                                            "stream").asExpression();

        final TypeUsage eventTypeRef = new TypeUsage(mainEventTranslator.getEventClass(), TypeUsage.ConstReference);
        final Variable
                typedEvent =
                new Variable(eventTypeRef, "typedEvent", Std.dynamic_cast(eventTypeRef).asFunctionCall(event));

        writeParams.getCode().appendStatement(typedEvent.asStatement());
        for (final ParameterDefinition param : params) {
            final Statement
                    writeParam =
                    new Function("write").asFunctionCall(stream,
                                                         false,
                                                         mainEventTranslator.getParamGetter(param).asFunctionCall(
                                                                 typedEvent.asExpression(),
                                                                 false)).asStatement();
            writeParams.getCode().appendStatement(writeParam);
        }

    }

    private boolean canRead(final BasicType paramType) {
        return !(paramType.getBasicType().getActualType() == ActualType.EVENT ||
                 paramType.getBasicType().getActualType() == ActualType.DEVICE ||
                 paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
    }

    private final List<? extends ParameterDefinition> params;

    private final CodeFile codeFile;

    private final Class handlerClass;

    private final DeclarationGroup group;

    private final Function createEventFunction;

    private final ObjectDeclaration object;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;
    private final org.xtuml.masl.translate.main.object.EventTranslator mainEventTranslator;

    private final boolean isInstance;

}
