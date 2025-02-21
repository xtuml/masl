/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Callable;
import org.xtuml.masl.javagen.ast.def.Parameter;
import org.xtuml.masl.javagen.ast.types.Type;

public class ParameterImpl extends VariableImpl implements Parameter {

    public ParameterImpl(final ASTImpl ast, final Type paramType, final String name) {
        super(ast, paramType, name);
    }

    @Override
    public Callable getParentCallable() {
        return (Callable) getParentNode();
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitParameter(this);
    }

}
