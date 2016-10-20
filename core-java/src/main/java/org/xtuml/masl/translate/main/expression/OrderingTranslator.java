//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.metamodel.expression.InstanceOrderingExpression;
import org.xtuml.masl.metamodel.expression.StructureOrderingExpression;
import org.xtuml.masl.metamodel.type.CollectionType;
import org.xtuml.masl.metamodel.type.InstanceType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Structure;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.object.ObjectTranslator;



public class OrderingTranslator extends ExpressionTranslator
{

  OrderingTranslator ( final InstanceOrderingExpression ordering, final Scope scope )
  {
    final org.xtuml.masl.metamodel.expression.Expression collection = ordering.getCollection();

    final CollectionType collectionType = (CollectionType)ordering.getType();
    final InstanceType instanceType = (InstanceType)collectionType.getContainedType();

    final ObjectTranslator objTrans = ObjectTranslator.getInstance(instanceType.getObjectDeclaration());


    // &SWA::ObjectPtr<OBJECT>::get
    final Expression ptrDerefFunction = Architecture.objectPtr(new TypeUsage(objTrans.getMainClass())).getFunctionPointer("deref");

    final Expression predicateFn = objTrans.getOrderByPredicate(ordering.getOrder()).asFunctionPointer();

    final Expression predicate = Boost.bind.asFunctionCall(predicateFn,
                                                           Boost.bind.asFunctionCall(ptrDerefFunction, Boost.bind_1),
                                                           Boost.bind.asFunctionCall(ptrDerefFunction, Boost.bind_2));

    final ExpressionTranslator collTrans = createTranslator(collection, scope);
    final Function orderFn = new Function((ordering.isReverse() ? "reverse_" : "") + "ordered_by");

    setReadExpression(orderFn.asFunctionCall(collTrans.getReadExpression(), false, predicate));
    setWriteableExpression(orderFn.asFunctionCall(collTrans.getWriteableExpression(), false, predicate));

  }

  OrderingTranslator ( final StructureOrderingExpression ordering, final Scope scope )
  {
    final org.xtuml.masl.metamodel.expression.Expression collection = ordering.getCollection();

    final CollectionType collectionType = (CollectionType)ordering.getType();

    final Structure structTrans = Types.getInstance().getStructureTranslator(collectionType.getContainedType()
                                                                                           .getBasicType()
                                                                                           .getTypeDeclaration());


    Expression predicateExpression = null;


    // Go through backwards to aid setting up the comparison expression
    final List<StructureOrderingExpression.Component> reverseComponentOrder = new ArrayList<StructureOrderingExpression.Component>(ordering.getOrder());
    Collections.reverse(reverseComponentOrder);

    for ( final StructureOrderingExpression.Component component : reverseComponentOrder )
    {
      final Expression attribute = structTrans.getGetter(component.getElement()).asFunctionPointer();

      final Expression lhsAttribute = Boost.lambda_bind.asFunctionCall(attribute, Boost.lambda_1);
      final Expression rhsAttribute = Boost.lambda_bind.asFunctionCall(attribute, Boost.lambda_2);

      predicateExpression = Structure.buildComparator(predicateExpression, lhsAttribute, rhsAttribute, component.isReverse());
    }

    final Function orderby = new Function((ordering.isReverse() ? "reverse_" : "") + "ordered_by");

    final ExpressionTranslator collTrans = createTranslator(collection, scope);

    if ( predicateExpression == null )
    {
      setReadExpression(orderby.asFunctionCall(collTrans.getReadExpression(), false));
      setWriteableExpression(orderby.asFunctionCall(collTrans.getWriteableExpression(), false));
    }
    else
    {
      setReadExpression(orderby.asFunctionCall(collTrans.getReadExpression(), false, predicateExpression));
      setWriteableExpression(orderby.asFunctionCall(collTrans.getWriteableExpression(), false, predicateExpression));
    }

  }

}
