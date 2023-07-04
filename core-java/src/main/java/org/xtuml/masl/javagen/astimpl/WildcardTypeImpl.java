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
import org.xtuml.masl.javagen.ast.types.ReferenceType;
import org.xtuml.masl.javagen.ast.types.WildcardType;

public class WildcardTypeImpl extends TypeImpl implements WildcardType {

    enum Direction {
        EXTENDS, SUPER
    }

    WildcardTypeImpl(final ASTImpl ast) {
        super(ast);
    }

    WildcardTypeImpl(final ASTImpl ast, final java.lang.reflect.WildcardType type) {
        super(ast);
        if (type.getLowerBounds().length > 0) {
            superBound.set((ReferenceTypeImpl) ast.createType(type.getLowerBounds()[0]));
        } else {
            extendsBound.set((ReferenceTypeImpl) ast.createType(type.getUpperBounds()[0]));
        }
    }

    @Override
    public ReferenceTypeImpl getSuperBound() {
        return superBound.get();
    }

    @Override
    public ReferenceTypeImpl getExtendsBound() {
        return extendsBound.get();
    }

    @Override
    public void setExtendsBound(final ReferenceType extendsBound) {
        this.extendsBound.set((ReferenceTypeImpl) extendsBound);
    }

    @Override
    public void setSuperBound(final ReferenceType superBound) {
        this.superBound.set((ReferenceTypeImpl) superBound);
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitWildcardType(this);
    }

    private final ChildNode<ReferenceTypeImpl> superBound = new ChildNode<>(this);
    private final ChildNode<ReferenceTypeImpl> extendsBound = new ChildNode<>(this);

    @Override
    public TypeImpl deepCopy() {
        final WildcardTypeImpl result = new WildcardTypeImpl(getAST());
        if (getSuperBound() != null) {
            result.setSuperBound(getSuperBound().deepCopy());
        }
        if (getExtendsBound() != null) {
            result.setExtendsBound(getExtendsBound().deepCopy());
        }
        return result;
    }

}
