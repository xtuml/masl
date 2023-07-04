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
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.expression.SelectedComponentExpression;
import org.xtuml.masl.metamodel.type.UserDefinedType;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Structure;
import org.xtuml.masl.translate.main.Types;

public class SelectedComponentTranslator extends ExpressionTranslator {

    private final Function getter;
    private final Function setter;

    SelectedComponentTranslator(final SelectedComponentExpression selectedComponent, final Scope scope) {
        final Expression prefix = selectedComponent.getPrefix();
        final UserDefinedType prefixType = (UserDefinedType) prefix.getType().getBasicType();

        final ExpressionTranslator prefixTrans = createTranslator(prefix, scope);

        final Structure
                structureTranslator =
                Types.getInstance().getStructureTranslator(prefixType.getTypeDeclaration());

        getter = structureTranslator.getGetter(selectedComponent.getComponent());
        setter = structureTranslator.getSetter(selectedComponent.getComponent());

        setReadExpression(getter.asFunctionCall(prefixTrans.getReadExpression(), false));
        setWriteableExpression(setter.asFunctionCall(prefixTrans.getWriteableExpression(), false));
    }

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

}
