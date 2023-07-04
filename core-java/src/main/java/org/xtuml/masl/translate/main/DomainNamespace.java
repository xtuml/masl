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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.metamodel.domain.Domain;

import java.util.HashMap;
import java.util.Map;

public class DomainNamespace {

    static Map<Domain, Namespace> namespaces = new HashMap<Domain, Namespace>();

    public static Namespace get(final Domain domain) {
        Namespace result = namespaces.get(domain);
        if (result == null) {
            result = new Namespace(Mangler.mangleName(domain));
            namespaces.put(domain, result);
        }
        return result;
    }

}
