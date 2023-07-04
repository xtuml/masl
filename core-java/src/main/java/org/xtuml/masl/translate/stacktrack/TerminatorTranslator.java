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
package org.xtuml.masl.translate.stacktrack;

import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;

import java.util.ArrayList;
import java.util.List;

public class TerminatorTranslator {

    public TerminatorTranslator(final DomainTerminator term) {
        for (final DomainTerminatorService service : term.getServices()) {
            serviceTranslators.add(new ActionTranslator(TerminatorServiceTranslator.getInstance(service)));
        }

    }

    void translate() {

        for (final ActionTranslator serviceTranslator : serviceTranslators) {
            serviceTranslator.translate();
        }
    }

    private final List<ActionTranslator> serviceTranslators = new ArrayList<ActionTranslator>();

}
