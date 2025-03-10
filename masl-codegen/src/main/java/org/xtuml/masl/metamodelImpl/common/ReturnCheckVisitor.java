/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.code.*;

class ReturnCheckVisitor extends ASTNodeVisitor {

    private boolean hasReturn = false;

    @Override
    public void visitCaseStatement(final CaseStatement statement) {
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
    }

    @Override
    public void visitCodeBlock(final CodeBlock statement) {
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
                handlerCheck.visit(child);
            }
            allHandlersReturn = allHandlersReturn && handlerCheck.hasReturn;
        }

        hasReturn &= allHandlersReturn;
    }

    @Override
    public void visitIfStatement(final IfStatement statement) {
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
    }

    @Override
    public void visitReturnStatement(final ReturnStatement statement) {
        hasReturn = true;
    }

    @Override
    public void visitRaiseStatement(final RaiseStatement statement) {
        // An exception is as good as a return, as all exception handlers are
        // checked for returns, and if the exeception is propagated all the way
        // out, the return is irrelevant.
        hasReturn = true;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

}
