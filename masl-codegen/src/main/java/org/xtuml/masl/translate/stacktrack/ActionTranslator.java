/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.stacktrack;

import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.DomainServiceTranslator;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.ProjectTerminatorServiceTranslator;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;
import org.xtuml.masl.translate.main.code.CodeBlockTranslator;
import org.xtuml.masl.translate.main.code.CodeTranslator;
import org.xtuml.masl.translate.main.code.ForTranslator;
import org.xtuml.masl.translate.main.code.VariableDefinitionTranslator;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;
import org.xtuml.masl.translate.main.object.StateActionTranslator;

import java.util.List;

public class ActionTranslator {

    ActionTranslator(final DomainServiceTranslator translator) {
        codeTranslator = translator.getCodeTranslator();
        parameters = translator.getParameters();
        enteringAction =
                Stack.getEnteringDomainService(translator.getDomainTranslator().getDomainId(),
                                               translator.getServiceId());
        mainObjectTranslator = null;
        isInstance = false;
    }

    ActionTranslator(final ObjectServiceTranslator translator) {
        codeTranslator = translator.getCodeTranslator();
        parameters = translator.getParameters();
        enteringAction =
                Stack.getEnteringObjectService(translator.getObjectTranslator().getDomainTranslator().getDomainId(),
                                               translator.getObjectTranslator().getObjectId(),
                                               translator.getServiceId());
        mainObjectTranslator = translator.getObjectTranslator();
        isInstance = translator.getService().isInstance();
    }

    ActionTranslator(final TerminatorServiceTranslator translator) {
        codeTranslator = translator.getCodeTranslator();
        parameters = translator.getParameters();
        enteringAction =
                Stack.getEnteringTerminatorService(translator.getTerminatorTranslator().getDomainTranslator().getDomainId(),
                                                   translator.getTerminatorTranslator().getTerminatorId(),
                                                   translator.getServiceId());
        mainObjectTranslator = null;
        isInstance = false;
    }

    ActionTranslator(final StateActionTranslator translator) {
        codeTranslator = translator.getCodeTranslator();
        parameters = translator.getParameters();
        enteringAction =
                Stack.getEnteringState(translator.getObjectTranslator().getDomainTranslator().getDomainId(),
                                       translator.getObjectTranslator().getObjectId(),
                                       translator.getStateId());
        mainObjectTranslator = translator.getObjectTranslator();
        isInstance =
                translator.getState().getType() == State.Type.NORMAL ||
                translator.getState().getType() == State.Type.TERMINAL;
    }

    ActionTranslator(final ProjectTerminatorServiceTranslator translator) {
        codeTranslator = translator.getCodeTranslator();
        parameters = translator.getParameters();

        final TerminatorServiceTranslator
                domainTSTranslator =
                TerminatorServiceTranslator.getInstance(translator.getService().getDomainTerminatorService());
        enteringAction =
                Stack.getEnteringTerminatorService(domainTSTranslator.getTerminatorTranslator().getDomainTranslator().getDomainId(),
                                                   domainTSTranslator.getTerminatorTranslator().getTerminatorId(),
                                                   domainTSTranslator.getServiceId());
        mainObjectTranslator = null;
        isInstance = false;
    }

    void addAtLines(final CodeTranslator code) {
        if (code == null) {
            return;
        }

        if (code instanceof CodeBlockTranslator codeBlock) {

            for (final VariableDefinitionTranslator varDef : codeBlock.getVariableTranslators()) {
                varDef.getPostamble().appendStatement(Stack.defineVariable(new Literal(varId++), varDef.getVariable()));
            }

            for (final CodeBlockTranslator.HandlerTranslator handler : codeBlock.getHandlerTranslators()) {
                handler.getPreamble().appendStatement(Stack.getEnteredCatch(handler.getHandler().getLineNumber()));
            }
        } else if (code instanceof ForTranslator forStatement) {
            forStatement.getLoopVarDef().appendStatement(Stack.defineVariable(new Literal(varId++),
                                                                              forStatement.getLoopVariable()));
        }

        final int lineNo = code.getMaslStatement().getLineNumber();

        if (lineNo > 0) {
            code.getPreamble().appendStatement(Stack.getExecutingStatement(lineNo));
        }

        for (final CodeTranslator child : code.getChildTranslators()) {
            addAtLines(child);
        }

    }

    void translate() {
        if (codeTranslator == null) {
            return;
        }

        codeTranslator.getPreamble().appendStatement(enteringAction);

        if (isInstance) {
            codeTranslator.getPreamble().appendStatement(Stack.defineThis(mainObjectTranslator.getMainClass().getThis()));
        }

        for (final ParameterTranslator parameter : parameters) {
            codeTranslator.getPreamble().appendStatement(Stack.defineParameter(parameter.getVariable()));
        }

        codeTranslator.getPreamble().appendStatement(Stack.getEnteredAction());

        addAtLines(codeTranslator);
    }

    private int varId = 0;

    private final Statement enteringAction;

    private final List<ParameterTranslator> parameters;

    private final CodeTranslator codeTranslator;

    private final boolean isInstance;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;
}
