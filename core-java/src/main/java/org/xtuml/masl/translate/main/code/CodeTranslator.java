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
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Comment;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.metamodel.code.*;
import org.xtuml.masl.translate.main.Scope;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeTranslator {

    public static CodeTranslator createTranslator(final org.xtuml.masl.metamodel.code.Statement statement,
                                                  final Scope parentScope) {
        return createTranslator(statement, parentScope, null);
    }

    private static CodeTranslator createTranslator(final org.xtuml.masl.metamodel.code.Statement statement,
                                                   final Scope parentScope,
                                                   final CodeTranslator parentTranslator) {
        if (statement instanceof org.xtuml.masl.metamodel.code.CodeBlock) {
            return new CodeBlockTranslator((org.xtuml.masl.metamodel.code.CodeBlock) statement,
                                           parentScope,
                                           parentTranslator);
        } else if (statement instanceof AssignmentStatement) {
            return new AssignmentTranslator((AssignmentStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof DeleteStatement) {
            return new DeleteTranslator((DeleteStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof EraseStatement) {
            return new EraseTranslator((EraseStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof org.xtuml.masl.metamodel.code.IfStatement) {
            return new IfTranslator((org.xtuml.masl.metamodel.code.IfStatement) statement,
                                    parentScope,
                                    parentTranslator);
        } else if (statement instanceof org.xtuml.masl.metamodel.code.WhileStatement) {
            return new WhileTranslator((org.xtuml.masl.metamodel.code.WhileStatement) statement,
                                       parentScope,
                                       parentTranslator);
        } else if (statement instanceof org.xtuml.masl.metamodel.code.ForStatement) {
            return new ForTranslator((org.xtuml.masl.metamodel.code.ForStatement) statement,
                                     parentScope,
                                     parentTranslator);
        } else if (statement instanceof LinkUnlinkStatement) {
            return new LinkUnlinkTranslator((LinkUnlinkStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof DomainServiceInvocation) {
            return new ServiceInvocationTranslator((DomainServiceInvocation) statement, parentScope, parentTranslator);
        } else if (statement instanceof TerminatorServiceInvocation) {
            return new ServiceInvocationTranslator((TerminatorServiceInvocation) statement,
                                                   parentScope,
                                                   parentTranslator);
        } else if (statement instanceof ObjectServiceInvocation) {
            return new ServiceInvocationTranslator((ObjectServiceInvocation) statement, parentScope, parentTranslator);
        } else if (statement instanceof InstanceServiceInvocation) {
            return new ServiceInvocationTranslator((InstanceServiceInvocation) statement,
                                                   parentScope,
                                                   parentTranslator);
        } else if (statement instanceof IOStreamStatement) {
            return new IOStreamTranslator((IOStreamStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof GenerateStatement) {
            return new GenerateTranslator((GenerateStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof ReturnStatement) {
            return new ReturnTranslator((ReturnStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof DelayStatement) {
            return new DelayTranslator((DelayStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof ExitStatement) {
            return new ExitTranslator((ExitStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof CaseStatement) {
            return new CaseTranslator((CaseStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof PragmaStatement) {
            return new PragmaTranslator((PragmaStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof RaiseStatement) {
            return new RaiseTranslator((RaiseStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof ScheduleStatement) {
            return new ScheduleStatementTranslator((ScheduleStatement) statement, parentScope, parentTranslator);
        } else if (statement instanceof CancelTimerStatement) {
            return new CancelTimerStatementTranslator((CancelTimerStatement) statement, parentScope, parentTranslator);
        }

        throw new IllegalArgumentException("Unrecognised Statement '" + statement + "'");
    }

    protected CodeTranslator(final org.xtuml.masl.metamodel.code.Statement maslStatement,
                             final Scope parentScope,
                             final CodeTranslator parentTranslator) {
        this.maslStatement = maslStatement;
        this.parentTranslator = parentTranslator;
        scope = new Scope(parentScope);
        fullCode = new CodeBlock(Comment.createComment(maslStatement.toAbbreviatedString(), false));

        fullCode.appendStatement(preamble);
        fullCode.appendStatement(code);
        fullCode.appendStatement(postamble);

    }

    public List<CodeTranslator> getChildTranslators() {
        return childTranslators;
    }

    public CodeBlock getFullCode() {
        return fullCode;
    }

    public org.xtuml.masl.metamodel.code.Statement getMaslStatement() {
        return maslStatement;
    }

    public StatementGroup getPostamble() {
        return postamble;
    }

    public StatementGroup getPreamble() {
        return preamble;
    }

    CodeTranslator createChildTranslator(final org.xtuml.masl.metamodel.code.Statement statement) {
        final CodeTranslator child = createTranslator(statement, scope, this);
        childTranslators.add(child);
        return child;
    }

    CodeTranslator getParentTranslator() {
        return parentTranslator;
    }

    private final CodeTranslator parentTranslator;

    public StatementGroup getCode() {
        return code;
    }

    protected Scope getScope() {
        return scope;
    }

    private final Scope scope;

    private final List<CodeTranslator> childTranslators = new ArrayList<CodeTranslator>();

    private final StatementGroup code = new StatementGroup();
    private final CodeBlock fullCode;

    private final org.xtuml.masl.metamodel.code.Statement maslStatement;
    private final StatementGroup postamble = new StatementGroup();
    private final StatementGroup preamble = new StatementGroup();
}
