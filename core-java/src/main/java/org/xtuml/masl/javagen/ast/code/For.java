//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.List;

import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;


public interface For
    extends Statement
{

  void addStartExpression ( StatementExpression expression );

  void addUpdateExpression ( StatementExpression expression );

  Expression getCondition ();

  List<? extends StatementExpression> getStartExpressions ();

  LocalVariable getVariable ();

  Expression getCollection ();

  Statement getStatement ();

  List<? extends StatementExpression> getUpdateExpressions ();

  void setCollection ( Expression collection );

  void setCondition ( Expression condition );

  void setVariable ( LocalVariable variable );

  void setStatement ( Statement statement );
}
