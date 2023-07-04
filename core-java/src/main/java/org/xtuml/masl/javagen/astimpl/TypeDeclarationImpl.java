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
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.*;

class TypeDeclarationImpl extends TypeMemberImpl implements TypeDeclaration, ModifiersImpl.Filter, Scoped {

    private final class TDScope extends Scope {

        TDScope() {
            super(TypeDeclarationImpl.this);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final EnumConstantAccessImpl enumAccess,
                                            boolean visible,
                                            boolean shadowed) {
            for (final TypeDeclarationImpl extendedType : getExtendedTypes()) {
                if (extendedType.getTypeBody().containsNonPrivateTypeNamed(enumAccess.getConstant().getName())) {
                    if (enumAccess.getConstant().getDeclaringType() == extendedType) {
                        visible = true;
                    } else {
                        // In certain pathological cases, where a field is defined in a
                        // supertype and one with the same name is defined in a supertype of
                        // that supertype, we might return shadowed when not strictly
                        // necessary. However, it will still be correct code, and arguably
                        // clearer for a human.
                        shadowed = true;
                    }
                }
            }

            return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final FieldAccessImpl fieldAccess,
                                            boolean visible,
                                            boolean shadowed) {
            for (final TypeDeclarationImpl extendedType : getExtendedTypes()) {
                if (extendedType.getTypeBody().containsNonPrivateTypeNamed(fieldAccess.getField().getName())) {
                    if (fieldAccess.getField().getDeclaringType() == extendedType) {
                        visible = true;
                    } else {
                        // In certain pathological cases, where a field is defined in a
                        // supertype and one with the same name is defined in a supertype of
                        // that supertype, we might return shadowed when not strictly
                        // necessary. However, it will still be correct code, and arguably
                        // clearer for a human.
                        shadowed = true;
                    }
                }
            }

            return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final MethodInvocationImpl methodCall,
                                            boolean visible,
                                            final boolean shadowed) {
            for (final TypeDeclarationImpl extendedType : getExtendedTypes()) {
                if (methodCall.getMethod().getDeclaringType() == extendedType) {
                    visible = true;
                }
            }

            return super.requiresQualifier(baseScope, methodCall, visible, shadowed);
        }

        @Override
        protected boolean requiresQualifier(final Scope baseScope,
                                            final TypeDeclarationImpl typeDeclaration,
                                            boolean visible,
                                            boolean shadowed) {
            for (final TypeDeclarationImpl extendedType : getExtendedTypes()) {
                if (extendedType.getTypeBody().containsNonPrivateTypeNamed(typeDeclaration.getName())) {
                    if (typeDeclaration.getDeclaringType() == extendedType) {
                        visible = true;
                    } else {
                        // In certain pathological cases, where a type is defined in a
                        // supertype and one with the same name is defined in a supertype of
                        // that supertype, we might return shadowed when not strictly
                        // necessary. However, it will still be correct code, and arguably
                        // clearer for a human.
                        shadowed = true;
                    }
                }
            }
            return super.requiresQualifier(baseScope, typeDeclaration, visible, shadowed);
        }

    }

    TypeDeclarationImpl(final ASTImpl ast, final String name) {
        super(ast);
        this.name = name;
        this.modifiers.set(new ModifiersImpl(ast, this));
        this.typeBody.set(new TypeBodyImpl(ast));
        scope = new TDScope();

    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitTypeDeclaration(this, p);
    }

    @Override
    public Comment addComment(final Comment comment) {
        return getTypeBody().addComment(comment);
    }

    @Override
    public Comment addComment(final String text) {
        return getTypeBody().addComment(text);
    }

    @Override
    public Constructor addConstructor(final Parameter... params) {
        return getTypeBody().addConstructor(params);
    }

    @Override
    public Constructor addConstructor(final Constructor constructor) {
        return getTypeBody().addConstructor(constructor);
    }

    @Override
    public EnumConstant addEnumConstant(final EnumConstant enumConstant) {
        return typeBody.get().addEnumConstant(enumConstant);
    }

    @Override
    public EnumConstant addEnumConstant(final String name, final Expression... args) {
        return typeBody.get().addEnumConstant(name, args);
    }

    @Override
    public Field addField(final Field field) {
        return getTypeBody().addField(field);
    }

    @Override
    public Field addField(final Type type, final String name) {
        return getTypeBody().addField(type, name);
    }

    @Override
    public Field addField(final Type type, final String name, final Expression initialValue) {
        return getTypeBody().addField(type, name, initialValue);
    }

    @Override
    public TypeMemberGroup addGroup() {
        return getTypeBody().addGroup();
    }

    @Override
    public InitializerBlock addInitializerBlock(final boolean isStatic) {
        return getTypeBody().addInitializerBlock(isStatic);
    }

    @Override
    public InitializerBlock addInitializerBlock(final InitializerBlock declaration) {
        return getTypeBody().addInitializerBlock(declaration);
    }

    @Override
    public DeclaredTypeImpl addInterface(final DeclaredType iface) {
        interfaces.add((DeclaredTypeImpl) iface);
        return (DeclaredTypeImpl) iface;
    }

    @Override
    public Method overrideMethod(final Method superMethod) {
        return getTypeBody().overrideMethod(superMethod);
    }

    @Override
    public Method addMethod(final Method method) {
        return getTypeBody().addMethod(method);
    }

    @Override
    public Method addMethod(final String name, final Type returnType, final Parameter... params) {
        return getTypeBody().addMethod(name, returnType, params);
    }

    @Override
    public Method addMethod(final String name, final Parameter... params) {
        return getTypeBody().addMethod(name, params);
    }

    @Override
    public Property addProperty(final Type type, final String name) {

        return getTypeBody().addProperty(type, name);
    }

    @Override
    public Property addProperty(final Type type, final String name, final Constructor initBy) {
        return getTypeBody().addProperty(type, name, initBy);
    }

    @Override
    public TypeDeclaration addTypeDeclaration(final String name) {
        return getTypeBody().addTypeDeclaration(name);
    }

    @Override
    public TypeDeclaration addTypeDeclaration(final TypeDeclaration typeDeclaration) {
        return getTypeBody().addTypeDeclaration(typeDeclaration);
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
    public TypeDeclarationStatementImpl asStatement() {
        return getAST().createTypeDeclarationStatement(this);
    }

    @Override
    public This asThis() {
        return getAST().createThis(this);
    }

    @Override
    public DeclaredTypeImpl asType(final Type... args) {
        return getAST().createDeclaredType(this, args);
    }

    @Override
    public ClassLiteralImpl clazz() {
        return this.asType().clazz();
    }

    @Override
    public CompilationUnitImpl getDeclaringCompilationUnit() {
        if (getParentNode() instanceof CompilationUnitImpl) {
            return (CompilationUnitImpl) getParentNode();
        } else {
            return null;
        }
    }

    @Override
    public TypeDeclarationImpl getDeclaringType() {
        if (getParentTypeBody() != null) {
            return getParentTypeBody().getParentTypeDeclaration();
        } else {
            return null;
        }
    }

    @Override
    public List<? extends EnumConstant> getEnumConstants() {
        return typeBody.get().getEnumConstants();
    }

    @Override
    public EnumSet<Modifier> getImplicitModifiers() {
        final EnumSet<Modifier> result = EnumSet.noneOf(Modifier.class);
        if (isInterface()) {
            result.add(Modifier.ABSTRACT);
        }

        if (getDeclaringType() != null && getDeclaringType().isInterface()) {
            result.add(Modifier.STATIC);
            result.add(Modifier.PUBLIC);
        }
        return result;
    }

    @Override
    public List<DeclaredTypeImpl> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    @Override
    public org.xtuml.masl.javagen.astimpl.ModifiersImpl getModifiers() {
        return modifiers.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public DeclaredTypeImpl getSupertype() {
        return supertype.get();
    }

    @Override
    public TypeBodyImpl getTypeBody() {
        return typeBody.get();
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
    public boolean isAnnotation() {
        return isAnnotation;
    }

    @Override
    public boolean isEnum() {
        return isEnum;
    }

    @Override
    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public void setAbstract() {
        getModifiers().setModifier(Modifier.ABSTRACT);
    }

    @Override
    public void setAnnotation() {
        setInterface();
        isAnnotation = true;
    }

    @Override
    public void setFinal() {
        getModifiers().setModifier(Modifier.FINAL);
    }

    @Override
    public void setInterface() {
        isInterface = true;
        supertype.clear();
    }

    @Override
    public void setEnum() {
        isEnum = true;
        supertype.clear();
    }

    @Override
    public void setName(final String name) {
        this.name = name;
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
    public DeclaredTypeImpl setSupertype(final DeclaredType supertype) {
        this.supertype.set((DeclaredTypeImpl) supertype);
        return (DeclaredTypeImpl) supertype;
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
    public String toString() {
        if (getDeclaringCompilationUnit() != null) {
            return getEnclosingPackage() + "." + getName();
        } else {
            return getEnclosingTypeDeclaration() + "$" + getName();
        }
    }

    protected void setTypeBody(final TypeBodyImpl typeBody) {
        this.typeBody.set(typeBody);
    }

    Scope getDeclaringScope() {
        return getScope().getParentScope();
    }

    TypeBodyImpl getParentTypeBody() {
        if (getParentNode() instanceof TypeBodyImpl) {
            return (TypeBodyImpl) getParentNode();
        } else {
            return null;
        }
    }

    private Collection<TypeDeclarationImpl> getExtendedTypes() {
        final Set<TypeDeclarationImpl> result = new HashSet<TypeDeclarationImpl>();
        if (getSupertype() != null) {
            result.add((getSupertype()).getTypeDeclaration());
            result.addAll((getSupertype()).getTypeDeclaration().getExtendedTypes());
        }

        for (final ReferenceType iface : getInterfaces()) {
            if (iface instanceof DeclaredTypeImpl) {
                result.add(((DeclaredTypeImpl) iface).getTypeDeclaration());
                result.addAll(((DeclaredTypeImpl) iface).getTypeDeclaration().getExtendedTypes());
            }
        }

        return result;
    }

    private String name;

    private final ChildNode<ModifiersImpl> modifiers = new ChildNode<ModifiersImpl>(this);

    private final ChildNode<DeclaredTypeImpl> supertype = new ChildNode<DeclaredTypeImpl>(this);

    private final List<DeclaredTypeImpl> interfaces = new ChildNodeList<DeclaredTypeImpl>(this);

    private final ChildNodeList<TypeParameterImpl> typeParameters = new ChildNodeList<TypeParameterImpl>(this);

    private boolean isInterface = false;

    private boolean isAnnotation = false;

    private boolean isEnum = false;

    private final TDScope scope;

    private final ChildNode<TypeBodyImpl> typeBody = new ChildNode<TypeBodyImpl>(this);

}
