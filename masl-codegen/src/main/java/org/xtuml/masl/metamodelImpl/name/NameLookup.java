/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.name;

import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

public class NameLookup extends CheckedLookup<Name> {

    public NameLookup(final SemanticErrorCode alreadyDefined, final SemanticErrorCode notFound, final Named parent) {
        super(alreadyDefined, notFound, parent);
    }

    public NameLookup(final SemanticErrorCode alreadyDefined, final SemanticErrorCode notFound) {
        super(alreadyDefined, notFound);
    }

    public NameLookup() {
        super(SemanticErrorCode.NameRedefinition, SemanticErrorCode.NameNotFoundInScope);
    }

    public void addName(final Name name) throws AlreadyDefined {
        put(name.getName(), name);
    }

}
