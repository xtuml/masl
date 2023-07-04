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

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.metamodel.expression.InstanceOrderingExpression;
import org.xtuml.masl.metamodel.expression.StructureOrderingExpression;
import org.xtuml.masl.metamodel.type.CollectionType;
import org.xtuml.masl.metamodel.type.InstanceType;
import org.xtuml.masl.translate.main.*;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderingTranslator extends ExpressionTranslator {

    OrderingTranslator(final InstanceOrderingExpression ordering, final Scope scope) {
        final org.xtuml.masl.metamodel.expression.Expression collection = ordering.getCollection();

        final CollectionType collectionType = (CollectionType) ordering.getType();
        final InstanceType instanceType = (InstanceType) collectionType.getContainedType();

        final ObjectTranslator objTrans = ObjectTranslator.getInstance(instanceType.getObjectDeclaration());

        // &SWA::ObjectPtr<OBJECT>::get
        final Expression
                ptrDerefFunction =
                Architecture.objectPtr(new TypeUsage(objTrans.getMainClass())).getFunctionPointer("deref");

        final Expression predicateFn = objTrans.getOrderByPredicate(ordering.getOrder()).asFunctionPointer();

        final Expression
                predicate =
                Boost.bind.asFunctionCall(predicateFn,
                                          Boost.bind.asFunctionCall(ptrDerefFunction, Boost.bind_1),
                                          Boost.bind.asFunctionCall(ptrDerefFunction, Boost.bind_2));

        final ExpressionTranslator collTrans = createTranslator(collection, scope);
        final Function orderFn = new Function((ordering.isReverse() ? "reverse_" : "") + "ordered_by");

        setReadExpression(orderFn.asFunctionCall(collTrans.getReadExpression(), false, predicate));
        setWriteableExpression(orderFn.asFunctionCall(collTrans.getWriteableExpression(), false, predicate));

    }

    OrderingTranslator(final StructureOrderingExpression ordering, final Scope scope) {
        final org.xtuml.masl.metamodel.expression.Expression collection = ordering.getCollection();

        final CollectionType collectionType = (CollectionType) ordering.getType();

        final Structure
                structTrans =
                Types.getInstance().getStructureTranslator(collectionType.getContainedType().getBasicType().getTypeDeclaration());

        Expression predicateExpression = null;

        // Go through backwards to aid setting up the comparison expression
        final List<StructureOrderingExpression.Component> reverseComponentOrder = new ArrayList<>(ordering.getOrder());
        Collections.reverse(reverseComponentOrder);

        for (final StructureOrderingExpression.Component component : reverseComponentOrder) {
            final Expression attribute = structTrans.getGetter(component.getElement()).asFunctionPointer();

            final Expression lhsAttribute = Boost.lambda_bind.asFunctionCall(attribute, Boost.lambda_1);
            final Expression rhsAttribute = Boost.lambda_bind.asFunctionCall(attribute, Boost.lambda_2);

            predicateExpression =
                    Structure.buildComparator(predicateExpression, lhsAttribute, rhsAttribute, component.isReverse());
        }

        final Function orderby = new Function((ordering.isReverse() ? "reverse_" : "") + "ordered_by");

        final ExpressionTranslator collTrans = createTranslator(collection, scope);

        if (predicateExpression == null) {
            setReadExpression(orderby.asFunctionCall(collTrans.getReadExpression(), false));
            setWriteableExpression(orderby.asFunctionCall(collTrans.getWriteableExpression(), false));
        } else {
            setReadExpression(orderby.asFunctionCall(collTrans.getReadExpression(), false, predicateExpression));
            setWriteableExpression(orderby.asFunctionCall(collTrans.getWriteableExpression(),
                                                          false,
                                                          predicateExpression));
        }

    }

}
