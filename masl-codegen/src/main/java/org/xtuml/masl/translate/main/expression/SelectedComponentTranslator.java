/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
