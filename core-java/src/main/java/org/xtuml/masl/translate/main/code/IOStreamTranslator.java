//
// File: IOStreamTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.StatementGroup;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.metamodel.code.IOStreamStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class IOStreamTranslator extends CodeTranslator
{

  protected IOStreamTranslator ( final IOStreamStatement statement, final Scope parentScope, final CodeTranslator parentTranslator )
  {
    super(statement, parentScope, parentTranslator);
    Expression result = ExpressionTranslator.createTranslator(statement.getStreamName(), getScope()).getReadExpression();

    final StatementGroup varDefs = new StatementGroup();
    final StatementGroup varSetters = new StatementGroup();

    int i = 0;
    for ( final IOStreamStatement.IOExpression argument : statement.getArguments() )
    {
      final ExpressionTranslator rhs = ExpressionTranslator.createTranslator(argument.getExpression(), getScope());

      if ( argument.getType() == IOStreamStatement.Type.OUT )
      {
        result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, rhs.getReadExpression());
      }
      else if ( argument.getType() == IOStreamStatement.Type.LINE_OUT )
      {
        result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, rhs.getReadExpression());
        result = new BinaryExpression(result, BinaryOperator.LEFT_SHIFT, Literal.NEWLINE);
      }
      else
      {
        Expression arg;
        if ( rhs.getWriteFunction() != null )
        {
          final Variable temp = new Variable(Types.getInstance().getType(argument
                                                                                 .getExpression().getType()), "arg_" + ++i);
          varDefs.appendStatement(new VariableDefinitionStatement(temp));
          arg = temp.asExpression();
          varSetters.appendStatement(new ExpressionStatement(rhs.getWriteExpression(temp.asExpression())));
        }
        else
        {
          arg = rhs.getWriteableExpression();
        }

        if ( argument.getType() == IOStreamStatement.Type.IN )
        {
          result = new BinaryExpression(result, BinaryOperator.RIGHT_SHIFT, arg);
        }
        else if ( argument.getType() == IOStreamStatement.Type.LINE_IN )
        {
          result = new Function("getline").asFunctionCall(result, false, arg);
        }
      }
    }

    // code.appendStatement(new ExpressionStatement(rhs));
    if ( varDefs.size() > 0 )
    {
      final CodeBlock codeBlock = new CodeBlock();
      getCode().appendStatement(codeBlock);

      codeBlock.appendStatement(varDefs);
      codeBlock.appendStatement(new ExpressionStatement(result));
      codeBlock.appendStatement(varSetters);
    }
    else
    {
      getCode().appendStatement(new ExpressionStatement(result));
    }
  }


}
