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

import org.xtuml.masl.javagen.ast.code.Variable;
import org.xtuml.masl.javagen.ast.def.Modifier;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.EnumSet;

abstract class VariableImpl extends ASTNodeImpl implements Variable, ModifiersImpl.Filter {

    VariableImpl(final ASTImpl ast, final Type type, final String name) {
        super(ast);
        modifiers.set(new ModifiersImpl(ast, this));
        setName(name);
        setType(type);

    }

    VariableImpl(final ASTImpl ast) {
        super(ast);
        modifiers.set(new ModifiersImpl(ast, this));
    }

    @Override
    public ModifiersImpl getModifiers() {
        return modifiers.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeImpl getType() {
        return type.get();
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setType(final Type type) {
        this.type.set((TypeImpl) type);
    }

    private final ChildNode<TypeImpl> type = new ChildNode<>(this);
    private final ChildNode<ModifiersImpl> modifiers = new ChildNode<>(this);
    private String name;

    @Override
    public VariableAccess asExpression() {
        return getAST().createVariableAccess(this);
    }

    @Override
    public boolean isFinal() {
        return getModifiers().isFinal();
    }

    @Override
    public void setFinal() {
        getModifiers().setModifier(Modifier.FINAL);
    }

    @Override
    public EnumSet<Modifier> getImplicitModifiers() {
        return EnumSet.noneOf(Modifier.class);
    }

}
