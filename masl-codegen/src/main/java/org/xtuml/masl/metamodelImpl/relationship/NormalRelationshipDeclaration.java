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
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

import java.util.List;

public class NormalRelationshipDeclaration extends RelationshipDeclaration
        implements org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration {

    private final HalfRelationship leftToRight;
    private final HalfRelationship rightToLeft;

    public static void create(final Position position,
                              final Domain domain,
                              final String name,
                              final HalfRelationship leftToRight,
                              final HalfRelationship rightToLeft,
                              final PragmaList pragmas) {
        if (domain == null || leftToRight == null || rightToLeft == null) {
            return;
        }

        try {
            domain.addRelationship(new NormalRelationshipDeclaration(position,
                                                                     domain,
                                                                     name,
                                                                     leftToRight,
                                                                     rightToLeft,
                                                                     pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    private NormalRelationshipDeclaration(final Position position,
                                          final Domain domain,
                                          final String name,
                                          final HalfRelationship leftToRight,
                                          final HalfRelationship rightToLeft,
                                          final PragmaList pragmas) {
        super(position, domain, name, pragmas);
        this.leftToRight = leftToRight;
        this.rightToLeft = rightToLeft;

        this.leftToRightSpec = new RelationshipSpecification(this, leftToRight);
        this.rightToLeftSpec = new RelationshipSpecification(this, rightToLeft);

        leftToRightSpec.setReverseSpec(rightToLeftSpec);
        rightToLeftSpec.setReverseSpec(leftToRightSpec);

        if (leftToRight.getMult() == MultiplicityType.MANY) {
            rightToLeftSpec.setRequiresFormalising();
        } else if (rightToLeft.getMult() == MultiplicityType.MANY) {
            leftToRightSpec.setRequiresFormalising();
        }

        getLeftObject().addRelationship(getLeftToRightSpec());
        getRightObject().addRelationship(getRightToLeftSpec());

    }

    private final RelationshipSpecification leftToRightSpec;
    private final RelationshipSpecification rightToLeftSpec;

    @Override
    public RelationshipSpecification getLeftToRightSpec() {
        return leftToRightSpec;
    }

    @Override
    public RelationshipSpecification getRightToLeftSpec() {
        return rightToLeftSpec;
    }

    @Override
    public ObjectDeclaration getLeftObject() {
        return rightToLeft.getToObject().getObject();
    }

    @Override
    public String getRightRole() {
        return leftToRight.getRole();
    }

    @Override
    public org.xtuml.masl.metamodel.relationship.MultiplicityType getRightMult() {
        return leftToRight.getMult().getType();
    }

    @Override
    public boolean getRightConditional() {
        return leftToRight.isConditional();
    }

    @Override
    public ObjectDeclaration getRightObject() {
        return leftToRight.getToObject().getObject();
    }

    @Override
    public String getLeftRole() {
        return rightToLeft.getRole();
    }

    @Override
    public org.xtuml.masl.metamodel.relationship.MultiplicityType getLeftMult() {
        return rightToLeft.getMult().getType();
    }

    @Override
    public boolean getLeftConditional() {
        return rightToLeft.isConditional();
    }

    @Override
    public String toString() {
        return super.toString() + leftToRight + "\n" + "\t\t" + rightToLeft + ";\n" + getPragmas();
    }

    boolean isLeftToRight(final ObjectDeclaration source, final RelationshipSpecification spec) {
        if (getRightRole().equals(spec.getRole()) &&
            getRightObject() == spec.getDestinationObject() &&
            getLeftObject() == source) {
            return true;
        } else if (getLeftRole().equals(spec.getRole()) &&
                   getLeftObject() == spec.getDestinationObject() &&
                   getRightObject() == source) {
            return false;
        } else {
            throw new IllegalStateException(source + "->" + spec + " not found on " + this);
        }

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.vistNormalRelationshipDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(super.children(), leftToRightSpec, rightToLeftSpec);
    }

}
