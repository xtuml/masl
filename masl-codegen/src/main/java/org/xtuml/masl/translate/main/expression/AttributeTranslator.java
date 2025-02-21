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
import org.xtuml.masl.metamodel.expression.FindAttributeNameExpression;
import org.xtuml.masl.metamodel.expression.SelectedAttributeExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

public class AttributeTranslator extends ExpressionTranslator {

    private ObjectTranslator objTrans = null;

    AttributeTranslator(final SelectedAttributeExpression selectedAttribute, final Scope scope) {
        final ExpressionTranslator prefixTrans = createTranslator(selectedAttribute.getPrefix(), scope);

        final AttributeDeclaration att = selectedAttribute.getAttribute();
        final ObjectDeclaration obj = att.getParentObject();
        objTrans = ObjectTranslator.getInstance(obj);
        getter = objTrans.getAttributeGetter(att);
        setter = objTrans.getAttributeSetter(att);

        setReadExpression(getter.asFunctionCall(prefixTrans.getReadExpression(), true));
        setWriteFunction(setter, prefixTrans.getWriteableExpression(), true);

    }

    AttributeTranslator(final FindAttributeNameExpression attributeName, final Scope scope) {
        final AttributeDeclaration att = attributeName.getAttribute();
        final ObjectDeclaration obj = att.getParentObject();

        final ObjectTranslator objTrans = ObjectTranslator.getInstance(obj);
        getter = objTrans.getAttributeGetter(att);
        setter = objTrans.getAttributeSetter(att);

        setReadExpression(getter.asFunctionCall());
        setWriteFunction(setter);

    }

    public Function getGetter() {
        return this.getter;
    }

    public Function getSetter() {
        return this.setter;
    }

    private final Function getter;
    private final Function setter;

}
