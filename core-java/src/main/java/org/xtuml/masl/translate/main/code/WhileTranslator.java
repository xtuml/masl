//
// File: WhileTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Label;
import org.xtuml.masl.cppgen.LabelStatement;
import org.xtuml.masl.cppgen.WhileStatement;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class WhileTranslator extends CodeTranslator
{

  protected WhileTranslator ( final org.xtuml.masl.metamodel.code.WhileStatement maslWhile,
                              final Scope parentScope,
                              final CodeTranslator parentTranslator )
  {
    super(maslWhile, parentScope, parentTranslator);
    final CodeBlock whileCode = new CodeBlock();

    final Expression condition = ExpressionTranslator.createTranslator(maslWhile.getCondition(), getScope()).getReadExpression();

    getCode().appendStatement(new WhileStatement(condition, whileCode));

    for ( final org.xtuml.masl.metamodel.code.Statement maslStatement : maslWhile.getStatements() )
    {
      final CodeTranslator translator = createChildTranslator(maslStatement);
      final CodeBlock translation = translator.getFullCode();
      whileCode.appendStatement(translation);
    }

  }

  Label getEndOfLoopLabel ()
  {
    if ( label == null )
    {
      // Need a label at the end of the loop so that a masl Exit statement will
      // know where to go to. See ExitStatement for why we can't just use break
      // to
      // do this;
      label = new Label();
      getCode().appendStatement(new LabelStatement(label));
    }
    return label;
  }


  private Label label;


}
