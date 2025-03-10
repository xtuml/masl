/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.domain;

import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

import java.util.Collection;

public class Domains {

    private static final CheckedLookup<Domain>
            domains =
            new CheckedLookup<>(SemanticErrorCode.DomainAlreadyDefined, SemanticErrorCode.DomainNotFound);

    public static void addDomain(final Domain domain) {
        if (domain == null) {
            return;
        }

        try {
            domains.put(domain.getName(), domain);
        } catch (final AlreadyDefined e) {
            e.report();
        }

    }

    public static Domain findDomain(final String name) {
        return domains.find(name);
    }

    public static Domain getDomain(final String name) throws NotFound {
        return domains.get(name);
    }

    public static Collection<Domain> getDomains() {
        return domains.asList();
    }
}
