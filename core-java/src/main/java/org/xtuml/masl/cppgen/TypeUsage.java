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
package org.xtuml.masl.cppgen;

import java.util.Set;

public final class TypeUsage {

    private static class Modifiers {

        private Modifiers(final boolean isConst, final boolean[] pointerConsts, final boolean isReference) {
            this.isConst = isConst;
            this.isReference = isReference;
            this.pointerConsts = pointerConsts;
        }

        private final boolean isConst;
        private final boolean isReference;

        private final boolean[] pointerConsts;
    }

    final public static Modifiers Const = new Modifiers(true, null, false);
    final public static Modifiers ConstPointerToConst = new Modifiers(true, new boolean[]{true}, false);
    final public static Modifiers ConstReference = new Modifiers(true, null, true);
    final public static Modifiers Pointer = new Modifiers(false, new boolean[]{false}, false);
    final public static Modifiers PointerToConst = new Modifiers(false, new boolean[]{true}, false);
    final public static Modifiers Reference = new Modifiers(false, null, true);

    final public static TypeUsage VOID = new TypeUsage(FundamentalType.VOID);

    public TypeUsage(final boolean isConst, final Type type, final boolean[] pointerConsts, final boolean isReference) {
        this.type = type;
        this.isConst = isConst;
        this.isReference = isReference;
        this.pointerConsts = pointerConsts;
        this.templateRefOnly = false;
    }

    public TypeUsage(final Type type) {
        this(false, type, null, false);
    }

    public TypeUsage(final Type type, final Modifiers modifiers) {
        this.type = type;
        this.isConst = modifiers.isConst;
        this.isReference = modifiers.isReference;
        this.pointerConsts = modifiers.pointerConsts;
        this.templateRefOnly = false;
    }

    private TypeUsage(final TypeUsage usage, final boolean templateRefOnly) {
        this.type = usage.type;
        this.isConst = usage.isConst;
        this.isReference = usage.isReference;
        this.pointerConsts = usage.pointerConsts;
        this.templateRefOnly = templateRefOnly;
    }

    public Expression getDefaultValue() {
        if (pointerConsts != null) {
            return Literal.ZERO;
        } else if (isReference) {
            throw new IllegalStateException("No Default Value for Reference");
        } else {
            return type.callConstructor();
        }

    }

    public Set<Declaration> getDirectUsageForwardDeclarations() {
        if ((isReference || pointerConsts != null || templateRefOnly)) {
            return type.getIndirectUsageForwardDeclarations();
        } else {
            return type.getDirectUsageForwardDeclarations();
        }

    }

    public Set<CodeFile> getDirectUsageIncludes() {
        if ((isReference || pointerConsts != null || templateRefOnly)) {
            return type.getIndirectUsageIncludes();
        } else {
            return type.getDirectUsageIncludes();
        }
    }

    public Set<Declaration> getIndirectUsageForwardDeclarations() {
        return type.getIndirectUsageForwardDeclarations();
    }

    public Set<CodeFile> getIndirectUsageIncludes() {
        return type.getIndirectUsageIncludes();
    }

    public Set<CodeFile> getNoRefDirectUsageIncludes() {
        if (pointerConsts != null) {
            return type.getIndirectUsageIncludes();
        } else {
            return type.getNoRefDirectUsageIncludes();
        }
    }

    public TypeUsage getOptimalParameterType() {
        return getOptimalParameterType(false);
    }

    public TypeUsage getOptimalParameterType(final boolean writeable) {
        if (writeable) {
            return new TypeUsage(false, type, pointerConsts, true);
        } else if (preferPassByReference()) {
            return new TypeUsage(true, type, pointerConsts, true);
        } else {
            return this;
        }
    }

    public TypeUsage getReferenceType() {
        return new TypeUsage(isConst, type, pointerConsts, true);
    }

    public TypeUsage getConstReferenceType() {
        return new TypeUsage(true, type, pointerConsts, true);
    }

    public TypeUsage getNonConstReferenceType() {
        return new TypeUsage(false, type, pointerConsts, isReference && !isConst);
    }

    public TypeUsage getModifiedType(final boolean constant, final boolean reference) {
        return new TypeUsage(constant, type, pointerConsts, reference);
    }

    public TypeUsage getConstType() {
        return new TypeUsage(true, type, pointerConsts, isReference);
    }

    public String getQualifiedName() {
        return getQualifiedName(null);
    }

    public String getQualifiedName(final Namespace currentNamespace) {
        if (pointerConsts != null && pointerConsts.length > 0) {
            final StringBuilder buf = new StringBuilder();
            buf.append((pointerConsts[0] ? "const " : "") + type.getQualifiedName(currentNamespace) + "*");

            for (int i = 1; i < pointerConsts.length; ++i) {
                buf.append(" " + (pointerConsts[i] ? "const " : "") + "*");
            }
            buf.append((isConst ? " const" : "") + (isReference ? "&" : ""));
            return buf.toString();
        } else {
            return (isConst ? "const " : "") + type.getQualifiedName(currentNamespace) + (isReference ? "&" : "");
        }
    }

    public TypeUsage getTemplateRefOnly() {
        return new TypeUsage(this, true);
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    boolean isTemplateType() {
        if (pointerConsts != null && pointerConsts.length > 0) {
            return false;
        } else {
            return getType().isTemplateType();
        }
    }

    boolean preferPassByReference() {
        return !isReference && type.preferPassByReference() && pointerConsts == null;

    }

    final private boolean templateRefOnly;

    final private boolean isConst;

    final private boolean isReference;

    final private boolean[] pointerConsts;

    final private Type type;

}
