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

import org.xtuml.masl.javagen.ast.types.Type;

class MirroredFieldImpl extends FieldImpl {

    MirroredFieldImpl(final ASTImpl ast, final java.lang.reflect.Field field) {
        super(ast);
        super.setType(ast.createType(field.getGenericType()));
        super.setName(field.getName());
        getModifiers().setModifiers(field.getModifiers());
    }

    @Override
    public void setName(final String name) {
        throw new UnsupportedOperationException("Mirrored Field");
    }

    @Override
    public TypeImpl setType(final Type type) {
        throw new UnsupportedOperationException("Mirrored Field");
    }

    boolean mirrorPopulated = false;
}
