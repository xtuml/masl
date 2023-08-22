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
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.FieldAccess;

public class FieldAccessImpl extends ExpressionImpl implements FieldAccess {

    FieldAccessImpl(final ASTImpl ast, final Expression instance, final Field field) {
        super(ast);
        setInstance(instance);
        setField(field);
    }

    FieldAccessImpl(final ASTImpl ast, final Field field) {
        super(ast);
        setField(field);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitFieldAccess(this);
    }

    @Override
    public void forceQualifier() {
        if (qualifier.get() == null && instance.get() == null) {
            if (field.getModifiers().isStatic()) {
                setQualifier(new TypeQualifierImpl(getAST(), field.getDeclaringType()));
            } else {
                setInstance(new ThisImpl(getAST(), field.getParentTypeBody()));
            }
        }
    }

    @Override
    public FieldImpl getField() {
        return field;
    }

    @Override
    public ExpressionImpl getInstance() {
        if (getEnclosingScope().requiresQualifier(this)) {
            forceQualifier();
        }
        return instance.get();
    }

    @Override
    public TypeQualifierImpl getQualifier() {
        if (getEnclosingScope().requiresQualifier(this)) {
            forceQualifier();
        }

        return qualifier.get();
    }

    @Override
    public FieldImpl setField(final Field field) {
        this.field = (FieldImpl) field;
        return (FieldImpl) field;
    }

    @Override
    public ExpressionImpl setInstance(Expression instance) {
        if (((ExpressionImpl) instance).getPrecedence() < getPrecedence()) {
            instance = getAST().createParenthesizedExpression(instance);
        }
        this.instance.set((ExpressionImpl) instance);
        return (ExpressionImpl) instance;
    }

    @Override
    protected int getPrecedence() {
        return 15;
    }

    private void setQualifier(final TypeQualifierImpl qualifier) {
        this.qualifier.set(qualifier);
    }

    private FieldImpl field;

    private final ChildNode<ExpressionImpl> instance = new ChildNode<>(this);

    private final ChildNode<TypeQualifierImpl> qualifier = new ChildNode<>(this);

}
