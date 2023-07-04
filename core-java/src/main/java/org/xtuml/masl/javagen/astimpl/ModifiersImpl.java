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
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitModifiers(this, p);
    }

    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

}
