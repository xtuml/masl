//
// File: PredicateNameMangler.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.metamodel.expression.BinaryExpression;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.expression.FindAttributeNameExpression;
import org.xtuml.masl.metamodel.expression.FindParameterExpression;
import org.xtuml.masl.metamodel.expression.SelectedComponentExpression;
import org.xtuml.masl.metamodel.expression.UnaryExpression;


public abstract class PredicateNameMangler
{

  private static String openParenthesisMangle  = "OP";
  private static String closeParenthesisMangle = "CP";

  private static class SimpleMangler extends PredicateNameMangler
  {

    SimpleMangler ( final String name )
    {
      setName(name);
    }
  }


  private static class BinaryExpressionMangler extends PredicateNameMangler
  {

    BinaryExpressionMangler ( final BinaryExpression expression )
    {
      String operator;
      switch ( expression.getOperator() )
      {
        case EQUAL:
          operator = "EQ";
          break;
        case NOT_EQUAL:
          operator = "NE";
          break;
        case LESS_THAN:
          operator = "LT";
          break;
        case GREATER_THAN:
          operator = "GT";
          break;
        case LESS_THAN_OR_EQUAL:
          operator = "LE";
          break;
        case GREATER_THAN_OR_EQUAL:
          operator = "GE";
          break;
        case AND:
          operator = "AND";
          break;
        case OR:
          operator = "OR";
          break;
        case XOR:
          operator = "XOR";
          break;
        default:
          throw new IllegalArgumentException("Binary operator '" + expression.getOperator() + "' invalid in find predicate");
      }

      setName(openParenthesisMangle
              + createMangler(expression.getLhs()).getName()
              + operator
              + createMangler(expression.getRhs()).getName()
              + closeParenthesisMangle);
    }


  }

  private static class UnaryExpressionMangler extends PredicateNameMangler
  {

    UnaryExpressionMangler ( final UnaryExpression expression )
    {
      String operator;
      switch ( expression.getOperator() )
      {
        case NOT:
          operator = "NOT";
          break;
        default:
          throw new IllegalArgumentException("Unary operator '" + expression.getOperator() + "' invalid in find predicate");
      }

      setName(operator + openParenthesisMangle + createMangler(expression.getRhs()).getName() + closeParenthesisMangle);
    }


  }

  public static PredicateNameMangler createMangler ( final Expression expression )
  {
    if ( expression instanceof BinaryExpression )
    {
      return new BinaryExpressionMangler((BinaryExpression)expression);
    }
    else if ( expression instanceof FindParameterExpression )
    {
      return new SimpleMangler(((FindParameterExpression)expression).getName());
    }
    else if ( expression instanceof FindAttributeNameExpression )
    {
      return new SimpleMangler("masl_" + ((FindAttributeNameExpression)expression).getAttribute().getName() + "_masl");
    }
    else if ( expression instanceof SelectedComponentExpression )
    {
      return new SimpleMangler(createMangler(((SelectedComponentExpression)expression).getPrefix()).getName()
                               + "DOTmasl_"
                               + ((SelectedComponentExpression)expression).getComponent().getName()
                               + "_masl");
    }
    else if ( expression instanceof UnaryExpression )
    {
      return new UnaryExpressionMangler((UnaryExpression)expression);
    }

    throw new IllegalArgumentException("Unrecognised Expression " + expression.getClass() + " : '" + expression + "'");
  }


  
  public String getName ()
  {
    return name;
  }


  private String name;

  void setName ( final String name )
  {
    this.name = name;
  }
}
