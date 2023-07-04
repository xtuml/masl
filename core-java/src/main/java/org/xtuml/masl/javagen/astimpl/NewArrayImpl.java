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
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewArray;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.Collections;
import java.util.List;

class NewArrayImpl extends ExpressionImpl implements NewArray {

    NewArrayImpl(final ASTImpl ast, final Type type, final int noDimensons, final ArrayInitializer initialValue) {
        super(ast);
        setType(type);
        setNoDimensions(noDimensons);
        setInitialValue(initialValue);
    }

    NewArrayImpl(final ASTImpl ast, final Type type, final int noDimensons, final Expression... dimensionSizes) {
        super(ast);
        setType(type);
        setNoDimensions(noDimensons);
        for (final Expression dimSize : dimensionSizes) {
            addDimensionSize(dimSize);
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitNewArray(this);
    }

    @Override
    public ExpressionImpl addDimensionSize(final Expression dimensionSize) {
        this.dimensionSizes.add((ExpressionImpl) dimensionSize);
        return (ExpressionImpl) dimensionSize;
    }

    @Override
    public List<? extends ExpressionImpl> getDimensionSizes() {
        return Collections.unmodifiableList(dimensionSizes);
    }

    @Override
    public ArrayInitializerImpl getInitialValue() {
        return initialValue.get();
    }

    @Override
    public int getNoDimensions() {
        return noDimensions;
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public ArrayInitializerImpl setInitialValue(final ArrayInitializer initialValue) {
        this.initialValue.set((ArrayInitializerImpl) initialValue);
        return (ArrayInitializerImpl) initialValue;
    }

    @Override
    public void setNoDimensions(final int noDimensions) {
        this.noDimensions = noDimensions;
    }

    @Override
    public TypeImpl setType(final Type type) {
        this.type.set((TypeImpl) type);
        return (TypeImpl) type;
    }

    @Override
    protected int getPrecedence() {
        return 13;
    }

    private final ChildNodeList<ExpressionImpl> dimensionSizes = new ChildNodeList<>(this);

    private int noDimensions;

    private final ChildNode<ArrayInitializerImpl> initialValue = new ChildNode<>(this);

    private final ChildNode<TypeImpl> type = new ChildNode<>(this);
}
