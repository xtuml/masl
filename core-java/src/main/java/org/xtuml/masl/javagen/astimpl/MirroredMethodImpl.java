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

import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.def.Parameter;
import org.xtuml.masl.javagen.ast.def.TypeParameter;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MirroredMethodImpl extends MethodImpl {

    MirroredMethodImpl(final ASTImpl ast, final java.lang.reflect.Method method) {
        super(ast, method.getName());
        this.mirroredMethod = method;
        this.mirroredConstructor = null;
        getModifiers().setModifiers(method.getModifiers());
    }

    MirroredMethodImpl(final ASTImpl ast, final java.lang.reflect.Constructor<?> constructor) {
        super(ast);
        this.mirroredConstructor = constructor;
        this.mirroredMethod = null;
        getModifiers().setModifiers(constructor.getModifiers());
    }

    void populateMirror() {
        if (mirroredMethod != null && !mirrorPopulated) {
            mirrorPopulated = true;

            for (final java.lang.reflect.TypeVariable<?> tp : mirroredMethod.getTypeParameters()) {
                final MirroredTypeParameterImpl parameter = new MirroredTypeParameterImpl(getAST(), tp, getScope());
                super.addTypeParameter(parameter);
                typeParameterLookup.put(tp, parameter);
            }

            super.setReturnType(getAST().createType(mirroredMethod.getGenericReturnType()));
            for (final java.lang.reflect.Type paramType : mirroredMethod.getGenericParameterTypes()) {
                final String paramName = "p" + (super.getParameters().size() + 1);
                super.addParameter(new ParameterImpl(getAST(), getAST().createType(paramType), paramName));
            }

            for (final java.lang.reflect.Type thrownType : mirroredMethod.getGenericExceptionTypes()) {
                super.addThrownException(getAST().createType(thrownType));
            }
            if (mirroredMethod.isVarArgs()) {
                super.setVarArgs();
            }
        } else if (mirroredConstructor != null && !mirrorPopulated) {
            mirrorPopulated = true;

            for (final java.lang.reflect.TypeVariable<?> tp : mirroredConstructor.getTypeParameters()) {
                final MirroredTypeParameterImpl parameter = new MirroredTypeParameterImpl(getAST(), tp, getScope());
                super.addTypeParameter(parameter);
                typeParameterLookup.put(tp, parameter);
            }

            for (final java.lang.reflect.Type paramType : mirroredConstructor.getGenericParameterTypes()) {
                final String paramName = "p" + (super.getParameters().size() + 1);
                super.addParameter(new ParameterImpl(getAST(), getAST().createType(paramType), paramName));
            }

            for (final java.lang.reflect.Type thrownType : mirroredConstructor.getGenericExceptionTypes()) {
                super.addThrownException(getAST().createType(thrownType));
            }
            if (mirroredConstructor.isVarArgs()) {
                super.setVarArgs();
            }
        }

    }

    MirroredTypeParameterImpl getTypeParameter(final java.lang.reflect.TypeVariable<?> variable) {
        populateMirror();
        return typeParameterLookup.get(variable);
    }

    @Override
    public TypeParameterImpl addTypeParameter(final TypeParameter parameter) {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    @Override
    public ParameterImpl addParameter(final Parameter parameter) {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    @Override
    public TypeImpl addThrownException(final Type exceptionType) {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    @Override
    public List<ParameterImpl> getParameters() {
        populateMirror();
        return super.getParameters();
    }

    @Override
    public List<TypeImpl> getThrownExceptions() {
        populateMirror();
        return super.getThrownExceptions();
    }

    @Override
    public TypeImpl getReturnType() {
        populateMirror();
        return super.getReturnType();
    }

    @Override
    public TypeImpl setReturnType(final Type type) {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    @Override
    public void setVarArgs() {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    @Override
    public List<TypeParameterImpl> getTypeParameters() {
        populateMirror();
        return super.getTypeParameters();
    }

    @Override
    public boolean isVarArgs() {
        populateMirror();
        return super.isVarArgs();
    }

    @Override
    public CodeBlockImpl setCodeBlock(final CodeBlock block) {
        throw new UnsupportedOperationException("Mirrored Method/Constructor");
    }

    private final java.lang.reflect.Method mirroredMethod;
    private final java.lang.reflect.Constructor<?> mirroredConstructor;
    private boolean mirrorPopulated = false;
    private final Map<java.lang.reflect.TypeVariable<?>, MirroredTypeParameterImpl>
            typeParameterLookup =
            new HashMap<java.lang.reflect.TypeVariable<?>, MirroredTypeParameterImpl>();

}
