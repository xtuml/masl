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
import org.xtuml.masl.javagen.ast.def.Modifier;
import org.xtuml.masl.javagen.ast.def.Modifiers;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

class ModifiersImpl extends ASTNodeImpl implements Modifiers {

    interface Filter {

        EnumSet<Modifier> getImplicitModifiers();
    }

    ModifiersImpl(final ASTImpl ast, final Filter filter) {
        super(ast);
        this.filter = filter;
    }

    private final Filter filter;

    void setModifiers(final int jvmModifiers) {
        if (java.lang.reflect.Modifier.isPublic(jvmModifiers)) {
            setModifier(Modifier.PUBLIC);
        }

        if (java.lang.reflect.Modifier.isPrivate(jvmModifiers)) {
            setModifier(Modifier.PRIVATE);
        }

        if (java.lang.reflect.Modifier.isProtected(jvmModifiers)) {
            setModifier(Modifier.PROTECTED);
        }

        if (java.lang.reflect.Modifier.isAbstract(jvmModifiers)) {
            setModifier(Modifier.ABSTRACT);
        }

        if (java.lang.reflect.Modifier.isFinal(jvmModifiers)) {
            setModifier(Modifier.FINAL);
        }

        if (java.lang.reflect.Modifier.isStrict(jvmModifiers)) {
            setModifier(Modifier.STRICTFP);
        }

        if (java.lang.reflect.Modifier.isStatic(jvmModifiers)) {
            setModifier(Modifier.STATIC);
        }

        if (java.lang.reflect.Modifier.isTransient(jvmModifiers)) {
            setModifier(Modifier.TRANSIENT);
        }

        if (java.lang.reflect.Modifier.isVolatile(jvmModifiers)) {
            setModifier(Modifier.VOLATILE);
        }

        if (java.lang.reflect.Modifier.isSynchronized(jvmModifiers)) {
            setModifier(Modifier.SYNCHRONIZED);
        }

    }

    void setModifier(final Modifier modifier) {
        modifiers.add(modifier);
    }

    void clearModifier(final Modifier modifier) {
        modifiers.remove(modifier);
    }

    @Override
    public Set<Modifier> getModifiers() {
        final EnumSet<Modifier> result = modifiers.clone();
        result.removeAll(filter.getImplicitModifiers());
        return Collections.unmodifiableSet(result);
    }

    @Override
    public boolean isAbstract() {
        return modifiers.contains(Modifier.ABSTRACT);
    }

    @Override
    public boolean isFinal() {
        return modifiers.contains(Modifier.FINAL);
    }

    @Override
    public boolean isPrivate() {
        return modifiers.contains(Modifier.PRIVATE);
    }

    @Override
    public boolean isProtected() {
        return modifiers.contains(Modifier.PROTECTED);
    }

    @Override
    public boolean isPublic() {
        return modifiers.contains(Modifier.PUBLIC);
    }

    @Override
    public boolean isStatic() {
        return modifiers.contains(Modifier.STATIC);
    }

    @Override
    public boolean isStrictFp() {
        return modifiers.contains(Modifier.STRICTFP);
    }

    @Override
    public boolean isTransient() {
        return modifiers.contains(Modifier.TRANSIENT);
    }

    @Override
    public boolean isVolatile() {
        return modifiers.contains(Modifier.VOLATILE);
    }

    @Override
    public boolean isNative() {
        return modifiers.contains(Modifier.NATIVE);
    }

    @Override
    public boolean isSynchronized() {
        return modifiers.contains(Modifier.SYNCHRONIZED);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitModifiers(this);
    }

    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

}
