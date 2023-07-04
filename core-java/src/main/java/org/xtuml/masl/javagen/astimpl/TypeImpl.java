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

import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;

abstract class TypeImpl extends ASTNodeImpl implements org.xtuml.masl.javagen.ast.types.Type {

    TypeImpl(final ASTImpl ast) {
        super(ast);
    }

    @Override
    public NewArrayImpl newArray(final int noDimensions, final ArrayInitializer initialValue) {
        return getAST().createNewArray(this, noDimensions, initialValue);
    }

    @Override
    public NewArrayImpl newArray(final int noDimensions, final Expression... dimensionSizes) {
        return getAST().createNewArray(this, noDimensions, dimensionSizes);
    }

    @Override
    public CastImpl cast(final Expression expression) {
        return getAST().createCast(this, expression);
    }

    @Override
    public ClassLiteralImpl clazz() {
        return getAST().createClassLiteral(this);
    }

    abstract public TypeImpl deepCopy();

}
