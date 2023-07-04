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
package org.xtuml.masl.translate.tracelines;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.TerminatorTranslator;
import org.xtuml.masl.translate.main.code.CodeBlockTranslator;
import org.xtuml.masl.translate.main.code.CodeTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Alias("TraceLines")
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.<org.xtuml.masl.translate.DomainTranslator>singletonList(mainDomainTranslator);
    }

    @Override
    public void translate() {

        for (final ObjectDeclaration object : domain.getObjects()) {
            final ObjectTranslator objectTranslator = mainDomainTranslator.getObjectTranslator(object);

            for (final ObjectService service : object.getServices()) {
                final CodeTranslator translator = objectTranslator.getServiceTranslator(service).getCodeTranslator();

                final String type = (service.isInstance() ? "Instance" : "Object") + " Service ";

                final Variable
                        traceName =
                        new Variable(new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst),
                                     "traceModuleName",
                                     Literal.createStringLiteral(service.getQualifiedName()));
                traceName.setStatic(true);

                addEnteringLeaving(type, traceName, translator);
                addGetLines(traceName, translator);

            }

            for (final State state : object.getStates()) {

                final String type = "State Action";

                final Variable
                        traceName =
                        new Variable(new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst),
                                     "traceModuleName",
                                     Literal.createStringLiteral(state.getQualifiedName()));
                traceName.setStatic(true);

                final CodeTranslator translator = objectTranslator.getStateActionTranslator(state).getCodeTranslator();
                addEnteringLeaving(type, traceName, translator);
                addGetLines(traceName, translator);
            }

        }

        for (final DomainTerminator terminator : domain.getTerminators()) {
            final TerminatorTranslator termTranslator = mainDomainTranslator.getTerminatorTranslator(terminator);

            for (final DomainTerminatorService service : terminator.getServices()) {
                final CodeTranslator translator = termTranslator.getServiceTranslator(service).getCodeTranslator();

                final String type = "Terminator Service ";

                final Variable
                        traceName =
                        new Variable(new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst),
                                     "traceModuleName",
                                     Literal.createStringLiteral(service.getQualifiedName()));
                traceName.setStatic(true);

                addEnteringLeaving(type, traceName, translator);
                addGetLines(traceName, translator);

            }

        }

        for (final DomainService service : domain.getServices()) {
            final CodeTranslator translator = mainDomainTranslator.getServiceTranslator(service).getCodeTranslator();

            final String
                    type =
                    (service.isExternal() ? "External" : (service.isScenario() ? "Scenario" : "Domain Service"));

            final Variable
                    traceName =
                    new Variable(new TypeUsage(FundamentalType.CHAR, TypeUsage.ConstPointerToConst),
                                 "traceModuleName",
                                 Literal.createStringLiteral(service.getQualifiedName()));
            traceName.setStatic(true);

            addEnteringLeaving(type, traceName, translator);
            addGetLines(traceName, translator);

        }

    }

    void addEnteringLeaving(final String type, final Variable name, final CodeTranslator translator) {
        if (translator == null) {
            return;
        }

        translator.getPreamble().appendStatement(name.asStatement());
        translator.getPreamble().appendStatement(getTrace("Entering " + type + " ", name));
        translator.getPostamble().appendStatement(getTrace("Leaving " + type + " ", name));
    }

    void addGetLines(final Variable name, final CodeTranslator translator) {
        if (translator == null) {
            return;
        }

        if (translator instanceof CodeBlockTranslator cbTranslator) {
            cbTranslator.getPreamble().appendStatement(getTrace("Entering Block"));
            cbTranslator.getChildStatements().prependStatement(getTrace("Entered Block"));
            cbTranslator.getChildStatements().appendStatement(getTrace("Leaving Block"));
            cbTranslator.getPostamble().appendStatement(getTrace("Left Block"));
        } else {
            translator.getPreamble().appendStatement(getTrace("At ",
                                                              name,
                                                              " line " +
                                                              translator.getMaslStatement().getLineNumber()));
        }

        for (final CodeTranslator child : translator.getChildTranslators()) {
            addGetLines(name, child);
        }
    }

    Statement getTrace(final Expression message1, final Expression message2, final Expression message3) {
        Expression
                cout =
                new BinaryExpression(Architecture.console.asFunctionCall(), BinaryOperator.LEFT_SHIFT, message1);
        if (message2 != null) {
            cout = new BinaryExpression(cout, BinaryOperator.LEFT_SHIFT, message2);
        }
        if (message3 != null) {
            cout = new BinaryExpression(cout, BinaryOperator.LEFT_SHIFT, message3);
        }
        cout = new BinaryExpression(cout, BinaryOperator.LEFT_SHIFT, Std.endl);
        return new ExpressionStatement(cout);
    }

    Statement getTrace(final String message) {
        return getTrace(Literal.createStringLiteral(message), null, null);
    }

    Statement getTrace(final String message1, final Variable name) {
        return getTrace(Literal.createStringLiteral(message1), name.asExpression(), null);
    }

    Statement getTrace(final String message1, final Variable name, final String message2) {
        return getTrace(Literal.createStringLiteral(message1),
                        name.asExpression(),
                        Literal.createStringLiteral(message2));
    }

    private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

}
