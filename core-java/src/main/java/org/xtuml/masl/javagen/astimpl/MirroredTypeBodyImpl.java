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

import org.xtuml.masl.javagen.ast.def.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MirroredTypeBodyImpl extends TypeBodyImpl implements TypeBody {

    MirroredTypeBodyImpl(final ASTImpl ast, final Class<?> clazz) {
        super(ast);
        this.mirroredClass = clazz;
    }

    @Override
    public InitializerBlockImpl addInitializerBlock(final InitializerBlock declaration) {
        throw new UnsupportedOperationException("Mirrored Class");
    }

    @Override
    public CommentImpl addComment(final Comment comment) {
        throw new UnsupportedOperationException("Mirrored Class");
    }

    @Override
    public MemberGroupImpl addGroup() {
        throw new UnsupportedOperationException("Mirrored Class");
    }

    @Override
    public List<TypeMemberImpl> getMembers() {
        populateMirror();
        return super.getMembers();
    }

    @Override
    boolean containsFieldNamed(final String name) {
        populateMirror();
        return super.containsFieldNamed(name);
    }

    @Override
    boolean containsMethodNamed(final String name) {
        populateMirror();
        return super.containsMethodNamed(name);
    }

    @Override
    boolean containsTypeNamed(final String name) {
        populateMirror();
        return super.containsTypeNamed(name);
    }

    MirroredMethodImpl getConstructorDeclaration(final java.lang.reflect.Constructor<?> constructor) {
        populateMirror();
        return constructorLookup.get(constructor);
    }

    MirroredMethodImpl getMethodDeclaration(final java.lang.reflect.Method method) {
        populateMirror();
        return methodLookup.get(method);
    }

    MirroredTypeDeclarationImpl getTypeDeclaration(final java.lang.Class<?> type) {
        populateMirror();
        return typeLookup.get(type);
    }

    MirroredFieldImpl getFieldDeclaration(final java.lang.reflect.Field field) {
        populateMirror();
        return fieldLookup.get(field);
    }

    @Override
    public FieldImpl addField(final Field field) {
        throw new UnsupportedOperationException("Mirrored Class");
    }

    @Override
    public MethodImpl addMethod(final Method method) {
        throw new UnsupportedOperationException("Mirrored Class");

    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration(final TypeDeclaration declaration) {
        throw new UnsupportedOperationException("Mirrored Class");
    }

    private boolean isVisible(final int modifiers) {
        if (((MirroredPackageImpl) getEnclosingPackage()).isExtensible()) {
            return !java.lang.reflect.Modifier.isPrivate(modifiers);
        } else {
            return java.lang.reflect.Modifier.isPublic(modifiers) || java.lang.reflect.Modifier.isProtected(modifiers);
        }
    }

    private void populateMirror() {
        if (!mirrorPopulated) {
            mirrorPopulated = true;

            for (final java.lang.Class<?> member : mirroredClass.getDeclaredClasses()) {
                // Need to put all package visible types in, as they may be used by
                // other types, methods or fields elsewhere
                if (!member.isSynthetic() && !java.lang.reflect.Modifier.isPrivate(member.getModifiers())) {
                    final MirroredTypeDeclarationImpl
                            typeDeclaration =
                            new MirroredTypeDeclarationImpl(getAST(), member);
                    super.addTypeDeclaration(typeDeclaration);
                    typeLookup.put(member, typeDeclaration);
                }
            }

            for (final java.lang.reflect.Constructor<?> member : mirroredClass.getDeclaredConstructors()) {
                if (!member.isSynthetic() && isVisible(member.getModifiers())) {
                    final MirroredMethodImpl constructor = new MirroredMethodImpl(getAST(), member);
                    super.addMethod(constructor);
                    constructorLookup.put(member, constructor);
                }
            }

            for (final java.lang.reflect.Method member : mirroredClass.getDeclaredMethods()) {
                if (!member.isSynthetic() && isVisible(member.getModifiers())) {
                    final MirroredMethodImpl method = new MirroredMethodImpl(getAST(), member);
                    super.addMethod(method);
                    methodLookup.put(member, method);
                }
            }

            for (final java.lang.reflect.Field member : mirroredClass.getDeclaredFields()) {
                if (!member.isSynthetic() && isVisible(member.getModifiers())) {
                    final MirroredFieldImpl field = new MirroredFieldImpl(getAST(), member);
                    super.addField(field);
                    fieldLookup.put(member, field);
                }
            }
        }
    }

    private Class<?> mirroredClass = null;
    private final Map<java.lang.reflect.Constructor<?>, MirroredMethodImpl> constructorLookup = new HashMap<>();
    private final Map<java.lang.reflect.Method, MirroredMethodImpl> methodLookup = new HashMap<>();
    private final Map<java.lang.Class<?>, MirroredTypeDeclarationImpl> typeLookup = new HashMap<>();
    private final Map<java.lang.reflect.Field, MirroredFieldImpl> fieldLookup = new HashMap<>();
    private boolean mirrorPopulated = false;

}
