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
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.DoNothingASTNodeVisitor;
import org.xtuml.masl.metamodel.code.*;

class ReturnCheckVisitor extends DoNothingASTNodeVisitor<Void, Void> {

    private boolean hasReturn = false;

    @Override
    public Void visitCaseStatement(final CaseStatement statement, final Void p) throws Exception {
        // Need to check that all alternatives return a value, and that at least
        // one of the alternatives will be used (in other words, there is an
        // 'others' clause.
        boolean allAltsReturn = true;
        boolean hasOther = false;
        for (final CaseStatement.Alternative alt : statement.getAlternatives()) {
            if (alt.getConditions() == null) {
                hasOther = true;
            }
            final ReturnCheckVisitor caseCheck = new ReturnCheckVisitor();
            for (final Statement child : alt.getStatements()) {
                caseCheck.visit(child);
            }
            allAltsReturn = allAltsReturn && caseCheck.hasReturn;
        }

        hasReturn = allAltsReturn && hasOther;
        return null;
    }

    @Override
    public Void visitCodeBlock(final CodeBlock statement, final Void p) throws Exception {
        // Check that at least one of the statements is a return
        for (final Statement child : statement.getStatements()) {
            visit(child);
            if (hasReturn) {
                break;
            }
        }

        // Check that all exception handlers return a value
        boolean allHandlersReturn = true;
        for (final ExceptionHandler handler : statement.getExceptionHandlers()) {
            final ReturnCheckVisitor handlerCheck = new ReturnCheckVisitor();
            for (final Statement child : handler.getCode()) {
                handlerCheck.visit(child, p);
            }
            allHandlersReturn = allHandlersReturn && handlerCheck.hasReturn;
        }

        hasReturn &= allHandlersReturn;
        return null;
    }

    @Override
    public Void visitIfStatement(final IfStatement statement, final Void p) throws Exception {
        boolean allBranchesReturn = true;
        boolean hasElse = false;

        for (final IfStatement.Branch branch : statement.getBranches()) {
            if (branch.getCondition() == null) {
                hasElse = true;
            }
            final ReturnCheckVisitor branchCheck = new ReturnCheckVisitor();
            for (final Statement child : branch.getStatements()) {
                branchCheck.visit(child);
            }
            allBranchesReturn = allBranchesReturn && branchCheck.hasReturn;
        }

        hasReturn = allBranchesReturn && hasElse;
        return null;
    }

    @Override
    public Void visitReturnStatement(final ReturnStatement statement, final Void p) throws Exception {
        hasReturn = true;
        return null;
    }

    @Override
    public Void visitRaiseStatement(final RaiseStatement statement, final Void p) throws Exception {
        // An exception is as good as a return, as all exception handlers are
        // checked for returns, and if the exeception is propagated all the way
        // out, the return is irrelevant.
        hasReturn = true;
        return null;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

}
