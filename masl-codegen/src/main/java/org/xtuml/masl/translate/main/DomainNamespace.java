/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.metamodel.domain.Domain;

import java.util.HashMap;
import java.util.Map;

public class DomainNamespace {

    static Map<Domain, Namespace> namespaces = new HashMap<>();

    public static Namespace get(final Domain domain) {
        Namespace result = namespaces.get(domain);
        if (result == null) {
            result = new Namespace(Mangler.mangleName(domain));
            namespaces.put(domain, result);
        }
        return result;
    }

}
