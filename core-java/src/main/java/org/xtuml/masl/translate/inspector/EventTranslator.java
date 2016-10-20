//
// File: ActionTranslator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.inspector;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.ConditionalExpression;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.UnaryExpression;
import org.xtuml.masl.cppgen.UnaryOperator;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Types;


class EventTranslator
{

  EventTranslator ( final EventDeclaration event, final ObjectTranslator objectTranslator )
  {
    this.params = event.getParameters();
    object = event.getParentObject();
    mainObjectTranslator = org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);
    mainEventTranslator = mainObjectTranslator.getEventTranslator(event);

    this.codeFile = objectTranslator.getCodeFile();

    this.handlerClass = new Class(Mangler.mangleName(object) + "_" + Mangler.mangleName(event) + "Handler",
                                  objectTranslator.getNamespace());
    codeFile.addClassDeclaration(handlerClass);
    group = handlerClass.createDeclarationGroup();

    createEventFunction = mainEventTranslator.getCreateFunction();
    isInstance = event.getType() == EventDeclaration.Type.NORMAL;
  }

  Class getHandlerClass ()
  {
    return handlerClass;
  }

  void translate ()
  {
    handlerClass.addSuperclass(Inspector.eventHandlerClass, Visibility.PUBLIC);
    addGetEvent();
    addWriteParameters();
  }

  private void addGetEvent ()
  {
    final Function getEvent = handlerClass.createMemberFunction(group, "getEvent", Visibility.PUBLIC);
    getEvent.setReturnType(new TypeUsage(Architecture.event.getEventPtr()));
    getEvent.setConst(true);
    codeFile.addFunctionDefinition(getEvent);
    final Expression channel = getEvent.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference), "channel")
                                       .asExpression();

    final Variable sourceObject = new Variable(new TypeUsage(Std.int32), "sourceObjId", new UnaryExpression(UnaryOperator.MINUS,
                                                                                                            Literal.ONE));
    final Variable sourceInstance = new Variable(new TypeUsage(Std.int32), "sourceInstanceId", Literal.ZERO);
    final Variable hasSource = new Variable(new TypeUsage(FundamentalType.BOOL), "hasSource");


    getEvent.getCode().appendStatement(sourceObject.asStatement());
    getEvent.getCode().appendStatement(sourceInstance.asStatement());
    getEvent.getCode().appendStatement(hasSource.asStatement());
    getEvent.getCode()
            .appendStatement(new BinaryExpression(channel, BinaryOperator.RIGHT_SHIFT, hasSource.asExpression())
                                                                                                                .asStatement());
    final CodeBlock readSource = new CodeBlock();
    readSource.appendStatement(new BinaryExpression(channel, BinaryOperator.RIGHT_SHIFT, sourceObject.asExpression())
                                                                                                                     .asStatement());
    readSource.appendStatement(new BinaryExpression(channel, BinaryOperator.RIGHT_SHIFT, sourceInstance.asExpression())
                                                                                                                       .asStatement());

    getEvent.getCode().appendStatement(new IfStatement(hasSource.asExpression(), readSource));

    Variable thisPtr = null;

    if ( isInstance )
    {
      thisPtr = new Variable(
                             org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object).getPointerType(), "thisVar");
      getEvent.getCode().appendStatement(thisPtr.asStatement());
      getEvent.getCode()
              .appendStatement(new BinaryExpression(channel, BinaryOperator.RIGHT_SHIFT, thisPtr.asExpression())
                                                                                                                .asStatement());

    }

    final List<Expression> createArgs = new ArrayList<Expression>();

    for ( final ParameterDefinition param : params )
    {
      final TypeUsage type = Types.getInstance().getType(param.getType());
      if ( canRead(param.getType().getBasicType()) )
      {
        final Variable arg = new Variable(type, Mangler.mangleName(param));
        getEvent.getCode().appendStatement(arg.asStatement());
        getEvent.getCode()
                .appendStatement(new BinaryExpression(channel, BinaryOperator.RIGHT_SHIFT, arg.asExpression())
                                                                                                              .asStatement());
        createArgs.add(arg.asExpression());
      }
      else
      {

        final Variable arg = new Variable(type, Mangler.mangleName(param));
        getEvent.getCode().appendStatement(arg.asStatement());
        createArgs.add(arg.asExpression());
      }
    }

    createArgs.add(sourceObject.asExpression());
    createArgs.add(sourceInstance.asExpression());

    final Expression createExpression = isInstance ? new ConditionalExpression(thisPtr.asExpression(),
                                                                               createEventFunction.asFunctionCall(thisPtr.asExpression(),
                                                                                                                  true,
                                                                                                                  createArgs),
                                                                               Architecture.event.getEventPtr()
                                                                                                 .callConstructor())
                                                  : createEventFunction.asFunctionCall(createArgs);

    getEvent.getCode().appendStatement(new ReturnStatement(createExpression));


  }

  private void addWriteParameters ()
  {
    final Function writeParams = handlerClass.createMemberFunction(group, "writeParameters", Visibility.PUBLIC);

    writeParams.setConst(true);
    codeFile.addFunctionDefinition(writeParams);
    final Expression event = writeParams.createParameter(new TypeUsage(Architecture.event.getClazz(), TypeUsage.ConstReference),
                                                         "event").asExpression();
    final Expression stream = writeParams.createParameter(new TypeUsage(Inspector.bufferedOutputStream, TypeUsage.Reference),
                                                          "stream")
                                         .asExpression();

    final TypeUsage eventTypeRef = new TypeUsage(mainEventTranslator.getEventClass(), TypeUsage.ConstReference);
    final Variable typedEvent = new Variable(eventTypeRef,
                                             "typedEvent", Std.dynamic_cast(eventTypeRef).asFunctionCall(event));

    writeParams.getCode().appendStatement(typedEvent.asStatement());
    for ( final ParameterDefinition param : params )
    {
      final Statement writeParam = new Function("write").asFunctionCall(stream,
                                                                        false,
                                                                        mainEventTranslator.getParamGetter(param)
                                                                                           .asFunctionCall(typedEvent.asExpression(),
                                                                                                           false))
                                                        .asStatement();
      writeParams.getCode().appendStatement(writeParam);
    }

  }

  private boolean canRead ( final BasicType paramType )
  {
    return !(paramType.getBasicType().getActualType() == ActualType.EVENT || paramType.getBasicType().getActualType() == ActualType.DEVICE || paramType.getBasicType()
                                                                                                                                                       .getActualType() == ActualType.ANY_INSTANCE);
  }

  private final List<? extends ParameterDefinition>                  params;

  private final CodeFile                                             codeFile;

  private final Class                                                handlerClass;


  private final DeclarationGroup                                     group;


  private final Function                                             createEventFunction;

  private final ObjectDeclaration                                    object;
  private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;
  private final org.xtuml.masl.translate.main.object.EventTranslator  mainEventTranslator;

  private final boolean                                              isInstance;


}
