/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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

    private final List<ActionTranslator> serviceTranslators = new ArrayList<>();

}
