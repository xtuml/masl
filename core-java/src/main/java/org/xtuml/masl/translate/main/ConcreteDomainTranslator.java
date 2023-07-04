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

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.main.object.ConcreteObjectTranslator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ConcreteDomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    private final Map<ObjectDeclaration, ConcreteObjectTranslator> objectTranslators = new HashMap<>();

    protected ConcreteDomainTranslator(final Domain domain) {
        super(domain);
        mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    }

    protected final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.singletonList(mainDomainTranslator);
    }

    protected abstract ConcreteObjectTranslator createTranslator(ObjectDeclaration object);

    protected abstract void translateAuxiliaryFiles(Domain domain);

    protected abstract String getLibNameSuffix();

    protected abstract FileGroup getLibrary();

    @Override
    public void translate() {
        for (final ObjectDeclaration object : domain.getObjects()) {
            final ConcreteObjectTranslator objectTranslator = createTranslator(object);
            objectTranslator.translate();
            objectTranslators.put(object, objectTranslator);
        }

        for (final RelationshipDeclaration relationship : domain.getRelationships()) {
            translateRelationship(relationship);
        }

        for (final ObjectDeclaration object : domain.getObjects()) {
            getObjectTranslator(object).translateRelationships();
        }

        translateAuxiliaryFiles(domain);

    }

    public void translateRelationship(final RelationshipDeclaration relationship) {
        if (relationship instanceof NormalRelationshipDeclaration) {
            createRelationship((NormalRelationshipDeclaration) relationship);
        } else if (relationship instanceof AssociativeRelationshipDeclaration) {
            createRelationship((AssociativeRelationshipDeclaration) relationship);
        } else if (relationship instanceof SubtypeRelationshipDeclaration) {
            createRelationship((SubtypeRelationshipDeclaration) relationship);
        }
    }

    private void createRelationship(final AssociativeRelationshipDeclaration rel) {
        final ConcreteObjectTranslator leftObj = getObjectTranslator(rel.getLeftObject());
        final ConcreteObjectTranslator rightObj = getObjectTranslator(rel.getRightObject());
        final ConcreteObjectTranslator assocObj = getObjectTranslator(rel.getAssocObject());

        leftObj.addRelationship(rel.getLeftToRightSpec(), rel.getLeftToAssocSpec());
        rightObj.addRelationship(rel.getRightToLeftSpec(), rel.getRightToAssocSpec());

        assocObj.addRelationship(rel.getAssocToLeftSpec(), null);
        assocObj.addRelationship(rel.getAssocToRightSpec(), null);

    }

    private void createRelationship(final NormalRelationshipDeclaration rel) {
        final ConcreteObjectTranslator leftObj = getObjectTranslator(rel.getLeftObject());
        final ConcreteObjectTranslator rightObj = getObjectTranslator(rel.getRightObject());

        leftObj.addRelationship(rel.getLeftToRightSpec(), null);
        rightObj.addRelationship(rel.getRightToLeftSpec(), null);
    }

    private void createRelationship(final SubtypeRelationshipDeclaration rel) {
        final ConcreteObjectTranslator supObj = getObjectTranslator(rel.getSupertype());

        for (final ObjectDeclaration subType : rel.getSubtypes()) {
            final ConcreteObjectTranslator subObj = getObjectTranslator(subType);

            supObj.addRelationship(rel.getSuperToSubSpec(subType), null);
            subObj.addRelationship(rel.getSubToSuperSpec(subType), null);

        }
    }

    public org.xtuml.masl.translate.main.DomainTranslator getMainDomainTranslator() {
        return mainDomainTranslator;
    }

    public ConcreteObjectTranslator getObjectTranslator(final ObjectDeclaration dec) {
        return objectTranslators.get(dec);
    }
}
