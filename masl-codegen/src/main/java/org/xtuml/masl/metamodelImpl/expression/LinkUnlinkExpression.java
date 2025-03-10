/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodelImpl.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.SetType;
import org.xtuml.masl.utils.HashCode;

import java.util.List;
import java.util.Objects;

public class LinkUnlinkExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.LinkUnlinkExpression {

    public static LinkUnlinkExpression create(final Position position,
                                              final Type type,
                                              final Expression lhs,
                                              final RelationshipSpecification.Reference relationship,
                                              final Expression rhs) {
        if (type == null || lhs == null || relationship == null) {
            return null;
        }

        try {
            return new LinkUnlinkExpression(position, type, lhs, relationship, rhs);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public static class Type {

        private final String mainText;
        private final org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type type;

        private Type(final String mainText, final org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type type) {
            this.mainText = mainText;
            this.type = type;
        }

        public String getMainText() {
            return mainText;
        }

        public org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return mainText;
        }
    }

    public static Type LINK = new Type("link", org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type.LINK);
    public static Type
            UNLINK =
            new Type("unlink", org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type.UNLINK);

    private final Expression lhs;
    private final RelationshipSpecification relationship;
    private final Expression rhs;
    private final Type type;
    private final boolean isCollection;

    private LinkUnlinkExpression(final Position position,
                                 final Type type,
                                 final Expression lhs,
                                 final RelationshipSpecification.Reference relRef,
                                 final Expression rhs) throws SemanticError {
        super(position);
        this.type = type;
        this.lhs = lhs;
        this.rhs = rhs;

        lhsObj = ObjectDeclaration.getObject(lhs, true);

        final RelationshipSpecification relationship = relRef.getRelationshipSpec();

        // First check that statement has all the correct information supplied
        if (!(relationship.getRelationship() instanceof AssociativeRelationshipDeclaration)) {
            throw new SemanticError(SemanticErrorCode.AssociativeRelationshipRequired, relRef.getPosition());
        } else if (rhs == null && type == LINK) {
            throw new SemanticError(SemanticErrorCode.LinkMustSupplyRhs, relRef.getPosition());
        } else if (relationship.isFromAssociative()) {
            throw new SemanticError(SemanticErrorCode.NoLinkFromAssoc, lhs.getPosition());
        } else if (relationship.isToAssociative()) {
            throw new SemanticError(SemanticErrorCode.NoLinkToAssoc, relRef.getPosition());
        }

        // Check that the associative type is able to be created implicitly. Must be
        // able to deduce all identifiers from their referentials
        if (type == LINK) {
            if (relationship.getAssocSpec().getDestinationObject().hasCurrentState()) {
                throw new SemanticError(SemanticErrorCode.CannotDeduceCurrentState,
                                        relRef.getPosition(),
                                        relationship.getAssocSpec().getDestinationObject().getName());
            }
            for (final AttributeDeclaration att : relationship.getAssocSpec().getDestinationObject().getAttributes()) {
                if (att.isIdentifier() && !att.isUnique()) {
                    boolean ok = false;
                    for (final ReferentialAttributeDefinition refAttDef : att.getRefAttDefs()) {
                        if (refAttDef.getDestinationAttribute().getParentObject() == lhsObj ||
                            refAttDef.getDestinationAttribute().getParentObject() ==
                            relationship.getDestinationObject()) {
                            ok = true;
                            break;
                        }
                    }

                    if (!ok) {
                        throw new SemanticError(SemanticErrorCode.CannotDeduceAssocIdentifier,
                                                relRef.getPosition(),
                                                relationship.getAssocSpec().getDestinationObject().getName(),
                                                att.getName());
                    }
                }
            }
        }

        if (rhs != null) {
            // Get the lhs obj again, but with correct cardinality passed through.
            // This is the easiest way to ensure the correct error messages are
            // raised.
            ObjectDeclaration.getObject(lhs, relationship.getReverseSpec().getCardinality() == MultiplicityType.MANY);

            rhsObj = ObjectDeclaration.getObject(rhs, relationship.getCardinality() == MultiplicityType.MANY);
            if (relationship.getDestinationObject() != rhsObj) {
                throw new SemanticError(SemanticErrorCode.ExpectedInstanceOfExpression,
                                        rhs.getPosition(),
                                        relationship.getDestinationObject().getName(),
                                        rhsObj.getName());
            }
            this.isCollection = lhs.getType().isCollection() || rhs.getType().isCollection();
        } else {
            rhsObj = null;
            this.isCollection = lhs.getType().isCollection() || relationship.getCardinality() == MultiplicityType.MANY;
        }

        this.relationship = relationship;
    }

    @Override
    public Expression getLhs() {
        return this.lhs;
    }

    @Override
    public RelationshipSpecification getRelationship() {
        return this.relationship;
    }

    @Override
    public Expression getRhs() {
        return this.rhs;
    }

    @Override
    public org.xtuml.masl.metamodel.expression.LinkUnlinkExpression.Type getLinkType() {
        return this.type.getType();
    }

    @Override
    public String toString() {
        return type.getMainText() + " " + lhs + " " + relationship + (rhs == null ? "" : " " + rhs) + ";";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LinkUnlinkExpression obj2)) {
            return false;
        } else {

            return type.equals(obj2.type) &&
                   lhs.equals(obj2.lhs) &&
                   relationship.equals(obj2.relationship) &&
                   (Objects.equals(rhs, obj2.rhs));
        }
    }

    @Override
    public int hashCode() {
        return HashCode.makeHash(type, lhs, relationship, rhs);
    }

    @Override
    public BasicType getType() {
        final InstanceType
                instance =
                InstanceType.createAnonymous(((AssociativeRelationshipDeclaration) relationship.getRelationship()).getAssocObject());
        return isCollection ? SetType.createAnonymous(instance) : instance;
    }

    private final ObjectDeclaration lhsObj;
    private final ObjectDeclaration rhsObj;

    @Override
    public ObjectDeclaration getLhsObject() {
        return lhsObj;
    }

    @Override
    public ObjectDeclaration getRhsObject() {
        return rhsObj;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitLinkUnlinkExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs, rhs);
    }

}
