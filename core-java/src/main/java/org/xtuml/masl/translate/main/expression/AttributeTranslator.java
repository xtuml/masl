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
