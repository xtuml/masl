//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.code;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.Expression;


public interface Switch
    extends Statement
{

  public interface SwitchBlock
      extends ASTNode
  {

    List<? extends Expression> getCaseLabels ();

    void addCaseLabel ( Expression caseLabel );

    boolean isDefault ();

    void setDefault ();

    void addStatement ( BlockStatement statement );

    List<? extends BlockStatement> getStatements ();

  }

  void setDiscriminator ( Expression discriminator );

  Expression getDiscriminator ();

  List<? extends SwitchBlock> getSwitchBlocks ();

  void addSwitchBlock ( SwitchBlock switchBlock );

}
