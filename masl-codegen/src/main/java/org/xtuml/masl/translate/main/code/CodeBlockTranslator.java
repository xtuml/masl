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

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.code.ExceptionHandler;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.translate.main.*;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockTranslator extends CodeTranslator {

    protected CodeBlockTranslator(final org.xtuml.masl.metamodel.code.CodeBlock block,
                                  final Scope parentScope,
                                  final CodeTranslator parentTranslator) {
        super(block, parentScope, parentTranslator);

        final CodeBlock codeBlock = new CodeBlock();
        codeBlock.appendStatement(variableDefs);

        for (final VariableDefinition def : block.getVariables()) {
            final VariableDefinitionTranslator defTrans = new VariableDefinitionTranslator(def, getScope());

            variableTranslators.add(defTrans);

            getScope().addVariable(def, defTrans.getVariable().asExpression());

            variableDefs.appendStatement(defTrans.getFullCode());
        }

        for (final org.xtuml.masl.metamodel.code.Statement maslStatement : block.getStatements()) {
            childStatements.appendStatement(createChildTranslator(maslStatement).getFullCode());
        }

        final List<TryCatchBlock.CatchBlock> catchBlocks = new ArrayList<>();
        for (final ExceptionHandler handler : block.getExceptionHandlers()) {
            final HandlerTranslator translator = new HandlerTranslator(handler);
            handlerTranslators.add(translator);
            catchBlocks.add(translator.getCatchBlock());

        }

        if (catchBlocks.size() == 0) {
            codeBlock.appendStatement(childStatements);
        } else {
            final CodeBlock childBlock = new CodeBlock();
            childBlock.appendStatement(childStatements);
            codeBlock.appendStatement(new TryCatchBlock(childBlock, catchBlocks));
        }

        getCode().appendStatement(codeBlock);

    }

    public class HandlerTranslator {

        private HandlerTranslator(final ExceptionHandler handler) {
            this.handler = handler;
            codeBlock.appendStatement(preamble);

            final VariableDefinition maslMessageVar = handler.getMessageVarDef();
            if (maslMessageVar != null) {
                final TypeUsage type = Types.getInstance().getType(maslMessageVar.getType());
                final Variable
                        messageVar =
                        new Variable(type.getConstType(),
                                     Mangler.mangleName(maslMessageVar),
                                     new Literal("exception.what()"));
                getScope().addVariable(maslMessageVar, messageVar.asExpression());
                codeBlock.appendStatement(new VariableDefinitionStatement(messageVar));
            }

            for (final org.xtuml.masl.metamodel.code.Statement maslStatement : handler.getCode()) {
                codeBlock.appendStatement(createChildTranslator(maslStatement).getFullCode());
            }
            codeBlock.appendStatement(postamble);

            final ExceptionReference exception = handler.getException();
            final Class
                    exceptionClass =
                    exception == null ? Architecture.topException : ExceptionTranslator.getExceptionClass(exception);

            final Variable
                    exceptionVar =
                    new Variable(new TypeUsage(exceptionClass, TypeUsage.ConstReference), "exception");
            catchBlock = new TryCatchBlock.CatchBlock(exceptionVar, codeBlock);

        }

        public ExceptionHandler getHandler() {
            return handler;
        }

        private final ExceptionHandler handler;

        private final CodeBlock codeBlock = new CodeBlock();
        private final StatementGroup preamble = new StatementGroup();
        private final StatementGroup postamble = new StatementGroup();

        private final TryCatchBlock.CatchBlock catchBlock;

        public TryCatchBlock.CatchBlock getCatchBlock() {
            return catchBlock;
        }

        public StatementGroup getPreamble() {
            return preamble;
        }

        public StatementGroup getPostamble() {
            return postamble;
        }

    }

    public StatementGroup getChildStatements() {
        return childStatements;
    }

    public StatementGroup getVariableDefs() {
        return variableDefs;
    }

    public List<VariableDefinitionTranslator> getVariableTranslators() {
        return variableTranslators;
    }

    public List<HandlerTranslator> getHandlerTranslators() {
        return handlerTranslators;
    }

    private final StatementGroup variableDefs = new StatementGroup();

    private final StatementGroup childStatements = new StatementGroup();

    private final List<VariableDefinitionTranslator> variableTranslators = new ArrayList<>();

    private final List<HandlerTranslator> handlerTranslators = new ArrayList<>();
}
