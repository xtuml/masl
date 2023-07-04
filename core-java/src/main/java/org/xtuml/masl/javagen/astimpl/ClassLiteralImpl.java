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
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.types.Type;

public class ClassLiteralImpl extends ExpressionImpl implements ClassLiteral {

    public ClassLiteralImpl(final ASTImpl ast, final Type type) {
        super(ast);
        setType(type);

    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitClassLiteral(this, p);
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    protected int getPrecedence() {
        return Integer.MAX_VALUE;
    }

    private final ChildNode<TypeImpl> type = new ChildNode<TypeImpl>(this);

}
