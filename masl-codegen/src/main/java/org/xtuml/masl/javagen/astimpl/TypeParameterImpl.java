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
import org.xtuml.masl.javagen.ast.types.ReferenceType;

import java.util.Collections;
import java.util.List;

public class TypeParameterImpl extends ASTNodeImpl implements TypeParameter {

    TypeParameterImpl(final ASTImpl ast, final String name) {
        super(ast);
        this.name = name;
    }

    TypeParameterImpl(final ASTImpl ast, final java.lang.reflect.TypeVariable<?> param, final Scope declaringScope) {
        this(ast, param.getName());
        for (final java.lang.reflect.Type bound : param.getBounds()) {
            if (bound != Object.class) {
                addExtendsBound((ReferenceTypeImpl) ast.createType(bound));
            }
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReferenceTypeImpl addExtendsBound(final ReferenceType extendsBound) {
        extendsBounds.add((ReferenceTypeImpl) extendsBound);
        return (ReferenceTypeImpl) extendsBound;
    }

    @Override
    public List<ReferenceTypeImpl> getExtendsBounds() {
        return Collections.unmodifiableList(extendsBounds);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitTypeParameter(this);
    }

    private String name;
    private final ChildNodeList<ReferenceTypeImpl> extendsBounds = new ChildNodeList<>(this);

    @Override
    public void setName(final String name) {
        this.name = name;
    }

}
