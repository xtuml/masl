/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.customcode;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.BuildSet;

import java.io.File;

@Alias("CustomCode")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    private final BuildSet buildSet;

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        buildSet = BuildSet.getBuildSet(domain);
    }

    @Override
    public void translate() {
        if (new XMLParser(buildSet).parse()) {
            buildSet.addIncludeDir(new File("../custom"));
            buildSet.addSourceDir(new File("../custom"));
        }
    }
}
