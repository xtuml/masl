//
// File: NameExpressionTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.expression.ObjectNameExpression;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.object.ObjectTranslator;



public class FindTranslator extends ExpressionTranslator
{

  private static Function FIND      = new Function("find");
  private static Function FIND_ONE  = new Function("find_one");
  private static Function FIND_ONLY = new Function("find_only");

  FindTranslator ( final FindExpression find, final Scope scope )
  {
    if ( find.getCollection() instanceof ObjectNameExpression )
    {
      final ObjectTranslator objTrans = ObjectTranslator.getInstance(find.getInstanceType().getObjectDeclaration());


      if ( find.getSkeleton() == null )
      {
        final Function findFunction = objTrans.getFindFunction(null, find.getFindType());
        setReadExpression(findFunction.asFunctionCall());
      }
      else
      {
        final Function findFunction = objTrans.getFindFunction(find.getSkeleton(), find.getFindType());

        final List<Expression> findParams = new ArrayList<Expression>();

        for ( final org.xtuml.masl.metamodel.expression.Expression param : find.getArguments() )
        {
          findParams.add(createTranslator(param, scope).getReadExpression());
        }

        setReadExpression(findFunction.asFunctionCall(findParams));
        setWriteableExpression(findFunction.asFunctionCall(findParams));

      }
    }
    else
    {

      Function findFunction = null;
      switch ( find.getFindType() )
      {
        case FIND:
          findFunction = FIND;
          break;
        case FIND_ONE:
          findFunction = FIND_ONE;
          break;
        case FIND_ONLY:
          findFunction = FIND_ONLY;
          break;
        default:
          throw new IllegalArgumentException("Unrecognised find type: " + find.getFindType());
      }

      // Already have a collection of instances in a variable, so no
      // optimisation possible.

      if ( find.getSkeleton() != null )
      {
        final ObjectTranslator objTrans = ObjectTranslator.getInstance(find.getInstanceType().getObjectDeclaration());

        final List<? extends org.xtuml.masl.metamodel.expression.Expression> maslParams = find.getArguments();

        final List<Expression> findArgs = new ArrayList<Expression>(maslParams.size());

        for ( final org.xtuml.masl.metamodel.expression.Expression param : maslParams )
        {
          findArgs.add(createTranslator(param, scope).getReadExpression());
        }

        final Expression predicate = objTrans.getBoundPredicate(find.getSkeleton(), findArgs);

        final ExpressionTranslator lhsTrans = createTranslator(find.getCollection(), scope);

        setReadExpression(findFunction.asFunctionCall(lhsTrans.getReadExpression(), false, predicate));
        setWriteableExpression(findFunction.asFunctionCall(lhsTrans.getWriteableExpression(), false, predicate));
      }
      else
      {
        final ExpressionTranslator lhsTrans = createTranslator(find.getCollection(), scope);

        setReadExpression(findFunction.asFunctionCall(lhsTrans.getReadExpression(), false));
        setWriteableExpression(findFunction.asFunctionCall(lhsTrans.getWriteableExpression(), false));
      }

    }
  }


}
