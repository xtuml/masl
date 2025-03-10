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
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.MethodInvocation;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.*;

class MethodImpl extends TypeMemberImpl implements Method, Constructor, ModifiersImpl.Filter, Scoped {

    private final class MDScope extends Scope {

        MDScope() {
            super(MethodImpl.this);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final FieldAccessImpl fieldAccess,
                                            final boolean visible,
                                            boolean shadowed) {
            for (final ParameterImpl parameter : parameters) {
                if (parameter.getName().equals(fieldAccess.getField().getName())) {
                    shadowed = true;
                    break;
                }
            }
            return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final EnumConstantAccessImpl enumAccess,
                                            final boolean visible,
                                            boolean shadowed) {
            for (final ParameterImpl parameter : parameters) {
                if (parameter.getName().equals(enumAccess.getConstant().getName())) {
                    shadowed = true;
                    break;
                }
            }
            return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
        }
    }

    MethodImpl(final ASTImpl ast, final Parameter... params) {
        super(ast);
        // Constructor Constructor
        this.scope = new MDScope();
        this.name = null;
        this.isVarArgs = false;
        this.modifiers.set(new ModifiersImpl(ast, this));
        for (final Parameter param : params) {
            addParameter(param);
        }
    }

    MethodImpl(final ASTImpl ast, final String name, final Parameter... params) {
        super(ast);
        this.scope = new MDScope();
        this.name = name;
        this.isVarArgs = false;
        this.modifiers.set(new ModifiersImpl(ast, this));

        for (final Parameter param : params) {
            addParameter(param);
        }
    }

    MethodImpl(final ASTImpl ast, final String name, final Type returnType, final Parameter... params) {
        super(ast);
        this.scope = new MDScope();
        this.name = name;
        this.isVarArgs = false;
        this.modifiers.set(new ModifiersImpl(ast, this));
        this.setReturnType(returnType);

        for (final Parameter param : params) {
            addParameter(param);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        if (getName() == null) {
            v.visitConstructor(this);
        } else {
            v.visitMethod(this);
        }
    }

    @Override
    public ParameterImpl addParameter(final Parameter parameter) {
        parameters.add((ParameterImpl) parameter);
        return (ParameterImpl) parameter;
    }

    @Override
    public ParameterImpl addParameter(final Type type, final String name) {
        return addParameter(getAST().createParameter(type, name));
    }

    @Override
    public TypeImpl addThrownException(final Type exceptionType) {
        thrown.add((TypeImpl) exceptionType);
        return (TypeImpl) exceptionType;
    }

    @Override
    public TypeParameterImpl addTypeParameter(final String name) {
        return addTypeParameter(getAST().createTypeParameter(name));
    }

    @Override
    public TypeParameterImpl addTypeParameter(final TypeParameter parameter) {
        typeParameters.add((TypeParameterImpl) parameter);
        return (TypeParameterImpl) parameter;
    }

    @Override
    public CodeBlockImpl getCodeBlock() {
        return code.get();
    }

    @Override
    public EnumSet<Modifier> getImplicitModifiers() {
        if (getDeclaringType() != null && getDeclaringType().isInterface()) {
            return EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT);
        } else {
            return EnumSet.noneOf(Modifier.class);
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
    public List<ParameterImpl> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    @Override
    public TypeImpl getReturnType() {
        return returnType.get();
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public List<TypeImpl> getThrownExceptions() {
        return Collections.unmodifiableList(thrown);
    }

    @Override
    public List<TypeParameterImpl> getTypeParameters() {
        return Collections.unmodifiableList(typeParameters);
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
    public boolean isAbstract() {
        return getModifiers().isAbstract();
    }

    @Override
    public boolean isFinal() {
        return getModifiers().isFinal();
    }

    @Override
    public boolean isNative() {
        return getModifiers().isNative();
    }

    @Override
    public boolean isStatic() {
        return getModifiers().isStatic();
    }

    @Override
    public boolean isStrictFp() {
        return getModifiers().isStrictFp();
    }

    @Override
    public boolean isSynchronized() {
        return getModifiers().isSynchronized();
    }

    @Override
    public boolean isVarArgs() {
        return isVarArgs;
    }

    @Override
    public void setAbstract() {
        getModifiers().setModifier(Modifier.ABSTRACT);
    }

    @Override
    public CodeBlockImpl setCodeBlock() {
        return setCodeBlock(getAST().createCodeBlock());
    }

    @Override
    public CodeBlockImpl setCodeBlock(final CodeBlock block) {
        code.set((CodeBlockImpl) block);
        return (CodeBlockImpl) block;
    }

    @Override
    public void setFinal() {
        getModifiers().setModifier(Modifier.FINAL);
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setNative() {
        getModifiers().setModifier(Modifier.NATIVE);
    }

    @Override
    public TypeImpl setReturnType(final Type type) {
        this.returnType.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    public void setStatic() {
        getModifiers().setModifier(Modifier.STATIC);
    }

    @Override
    public void setStrictFp() {
        getModifiers().setModifier(Modifier.STRICTFP);
    }

    @Override
    public void setSynchronized() {
        getModifiers().setModifier(Modifier.SYNCHRONIZED);
    }

    @Override
    public void setVarArgs() {
        this.isVarArgs = true;
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

    TypeDeclarationImpl getDeclaringType() {
        if (getParentTypeBody() != null) {
            return getParentTypeBody().getParentTypeDeclaration();
        } else {
            return null;
        }
    }

    TypeBodyImpl getParentTypeBody() {
        return (TypeBodyImpl) getParentNode();
    }

    private String name;

    private final ChildNode<ModifiersImpl> modifiers = new ChildNode<>(this);

    private final ChildNode<TypeImpl> returnType = new ChildNode<>(this);

    private final ChildNodeList<ParameterImpl> parameters = new ChildNodeList<>(this);

    private final ChildNodeList<TypeImpl> thrown = new ChildNodeList<>(this);

    private final ChildNodeList<TypeParameterImpl> typeParameters = new ChildNodeList<>(this);

    private final Scope scope;

    private boolean isVarArgs;

    private final ChildNode<CodeBlockImpl> code = new ChildNode<>(this);

    @Override
    public MethodInvocation call(final Expression... args) {
        return getAST().createMethodInvocation(this, args);
    }

    @Override
    public String toString() {
        return getEnclosingTypeDeclaration() + "." + getName() + "(...)";
    }

    public MethodImpl copyForOverride() {
        final MethodImpl result = getAST().createMethod(name);
        final Map<TypeParameterImpl, TypeParameterImpl> typeParamMap = new HashMap<>();
        for (final TypeParameterImpl param : getTypeParameters()) {
            typeParamMap.put(param, result.addTypeParameter(param.getName()));
        }
        for (final ParameterImpl param : getParameters()) {
            if (param.getType() instanceof TypeVariableImpl typeVar) {
                final TypeParameter newTypeParam = typeParamMap.get(typeVar.getTypeParameter());
                if (newTypeParam != null) {
                    result.addParameter(getAST().createTypeVariable(newTypeParam), param.getName());
                } else {
                    result.addParameter(null, param.getName());
                }
            } else {
                result.addParameter(param.getType().deepCopy(), param.getName());
            }
        }
        if (getReturnType() != null) {
            result.setReturnType(getReturnType().deepCopy());
        }
        result.setVisibility(getVisibility());
        result.isVarArgs = isVarArgs;
        return result;
    }
}
