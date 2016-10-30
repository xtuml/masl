//
// File: PragmaTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.translate.main.Scope;



public class PragmaTranslator extends CodeTranslator
{

  protected PragmaTranslator ( final org.xtuml.masl.metamodel.code.PragmaStatement statement,
                               final Scope parentScope,
                               final CodeTranslator parentTranslator )
  {
    super(statement, parentScope, parentTranslator);
  }


}
