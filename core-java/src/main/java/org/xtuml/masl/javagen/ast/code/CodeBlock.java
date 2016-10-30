//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.List;


public interface CodeBlock
    extends Statement, StatementGroup
{

  List<? extends BlockStatement> getStatements ();

}
