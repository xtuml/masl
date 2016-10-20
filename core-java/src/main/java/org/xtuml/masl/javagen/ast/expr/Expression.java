//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.types.Type;


public interface Expression
    extends ASTNode
{

  ArrayAccess arrayIndex ( Expression index );

  Assignment assign ( Expression value );

  Assignment assign ( Assignment.Operator operator, Expression value );

  BinaryExpression multiply ( Expression rhs );

  BinaryExpression divide ( Expression rhs );

  BinaryExpression remainder ( Expression rhs );

  BinaryExpression add ( Expression rhs );

  BinaryExpression subtract ( Expression rhs );

  BinaryExpression leftShift ( Expression rhs );

  BinaryExpression rightShift ( Expression rhs );

  BinaryExpression rightShiftUnsigned ( Expression rhs );

  BinaryExpression lessThan ( Expression rhs );

  BinaryExpression greaterThan ( Expression rhs );

  BinaryExpression lessThanOrEqualTo ( Expression rhs );

  BinaryExpression greaterThanOrEqualTo ( Expression rhs );

  BinaryExpression isInstanceof ( Expression rhs );

  BinaryExpression equalTo ( Expression rhs );

  BinaryExpression notEqualTo ( Expression rhs );

  BinaryExpression bitwiseAnd ( Expression rhs );

  BinaryExpression bitwiseOr ( Expression rhs );

  BinaryExpression bitwiseXor ( Expression rhs );

  BinaryExpression and ( Expression rhs );

  BinaryExpression or ( Expression rhs );

  Cast castTo ( Type type );

  Conditional conditional ( Expression trueValue, Expression falseValue );

  FieldAccess dot ( Field field );

  MethodInvocation dot ( Method method, Expression... args );

  ParenthesizedExpression parenthesize ();

  PostfixExpression postIncrement ();

  PostfixExpression postDecrement ();

  PrefixExpression increment ();

  PrefixExpression decrement ();

  UnaryExpression plus ();

  UnaryExpression minus ();

  UnaryExpression not ();

  UnaryExpression complement ();

  ArrayLengthAccess length ();

}
