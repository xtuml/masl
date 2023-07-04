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
import org.xtuml.masl.javagen.ast.types.Type;

public class ArrayTypeImpl extends ReferenceTypeImpl implements org.xtuml.masl.javagen.ast.types.ArrayType {

    ArrayTypeImpl(final ASTImpl ast, final Type elementType) {
        super(ast);
        setElementType(elementType);
    }

    @Override
    public void setElementType(final Type elementType) {
        this.elementType.set((TypeImpl) elementType);
    }

    @Override
    public TypeImpl getElementType() {
        return elementType.get();
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitArrayType(this, p);
    }

    private final ChildNode<TypeImpl> elementType = new ChildNode<TypeImpl>(this);

    @Override
    public ArrayTypeImpl deepCopy() {
        return new ArrayTypeImpl(getAST(), elementType.get().deepCopy());
    }
}
