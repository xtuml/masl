/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.relationship;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

import java.util.*;

public class SubtypeRelationshipDeclaration extends RelationshipDeclaration
        implements org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration {

    private final ObjectDeclaration supertype;
    private final List<ObjectDeclaration> subtypes;

    public static void create(final Position position,
                              final Domain domain,
                              final String name,
                              final ObjectNameExpression supertype,
                              final List<ObjectNameExpression> subtypes,
                              final PragmaList pragmas) {

        if (domain == null || supertype == null) {
            return;
        }

        final List<ObjectDeclaration> subtypeObjs = new ArrayList<>();
        for (final ObjectNameExpression subtype : subtypes) {
            if (subtype != null) {
                subtypeObjs.add(subtype.getObject());
            }
        }

        try {
            domain.addRelationship(new SubtypeRelationshipDeclaration(position,
                                                                      domain,
                                                                      name,
                                                                      supertype.getObject(),
                                                                      subtypeObjs,
                                                                      pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    private SubtypeRelationshipDeclaration(final Position position,
                                           final Domain domain,
                                           final String name,
                                           final ObjectDeclaration supertype,
                                           final List<ObjectDeclaration> subtypes,
                                           final PragmaList pragmas) {
        super(position, domain, name, pragmas);
        this.supertype = supertype;
        this.subtypes = subtypes;
        for (final ObjectDeclaration subtype : subtypes) {
            final RelationshipSpecification
                    subToSuper =
                    new RelationshipSpecification(this, subtype, false, null, MultiplicityType.ONE, supertype);
            final RelationshipSpecification
                    superToSub =
                    new RelationshipSpecification(this, supertype, true, null, MultiplicityType.ONE, subtype);
            subToSuper.setRequiresFormalising();
            this.subToSuperSpecs.put(subtype, subToSuper);
            this.superToSubSpecs.put(subtype, superToSub);

            subToSuper.setReverseSpec(superToSub);
            superToSub.setReverseSpec(subToSuper);

            subtype.addRelationship(subToSuper);
            supertype.addRelationship(superToSub);

            subtype.addSupertype(supertype);

        }
    }

    private final Map<org.xtuml.masl.metamodel.object.ObjectDeclaration, RelationshipSpecification>
            subToSuperSpecs =
            new HashMap<>();
    private final Map<org.xtuml.masl.metamodel.object.ObjectDeclaration, RelationshipSpecification>
            superToSubSpecs =
            new HashMap<>();

    @Override
    public RelationshipSpecification getSubToSuperSpec(final org.xtuml.masl.metamodel.object.ObjectDeclaration subtype) {
        return subToSuperSpecs.get(subtype);
    }

    @Override
    public RelationshipSpecification getSuperToSubSpec(final org.xtuml.masl.metamodel.object.ObjectDeclaration subtype) {
        return superToSubSpecs.get(subtype);
    }

    @Override
    public ObjectDeclaration getSupertype() {
        return supertype;
    }

    @Override
    public List<ObjectDeclaration> getSubtypes() {
        final List<ObjectDeclaration> result = new ArrayList<>(subtypes.size());
        for (final ObjectDeclaration subtype : subtypes) {
            result.add(subtype);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        final List<String> subNames = new ArrayList<>();
        for (final ObjectDeclaration subtype : subtypes) {
            subNames.add(subtype.getName());
        }
        return super.toString() +
               supertype.getName() +
               " is_a (\t" +
               org.xtuml.masl.utils.TextUtils.formatList(subNames, "", ",\n\t\t\t", "") +
               " );\n" +
               getPragmas();
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.vistSubtypeRelationshipDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), subToSuperSpecs.values(), superToSubSpecs.values());
    }

}
