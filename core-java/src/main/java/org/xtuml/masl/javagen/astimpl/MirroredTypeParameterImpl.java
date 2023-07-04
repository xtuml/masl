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

import org.xtuml.masl.javagen.ast.types.ReferenceType;

public class MirroredTypeParameterImpl extends TypeParameterImpl
        implements org.xtuml.masl.javagen.ast.def.TypeParameter {

    MirroredTypeParameterImpl(final ASTImpl ast,
                              final java.lang.reflect.TypeVariable<?> param,
                              final Scope declaringScope) {
        super(ast, param.getName());
        for (final java.lang.reflect.Type bound : param.getBounds()) {
            if (bound != Object.class) {
                super.addExtendsBound((ReferenceTypeImpl) getAST().createType(bound));
            }
        }

    }

    @Override
    public ReferenceTypeImpl addExtendsBound(final ReferenceType extendsBound) {
        throw new UnsupportedOperationException("Mirrored Type Parameter");
    }

}
