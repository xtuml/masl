//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;


public interface StatementGroup
{

  BlockStatement addStatement ( BlockStatement statement );

  BlockStatement addStatement ( StatementExpression expression );

  BlockStatement addStatement ( LocalVariable declaration );

  BlockStatement addStatement ( TypeDeclaration declaration );

  StatementGroup addGroup ();

}
