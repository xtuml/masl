/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.translate.main.Scope;

public class PragmaTranslator extends CodeTranslator {

    protected PragmaTranslator(final org.xtuml.masl.metamodel.code.PragmaStatement statement,
                               final Scope parentScope,
                               final CodeTranslator parentTranslator) {
        super(statement, parentScope, parentTranslator);
    }

}
