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

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;

import java.util.*;

@Alias("StackTrack")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.<org.xtuml.masl.translate.DomainTranslator>singletonList(mainDomainTranslator);
    }

    @Override
    public void translate() {
        for (final ObjectDeclaration object : domain.getObjects()) {
            objectTranslators.put(object, new ObjectTranslator(object));
        }

        for (final DomainTerminator object : domain.getTerminators()) {
            termTranslators.put(object, new TerminatorTranslator(object));
        }

        for (final ObjectTranslator objectTranslator : objectTranslators.values()) {
            objectTranslator.translate();
        }

        for (final TerminatorTranslator termTranslator : termTranslators.values()) {
            termTranslator.translate();
        }

        for (final DomainService service : domain.getServices()) {
            final ActionTranslator
                    serviceTranslator =
                    new ActionTranslator(mainDomainTranslator.getServiceTranslator(service));
            serviceTranslator.translate();
        }

    }

    ObjectTranslator getObjectTranslator(final ObjectDeclaration object) {
        return objectTranslators.get(object);
    }

    Map<ObjectDeclaration, ObjectTranslator> objectTranslators = new HashMap<ObjectDeclaration, ObjectTranslator>();
    Map<DomainTerminator, TerminatorTranslator> termTranslators = new HashMap<DomainTerminator, TerminatorTranslator>();

    private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

}
