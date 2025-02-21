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
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Modifier;
import org.xtuml.masl.javagen.ast.def.Visibility;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.EnumSet;

class FieldImpl extends TypeMemberImpl implements Field, ModifiersImpl.Filter {

    FieldImpl(final ASTImpl ast, final Type type, final String name) {
        super(ast);
        setName(name);
        setType(type);
        this.modifiers.set(new ModifiersImpl(ast, this));
    }

    FieldImpl(final ASTImpl ast, final Type type, final String name, final Expression initialValue) {
        super(ast);
        setName(name);
        setType(type);
        setInitialValue(initialValue);
        this.modifiers.set(new ModifiersImpl(ast, this));
    }

    FieldImpl(final ASTImpl ast) {
        super(ast);
        this.modifiers.set(new ModifiersImpl(ast, this));
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitField(this);
    }

    @Override
    public EnumSet<Modifier> getImplicitModifiers() {
        if (getDeclaringType() != null && getDeclaringType().isInterface()) {
            return EnumSet.of(Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
        } else {
            return EnumSet.noneOf(Modifier.class);
        }

    }

    TypeBodyImpl getParentTypeBody() {
        return (TypeBodyImpl) getParentNode();
    }

    TypeDeclarationImpl getDeclaringType() {
        if (getParentTypeBody() != null) {
            return getParentTypeBody().getParentTypeDeclaration();
        } else {
            return null;
        }
    }

    @Override
    public ModifiersImpl getModifiers() {
        return modifiers.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    public ExpressionImpl getInitialValue() {
        return initialValue.get();
    }

    @Override
    public ExpressionImpl setInitialValue(final Expression initialValue) {
        this.initialValue.set((ExpressionImpl) initialValue);
        return (ExpressionImpl) initialValue;
    }

    private String name;
    private final ChildNode<ModifiersImpl> modifiers = new ChildNode<>(this);
    private final ChildNode<TypeImpl> type = new ChildNode<>(this);
    private final ChildNode<ExpressionImpl> initialValue = new ChildNode<>(this);

    @Override
    public FieldAccessImpl asExpression() {
        return getAST().createFieldAccess(this);
    }

    @Override
    public Visibility getVisibility() {
        return getModifiers().isPublic() ?
               Visibility.PUBLIC :
               getModifiers().isProtected() ?
               Visibility.PROTECTED :
               getModifiers().isPrivate() ? Visibility.PRIVATE : Visibility.DEFAULT;
    }

    @Override
    public void setVisibility(final Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                getModifiers().setModifier(Modifier.PUBLIC);
                getModifiers().clearModifier(Modifier.PROTECTED);
                getModifiers().clearModifier(Modifier.PRIVATE);
                break;
            case PROTECTED:
                getModifiers().clearModifier(Modifier.PUBLIC);
                getModifiers().setModifier(Modifier.PROTECTED);
                getModifiers().clearModifier(Modifier.PRIVATE);
                break;
            case DEFAULT:
                getModifiers().clearModifier(Modifier.PUBLIC);
                getModifiers().clearModifier(Modifier.PROTECTED);
                getModifiers().clearModifier(Modifier.PRIVATE);
                break;
            case PRIVATE:
                getModifiers().clearModifier(Modifier.PUBLIC);
                getModifiers().clearModifier(Modifier.PROTECTED);
                getModifiers().setModifier(Modifier.PRIVATE);
                break;
        }
    }

    @Override
    public boolean isFinal() {
        return getModifiers().isFinal();
    }

    @Override
    public boolean isStatic() {
        return getModifiers().isStatic();
    }

    @Override
    public boolean isTransient() {
        return getModifiers().isTransient();
    }

    @Override
    public boolean isVolatile() {
        return getModifiers().isVolatile();
    }

    @Override
    public void setFinal() {
        getModifiers().setModifier(Modifier.FINAL);
    }

    @Override
    public void setStatic() {
        getModifiers().setModifier(Modifier.STATIC);
    }

    @Override
    public void setTransient() {
        getModifiers().setModifier(Modifier.TRANSIENT);
    }

    @Override
    public void setVolatile() {
        getModifiers().setModifier(Modifier.VOLATILE);
    }

    @Override
    public String toString() {
        return getEnclosingTypeDeclaration() + "." + getName();
    }

}
