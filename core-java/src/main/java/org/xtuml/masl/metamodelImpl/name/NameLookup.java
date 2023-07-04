/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
