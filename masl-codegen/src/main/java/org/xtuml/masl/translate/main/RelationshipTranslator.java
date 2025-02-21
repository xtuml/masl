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

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

public class RelationshipTranslator {

    public static RelationshipTranslator getInstance(final RelationshipDeclaration rel) {
        return DomainTranslator.getInstance(rel.getDomain()).getRelationshipTranslator(rel);
    }

    final DomainTranslator domainTranslator;

    public RelationshipTranslator(final RelationshipDeclaration rel, final Expression relationshipId) {
        relationship = rel;
        domainTranslator = DomainTranslator.getInstance(rel.getDomain());
        this.relationshipId = relationshipId;
    }

    public void translateRelationship() {
        if (relationship instanceof SubtypeRelationshipDeclaration) {
            createRelationshipPolymorphicEvents((SubtypeRelationshipDeclaration) relationship);
        }
    }

    private void createRelationshipPolymorphicEvents(final SubtypeRelationshipDeclaration rel) {
        final ObjectTranslator supObj = domainTranslator.getObjectTranslator(rel.getSupertype());

        supObj.addPolymorphism(rel);
    }

    private final RelationshipDeclaration relationship;

    public Expression getRelationshipId() {
        return relationshipId;
    }

    private final Expression relationshipId;

}
