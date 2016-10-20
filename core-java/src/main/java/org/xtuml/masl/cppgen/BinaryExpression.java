//
// File: Operator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Represents a C++ binary expression. A binary expression is an expression
 * where two expressions appear on each side of an operator, for expample
 * {@code a + b}or {@code a == b}.
 */
public class BinaryExpression extends Expression
{

  /**
   * Takes a number of expressions and produces a single compound C++ expression
   * using the supplied operator. So for example parameters of
   * BinaryOperator.PLUS, { ExprA, ExprB, ExprC, ExprD } would result in a
   * single compound expression of the form ExprA + ExprB + ExprC + ExprD
   * 

   *          The operator to be applied to the specified expressions

   *          the expressions that need to be combined.
   * @return a single compound expression built using the supplied operator and
   *         expressions
   */
  public static Expression createCompoundExpression ( final BinaryOperator operator, final Expression... expressions )
  {
    return createCompoundExpression(operator, Arrays.asList(expressions));
  }

  /**
   * Takes a number of expressions and produces a single compound C++ expression
   * using the supplied operator. So for example parameters of
   * BinaryOperator.PLUS, { ExprA, ExprB, ExprC, ExprD } would result in a
   * single compound expression of the form ExprA + ExprB + ExprC + ExprD
   * 

   *          The operator to be applied to the specified expressions

   *          the expressions that need to be combined.
   * @return a single compound expression built using the supplied operator and
   *         expressions
   */
  public static Expression createCompoundExpression ( final BinaryOperator operator, final List<Expression> expressions )
  {
    if ( expressions.size() == 1 )
    {
      // exit condition from this recursive method.
      return expressions.get(0);
    }
    else
    {
      final List<Expression> subExpressionList = expressions.subList(0, expressions.size() - 1);
      final Expression tailExpr = expressions.get(expressions.size() - 1);
      return new BinaryExpression(createCompoundExpression(operator, subExpressionList), operator, tailExpr);
    }
  }

  /**
   * Constructs a binary expression.
   * 

   *          The left hand side expression

   *          The operator to use

   *          The right hand side expression
   */
  public BinaryExpression ( final Expression lhsExpression, final BinaryOperator operator, final Expression rhsExpression )
  {
    this.operator = operator;
    this.lhsExpression = lhsExpression;
    this.rhsExpression = rhsExpression;

  }

  @Override
  boolean isTemplateType ()
  {
    return lhsExpression.isTemplateType() || rhsExpression.isTemplateType();
  }


  @Override
  int getPrecedence ()
  {
    return operator.getPrecedence();
  }

  @Override
  /**
   * {@inheritDoc}
   * <p>
   * The code output will take account of the relative precedence of the
   * operator compared to the precedence of the expression on each side, and
   * also the associativity of the operator when determining whether parentheses
   * are needed. The minimum number of parenthesis necessary to preserve the
   * semantics of the expression will be used in the generated code. Assignment
   * expressions are right associative, that is {@code a=b=c}is equivalent to
   * {@code a=(b=c)}. All other binary expressions are left-associative, that
   * is {@code a+b+c}is equivalent to {@code (a+b)+c}. Precedence is
   * determined according to the table in C++ Programming Language (Third
   * Edition) Stroustrup, Section 6.2.
   */
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    String lhs = lhsExpression.getCode(currentNamespace, alignment);
    String rhs = rhsExpression.getCode(currentNamespace, alignment);

    if ( getPrecedence() < lhsExpression.getPrecedence()
         || (operator.getAssociativity() == BinaryOperator.Associativity.RIGHT && getPrecedence() == lhsExpression.getPrecedence()) )
    {
      lhs = "(" + lhs + ")";
    }

    if ( getPrecedence() < rhsExpression.getPrecedence()
         || (operator.getAssociativity() == BinaryOperator.Associativity.LEFT && getPrecedence() == rhsExpression.getPrecedence()) )
    {
      rhs = "(" + rhs + ")";
    }

    return lhs + operator.getCode() + rhs;
  }

  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    result.addAll(lhsExpression.getForwardDeclarations());
    result.addAll(rhsExpression.getForwardDeclarations());
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    result.addAll(lhsExpression.getIncludes());
    result.addAll(rhsExpression.getIncludes());
    return result;
  }

  /**
   * Gets the expression on the left hand side of the operator
   * 
   * @return the lhs expression
   */
  Expression getLhsExpression ()
  {
    return lhsExpression;
  }

  /**
   * Gets the operator for the expression
   * 
   * @return the operator
   */
  BinaryOperator getOperator ()
  {
    return operator;
  }

  /**
   * Gets the expression on the right hand side of the operator
   * 
   * @return the rhs expression
   */
  Expression getRhsExpression ()
  {
    return rhsExpression;
  }

  /**
   * The left hand side expression
   */
  private final Expression     lhsExpression;

  /**
   * The operator
   */
  private final BinaryOperator operator;

  /**
   * The right hand side expression
   */
  private final Expression     rhsExpression;
}
