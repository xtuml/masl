/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.inspector;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.SwitchStatement.CaseCondition;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.*;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;

import java.util.ArrayList;
import java.util.List;

class ActionTranslator {

    ActionTranslator(final DomainService service, final DomainTranslator domainTranslator) {
        this.localVars = service.getLocalVariables();
        this.params = service.getParameters();

        this.codeFile = domainTranslator.getCodeFile();

        this.handlerClass = new Class(Mangler.mangleName(service) + "Handler", domainTranslator.getNamespace());
        this.invokerClass = new Class(Mangler.mangleName(service) + "Invoker", domainTranslator.getNamespace());
        codeFile.addClassDeclaration(handlerClass);
        codeFile.addClassDeclaration(invokerClass);
        group = handlerClass.createDeclarationGroup();

        final DomainServiceTranslator serviceTranslator = DomainServiceTranslator.getInstance(service);
        // Only the domain and terminator based services are invoked using the
        // service interceptor classes, any other services should be directly
        // invoked.
        serviceInterceptor = serviceTranslator.getServiceInterceptor();
        if (serviceInterceptor != null) {
            serviceFunction = new Function("callService");
        } else {
            serviceFunction = serviceTranslator.getFunction();
        }

        isInstance = false;
        object = null;
        mainObjectTranslator = null;

    }

    ActionTranslator(final ObjectService service, final ObjectTranslator objectTranslator) {
        this.localVars = service.getLocalVariables();
        this.params = service.getParameters();
        object = service.getParentObject();
        mainObjectTranslator = org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);

        this.codeFile = objectTranslator.getCodeFile();

        this.handlerClass = new Class(Mangler.mangleName(service) + "Handler", objectTranslator.getNamespace());
        this.invokerClass = new Class(Mangler.mangleName(service) + "Invoker", objectTranslator.getNamespace());
        codeFile.addClassDeclaration(handlerClass);
        codeFile.addClassDeclaration(invokerClass);
        group = handlerClass.createDeclarationGroup();
        serviceInterceptor = null;
        serviceFunction = ObjectServiceTranslator.getInstance(service).getFunction();
        isInstance = service.isInstance();
    }

    ActionTranslator(final DomainTerminatorService service, final TerminatorTranslator terminatorTranslator) {
        this.localVars = service.getLocalVariables();
        this.params = service.getParameters();

        this.codeFile = terminatorTranslator.getCodeFile();

        this.handlerClass = new Class(Mangler.mangleName(service) + "Handler", terminatorTranslator.getNamespace());
        this.invokerClass = new Class(Mangler.mangleName(service) + "Invoker", terminatorTranslator.getNamespace());
        codeFile.addClassDeclaration(handlerClass);
        codeFile.addClassDeclaration(invokerClass);
        group = handlerClass.createDeclarationGroup();
        serviceInterceptor = null;
        serviceFunction = TerminatorServiceTranslator.getInstance(service).getFunction();
        isInstance = false;
        object = null;
        mainObjectTranslator = null;
    }

    ActionTranslator(final ProjectTerminatorService service,
                     final ProjectTranslator projectTranslator,
                     final Namespace namespace) {
        this.localVars = service.getLocalVariables();
        this.params = service.getParameters();

        this.codeFile = projectTranslator.getCodeFile();

        this.handlerClass = new Class(Mangler.mangleName(service) + "Handler", namespace);
        this.invokerClass = new Class(Mangler.mangleName(service) + "Invoker", namespace);
        codeFile.addClassDeclaration(handlerClass);
        codeFile.addClassDeclaration(invokerClass);
        group = handlerClass.createDeclarationGroup();
        serviceInterceptor = null;
        serviceFunction = ProjectTerminatorServiceTranslator.getInstance(service).getFunction();
        isInstance = false;
        object = null;
        mainObjectTranslator = null;
    }

    ActionTranslator(final State state, final ObjectTranslator objectTranslator) {
        this.localVars = state.getLocalVariables();
        this.params = state.getParameters();
        object = state.getParentObject();
        mainObjectTranslator = org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(object);

        this.codeFile = objectTranslator.getCodeFile();

        this.handlerClass = new Class(Mangler.mangleName(state) + "Handler", objectTranslator.getNamespace(), codeFile);
        codeFile.addClassDeclaration(handlerClass);
        group = handlerClass.createDeclarationGroup();
        serviceFunction = null;
        serviceInterceptor = null;
        invokerClass = null;
        isInstance = state.getType() == State.Type.NORMAL || state.getType() == State.Type.TERMINAL;
    }

    Class getHandlerClass() {
        return handlerClass;
    }

    void translate() {
        handlerClass.addSuperclass(Inspector.actionHandlerClass, Visibility.PUBLIC);
        if (serviceFunction != null) {
            addInvoker();
        }
        addLocalVarsWriter();
    }

    private void addInvoker() {
        final DeclarationGroup functions = invokerClass.createDeclarationGroup();
        final DeclarationGroup vars = invokerClass.createDeclarationGroup();
        final Function constructor = invokerClass.createConstructor(functions, Visibility.PUBLIC);
        constructor.declareInClass(true);

        final Expression
                channel =
                constructor.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                            "channel").asExpression();

        Variable thisPtr = null;

        if (isInstance) {
            thisPtr =
                    invokerClass.createMemberVariable(vars,
                                                      "thisVar",
                                                      org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(
                                                              object).getPointerType(),
                                                      Visibility.PRIVATE);
            constructor.getCode().appendStatement(new BinaryExpression(channel,
                                                                       BinaryOperator.RIGHT_SHIFT,
                                                                       thisPtr.asExpression()).asStatement());

        }

        final Function invoker = invokerClass.createMemberFunction(functions, "operator()", Visibility.PUBLIC);
        invoker.declareInClass(true);

        final List<Expression> invokeArgs = new ArrayList<>();

        for (final ParameterDefinition param : params) {
            final TypeUsage type = Types.getInstance().getType(param.getType());
            if (canRead(param.getType().getBasicType())) {
                final Variable
                        arg =
                        invokerClass.createMemberVariable(vars, Mangler.mangleName(param), type, Visibility.PRIVATE);
                constructor.getCode().appendStatement(new BinaryExpression(channel,
                                                                           BinaryOperator.RIGHT_SHIFT,
                                                                           arg.asExpression()).asStatement());
                invokeArgs.add(arg.asExpression());
            } else {

                final Variable arg = new Variable(type, Mangler.mangleName(param));
                invoker.getCode().appendStatement(arg.asStatement());
                invokeArgs.add(arg.asExpression());
            }
        }

        Expression
                invokeExpression =
                isInstance ?
                serviceFunction.asFunctionCall(thisPtr.asExpression(), true, invokeArgs) :
                serviceFunction.asFunctionCall(invokeArgs);

        // If the service has an associated interceptor class then
        // do not directly invoke the function, invoke through the
        // interceptor class which will decide whether to invoke a
        // local or remote implementation.
        if (serviceInterceptor != null) {
            // Create cpp line:
            // ::masld_FMT::maslsi_add_aerial_to_collecting_site::instance().callService()(
            // maslp_aerial_name, maslp_minimum_operating_frequency);
            final Expression instanceFnCall = serviceInterceptor.asClass().callStaticFunction("instance");
            invokeExpression =
                    new FunctionObjectCall(serviceFunction.asFunctionCall(instanceFnCall, false), invokeArgs);
        }

        invoker.getCode().appendStatement(isInstance ?
                                          new IfStatement(thisPtr.asExpression(), invokeExpression.asStatement()) :
                                          invokeExpression.asStatement());

        final Function getInvoker = handlerClass.createMemberFunction(group, "getInvoker", Visibility.PUBLIC);
        getInvoker.setReturnType(new TypeUsage(Inspector.callable));
        getInvoker.setConst(true);
        codeFile.addFunctionDefinition(getInvoker);
        final Expression
                channel2 =
                getInvoker.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                           "channel").asExpression();

        getInvoker.getCode().appendStatement(new ReturnStatement(invokerClass.callConstructor(channel2)));

    }

    private void addLocalVarsWriter() {
        final Function writer = handlerClass.createMemberFunction(group, "writeLocalVars", Visibility.PUBLIC);
        writer.setConst(true);

        final Expression
                channel =
                writer.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                       "channel").asExpression();
        final Expression
                frame =
                writer.createParameter(new TypeUsage(Architecture.stackFrameClass, TypeUsage.ConstReference),
                                       "frame").asExpression();

        codeFile.addFunctionDefinition(writer);

        if (isInstance) {
            final Function getter = new Function("getThis");
            getter.addTemplateSpecialisation(new TypeUsage(mainObjectTranslator.getMainClass()));
            final Expression thisVal = getter.asFunctionCall(frame, false);

            final Expression value = mainObjectTranslator.getPointerType().getType().callConstructor(thisVal);

            final StatementGroup action = new StatementGroup(Comment.createComment("Write this"));
            action.appendStatement(new BinaryExpression(channel, BinaryOperator.LEFT_SHIFT, value).asStatement());
            writer.getCode().appendStatement(action);
        }

        for (int i = 0; i < params.size(); ++i) {
            final Expression paramId = new Literal(i);
            final Expression
                    paramVal =
                    new ArrayAccess(new Function("getParameters").asFunctionCall(frame, false), paramId);

            final ParameterDefinition param = params.get(i);
            if (canRead(param.getType().getBasicType())) {
                final Function getter = new Function("getValue");
                getter.addTemplateSpecialisation(Types.getInstance().getType(param.getType()));
                final Expression value = getter.asFunctionCall(paramVal, false);

                final StatementGroup action = new StatementGroup(Comment.createComment("Write " + param.getName()));
                action.appendStatement(new BinaryExpression(channel, BinaryOperator.LEFT_SHIFT, value).asStatement());
                writer.getCode().appendStatement(action);
            }
        }

        final Function staticIntCast = Std.static_cast(new TypeUsage(Std.int32));
        final Variable loopVar = new Variable(new TypeUsage(Std.uint32), "i", new Literal(0));
        final Expression increment = new UnaryExpression(UnaryOperator.PREINCREMENT, loopVar.asExpression());
        final Expression
                lvSize =
                staticIntCast.asFunctionCall(new Function("size").asFunctionCall(new Function("getLocalVars").asFunctionCall(
                        frame,
                        false), false));

        final StatementGroup writeLocalVars = new StatementGroup(Comment.createComment("Write Local Variables"));
        writer.getCode().appendStatement(writeLocalVars);

        writeLocalVars.appendStatement(new BinaryExpression(channel, BinaryOperator.LEFT_SHIFT, lvSize).asStatement());

        final Expression endCond = new BinaryExpression(loopVar.asExpression(), BinaryOperator.LESS_THAN, lvSize);

        final CodeBlock forBlock = new CodeBlock();

        final ForStatement forLoop = new ForStatement(loopVar.asStatement(), endCond, increment, forBlock);

        final Expression
                lvVal =
                new ArrayAccess(new Function("getLocalVars").asFunctionCall(frame, false), loopVar.asExpression());
        final Expression lvId = new Function("getId").asFunctionCall(lvVal, false);

        forBlock.appendStatement(new BinaryExpression(channel, BinaryOperator.LEFT_SHIFT, lvId).asStatement());

        final List<CaseCondition> caseConditions = new ArrayList<>();

        for (int i = 0; i < localVars.size(); ++i) {
            final VariableDefinition localVar = localVars.get(i);
            final Function getter = new Function("getValue");
            getter.addTemplateSpecialisation(Types.getInstance().getType(localVar.getType()));
            final Expression value = getter.asFunctionCall(lvVal, false);

            final StatementGroup action = new StatementGroup(Comment.createComment("Write " + localVar.getName()));
            if (canWrite(localVar.getType().getBasicType())) {
                action.appendStatement(new BinaryExpression(channel, BinaryOperator.LEFT_SHIFT, value).asStatement());
            }
            action.appendStatement(new BreakStatement());

            caseConditions.add(new CaseCondition(new Literal(i), action));
        }

        if (caseConditions.size() > 0) {
            forBlock.appendStatement(new SwitchStatement(lvId, caseConditions));
            writeLocalVars.appendStatement(forLoop);
        }

    }

    private boolean canRead(final BasicType paramType) {
        return !(paramType.getBasicType().getActualType() == ActualType.EVENT ||
                 paramType.getBasicType().getActualType() == ActualType.DEVICE ||
                 paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
    }

    private boolean canWrite(final BasicType paramType) {
        return !(paramType.getBasicType().getActualType() == ActualType.EVENT ||
                 paramType.getBasicType().getActualType() == ActualType.DEVICE ||
                 paramType.getBasicType().getActualType() == ActualType.ANY_INSTANCE);
    }

    private final List<? extends VariableDefinition> localVars;

    private final List<? extends ParameterDefinition> params;

    private final CodeFile codeFile;

    private final Class handlerClass;
    private final Class invokerClass;

    private final DeclarationGroup group;

    private final Function serviceFunction;
    private final TypedefType serviceInterceptor;

    private final ObjectDeclaration object;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;

    private final boolean isInstance;

}
