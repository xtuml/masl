/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.DomainServiceInvocation;
import org.xtuml.masl.metamodel.code.InstanceServiceInvocation;
import org.xtuml.masl.metamodel.code.ObjectServiceInvocation;
import org.xtuml.masl.metamodel.code.TerminatorServiceInvocation;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.translate.main.ArgumentTranslator;
import org.xtuml.masl.translate.main.DomainServiceTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;

/**
 * When calling domain based services or terminator services the actual call
 * undertaken is not statically bound at compile time (with a direct call to the
 * required function). Instead the function call sites make a call to the
 * services corresponding Interceptor class. This Interceptor class is bound at
 * runtime through the use of static registration mechanisms. The actual method
 * invoked at the call site will therefore depend on what libraries have been
 * included in the build and what method(s) they register.
 */
public class ServiceInvocationTranslator extends CodeTranslator {

    protected ServiceInvocationTranslator(final DomainServiceInvocation invocation,
                                          final Scope parentScope,
                                          final CodeTranslator parentTranslator) {
        super(invocation, parentScope, parentTranslator);
        final DomainServiceTranslator translator = DomainServiceTranslator.getInstance(invocation.getService());

        final ArgumentTranslator
                args =
                new ArgumentTranslator(invocation.getService().getParameters(), invocation.getArguments(), getScope());

        // The service might be a call to another domain, so get the required
        // interceptor type. Intercepter classes are not produced for externals
        // or senerio files as these methods should not be direclty invoked
        // by any MASL service invocation statements so don't need to check for
        // null.
        Expression fnCall;
        if (invocation.getPragmas().getValue(PragmaList.SCOPE).equals("local")) {
            fnCall = translator.getLocalInvocation(args.getArguments());
        } else if (invocation.getPragmas().getValue(PragmaList.SCOPE).equals("remote")) {
            fnCall = translator.getRemoteInvocation(args.getArguments());
        } else {
            fnCall = translator.getInvocation(args.getArguments());
        }

        if (args.getTempVariableDefinitions().size() > 0) {
            final CodeBlock codeBlock = new CodeBlock();
            getCode().appendStatement(codeBlock);
            codeBlock.appendStatement(args.getTempVariableDefinitions());
            codeBlock.appendStatement(fnCall.asStatement());
            codeBlock.appendStatement(args.getOutParameterAssignments());
        } else {
            getCode().appendStatement(fnCall.asStatement());
        }
    }

    protected ServiceInvocationTranslator(final TerminatorServiceInvocation invocation,
                                          final Scope parentScope,
                                          final CodeTranslator parentTranslator) {
        super(invocation, parentScope, parentTranslator);
        final TerminatorServiceTranslator translator = TerminatorServiceTranslator.getInstance(invocation.getService());

        final Function function = translator.getFunction();
        final ArgumentTranslator
                args =
                new ArgumentTranslator(invocation.getService().getParameters(), invocation.getArguments(), getScope());

        // The service might be a call to another domain, so get the required
        // interceptor type.
        if (args.getTempVariableDefinitions().size() > 0) {
            final CodeBlock codeBlock = new CodeBlock();
            getCode().appendStatement(codeBlock);
            codeBlock.appendStatement(args.getTempVariableDefinitions());
            codeBlock.appendStatement(new ExpressionStatement(function.asFunctionCall(args.getArguments())));
            codeBlock.appendStatement(args.getOutParameterAssignments());
        } else {
            getCode().appendStatement(new ExpressionStatement(function.asFunctionCall(args.getArguments())));
        }
    }

    protected ServiceInvocationTranslator(final InstanceServiceInvocation invocation,
                                          final Scope parentScope,
                                          final CodeTranslator parentTranslator) {
        super(invocation, parentScope, parentTranslator);

        final ObjectService service = invocation.getService();
        final Function function = ObjectServiceTranslator.getInstance(service).getFunction();
        final ArgumentTranslator
                args =
                new ArgumentTranslator(invocation.getService().getParameters(), invocation.getArguments(), getScope());
        final Expression
                instance =
                ExpressionTranslator.createTranslator(invocation.getInstance(), getScope()).getReadExpression();

        // A static object method can be called through an instance of the
        // associated
        // object type. Therefore detect this situation and change the instance
        // function
        // call for a static function call.
        ExpressionStatement
                funcCallExpr =
                new ExpressionStatement(function.asFunctionCall(instance, true, args.getArguments()));
        if (!service.isInstance()) {
            funcCallExpr = new ExpressionStatement(function.asFunctionCall(args.getArguments()));
        }

        if (args.getTempVariableDefinitions().size() > 0) {
            final CodeBlock codeBlock = new CodeBlock();
            getCode().appendStatement(codeBlock);

            codeBlock.appendStatement(args.getTempVariableDefinitions());
            codeBlock.appendStatement(funcCallExpr);
            codeBlock.appendStatement(args.getOutParameterAssignments());
        } else {
            getCode().appendStatement(funcCallExpr);
        }
    }

    protected ServiceInvocationTranslator(final ObjectServiceInvocation invocation,
                                          final Scope parentScope,
                                          final CodeTranslator parentTranslator) {
        super(invocation, parentScope, parentTranslator);

        final ObjectService service = invocation.getService();
        final Function function = ObjectServiceTranslator.getInstance(service).getFunction();
        final ArgumentTranslator
                args =
                new ArgumentTranslator(invocation.getService().getParameters(), invocation.getArguments(), getScope());

        if (args.getTempVariableDefinitions().size() > 0) {
            final CodeBlock codeBlock = new CodeBlock();
            getCode().appendStatement(codeBlock);

            codeBlock.appendStatement(args.getTempVariableDefinitions());
            codeBlock.appendStatement(new ExpressionStatement(function.asFunctionCall(args.getArguments())));
            codeBlock.appendStatement(args.getOutParameterAssignments());
        } else {
            getCode().appendStatement(new ExpressionStatement(function.asFunctionCall(args.getArguments())));
        }
    }

}
