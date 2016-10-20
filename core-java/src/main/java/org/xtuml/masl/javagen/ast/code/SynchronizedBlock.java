//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import org.xtuml.masl.javagen.ast.expr.Expression;


public interface SynchronizedBlock
    extends Statement
{

  Expression getLockExpression ();

  CodeBlock getCodeBlock ();

  void setLockExpression ( Expression expression );

  void setCodeBlock ( CodeBlock block );

}
