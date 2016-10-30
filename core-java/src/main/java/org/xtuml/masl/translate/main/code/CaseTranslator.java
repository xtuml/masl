//
// File: CaseTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.BreakStatement;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.SwitchStatement;
import org.xtuml.masl.cppgen.ThrowStatement;
import org.xtuml.masl.metamodel.expression.EnumerateLiteral;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.EnumerationTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;



public class CaseTranslator extends CodeTranslator
{

  protected CaseTranslator ( final org.xtuml.masl.metamodel.code.CaseStatement maslCase,
                             final Scope parentScope,
                             final CodeTranslator parentTranslator )
  {
    super(maslCase, parentScope, parentTranslator);

    final List<SwitchStatement.CaseCondition> alternatives = new ArrayList<SwitchStatement.CaseCondition>();

    Statement defaultStatement = new ThrowStatement(Architecture.programError.callConstructor(Literal
                                                                                                     .createStringLiteral("Invalid case condition")));

    EnumerationTranslator enumTranslator = null;

    for ( final org.xtuml.masl.metamodel.code.CaseStatement.Alternative branch : maslCase.getAlternatives() )
    {

      final CodeBlock caseCode = new CodeBlock();
      for ( final org.xtuml.masl.metamodel.code.Statement maslStatement : branch.getStatements() )
      {
        final CodeTranslator translator = createChildTranslator(maslStatement);
        final CodeBlock translation = translator.getFullCode();
        caseCode.appendStatement(translation);
      }

      if ( branch.getConditions() == null )
      {
        defaultStatement = caseCode;
      }
      else
      {
        for ( final org.xtuml.masl.metamodel.expression.Expression maslCondition : branch.getConditions() )
        {
          // Special case for enumerate literals, as we can't select on a class,
          // so need to peek inside and get the actual enumerate, which is not
          // what we want to do anywhere else.
          Expression condition;
          if ( enumTranslator != null || maslCondition instanceof EnumerateLiteral )
          {
            final EnumerateLiteral enumerator = (EnumerateLiteral)maslCondition;

            if ( enumTranslator == null )
            {
              // Force creation of the enumerate type, as it might not have been
              // used yet, eg if the literal is just used in a function/service
              // call.
              Types.getInstance().getType(enumerator.getType());

              enumTranslator = Types.getInstance().getEnumerateTranslator(enumerator.getType()
                                                                                    .getTypeDeclaration());
            }
            condition = enumTranslator.getEnumeratorIndex(enumerator.getValue());
          }
          else
          {
            condition = ExpressionTranslator.createTranslator(maslCondition, getScope()).getReadExpression();
          }
          if ( maslCondition != branch.getConditions().get(branch.getConditions().size() - 1) )
          {
            final SwitchStatement.CaseCondition alternative = new SwitchStatement.CaseCondition(condition, null);
            alternatives.add(alternative);
          }
          else
          {
            caseCode.appendStatement(new BreakStatement());
            final SwitchStatement.CaseCondition alternative = new SwitchStatement.CaseCondition(condition, caseCode);
            alternatives.add(alternative);
          }
        }
      }
    }

    Expression discriminator = ExpressionTranslator.createTranslator(maslCase.getDiscriminator(), getScope()).getReadExpression();

    if ( enumTranslator != null )
    {
      discriminator = enumTranslator.getGetIndex().asFunctionCall(discriminator, false);
    }

    getCode().appendStatement(new SwitchStatement(discriminator, alternatives, defaultStatement));
  }
}
