/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.translate.DomainTranslator;

import java.util.*;

public abstract class SqlFrameworkTranslator extends DomainTranslator {

    private final Map<ObjectDeclaration, ObjectTranslator> objectTranslators = new LinkedHashMap<>();
    private final Map<RelationshipDeclaration, RelationshipTranslator> relationshipTranslators = new HashMap<>();

    protected SqlFrameworkTranslator(final Domain domain) {
        super(domain);
        mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
    }

    @Override
    public void translate() {
        translateModel(domain);
        translateActions(domain);
    }

    public abstract Library getLibrary();

    protected abstract Database getDatabase();

    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.singletonList(mainDomainTranslator);
    }

    public org.xtuml.masl.translate.main.DomainTranslator getMainDomainTranslator() {
        return mainDomainTranslator;
    }

    private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

    org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator(final ObjectDeclaration object) {
        return getMainDomainTranslator().getObjectTranslator(object);
    }

    ObjectTranslator getObjectTranslator(final ObjectDeclaration objectDecl) {
        return objectTranslators.get(objectDecl);
    }

    RelationshipTranslator getRelationshipTranslator(final RelationshipDeclaration relDecl) {
        return relationshipTranslators.get(relDecl);
    }

    private void translateModel(final Domain domain) {
        // Create all the objectTranslators, before undertaking any
        // actual translation, as transaltors by be dependent upon
        // one another because of constructs like relationships.
        for (final ObjectDeclaration object : domain.getObjects()) {
            final ObjectTranslator objectTranslator = new ObjectTranslator(this, object);
            objectTranslators.put(object, objectTranslator);
            for (final RelationshipDeclaration relationship : domain.getRelationships()) {
                objectTranslator.addRelationship(relationship);
            }
        }

        // Create and Run all the relationshipTranslators
        for (final RelationshipDeclaration relationshipDecl : domain.getRelationships()) {
            final RelationshipTranslator relationshipTranslator = new RelationshipTranslator(this, relationshipDecl);
            relationshipTranslators.put(relationshipDecl, relationshipTranslator);
        }

        // Run all the object Translators
        for (final Map.Entry<ObjectDeclaration, ObjectTranslator> currentTranslator : objectTranslators.entrySet()) {
            currentTranslator.getValue().translateModel();
        }
    }

    private void translateActions(final Domain domain) {
        for (final ObjectDeclaration objectDecl : domain.getObjects()) {
            final ObjectTranslator objectTranslator = objectTranslators.get(objectDecl);
            objectTranslator.translateActions();
        }
    }
}
