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
import org.xtuml.masl.javagen.ast.def.TypeParameter;
import org.xtuml.masl.javagen.ast.types.TypeVariable;

public class TypeVariableImpl extends ReferenceTypeImpl implements TypeVariable {

    TypeVariableImpl(final ASTImpl ast, final TypeParameter parameter) {
        super(ast);
        this.parameter = parameter;
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public TypeParameter getTypeParameter() {
        return parameter;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitTypeVariable(this);
    }

    private final TypeParameter parameter;

    @Override
    public TypeVariableImpl deepCopy() {
        return new TypeVariableImpl(getAST(), parameter);
    }

}
