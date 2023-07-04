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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.SetType;

import java.util.List;

public class CorrelatedNavExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.CorrelatedNavExpression {

    public static CorrelatedNavExpression create(final Position position,
                                                 final Expression lhs,
                                                 final Expression rhs,
                                                 final RelationshipSpecification.Reference relRef) {
        if (lhs == null || rhs == null || relRef == null) {
            return null;
        }

        try {
            final ObjectDeclaration lhsObj = ObjectDeclaration.getObject(lhs, true);
            final ObjectDeclaration rhsObj = ObjectDeclaration.getObject(rhs, true);

            final RelationshipSpecification leftToAssoc = relRef.getRelationshipSpec();

            final RelationshipDeclaration rel = leftToAssoc.getRelationship();
            if (!(rel instanceof AssociativeRelationshipDeclaration assocRel)) {
                throw new SemanticError(SemanticErrorCode.CorrelateNotAssociative, position, rel.getName());
            }

            if ((assocRel.getLeftObject() == lhsObj && assocRel.getRightObject() != rhsObj) ||
                (assocRel.getRightObject() == rhsObj && assocRel.getLeftObject() != lhsObj)) {
                throw new SemanticError(SemanticErrorCode.CorrelateObjsIncorrect,
                                        position,
                                        lhsObj.getName(),
                                        rhsObj.getName(),
                                        assocRel.getName());
            }

            return new CorrelatedNavExpression(position, lhs, rhs, leftToAssoc.getNonAssocSpec());
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private final Expression lhs;
    private final Expression rhs;
    private final RelationshipSpecification relationship;

    public CorrelatedNavExpression(final Position position,
                                   final Expression lhs,
                                   final Expression rhs,
                                   final RelationshipSpecification relationship) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
        this.relationship = relationship;
    }

    @Override
    public Expression getLhs() {
        return this.lhs;
    }

    @Override
    public Expression getRhs() {
        return this.rhs;
    }

    @Override
    public RelationshipSpecification getRelationship() {
        return this.relationship;
    }

    /**
     * @return
     * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
     */
    @Override
    public BasicType getType() {
        if (lhs.getType().getBasicType() instanceof InstanceType &&
            rhs.getType().getBasicType() instanceof InstanceType) {
            return relationship.getAssocSpec().getDestinationObject().getType();
        } else if ((lhs.getType().getBasicType() instanceof SetType ||
                    lhs.getType().getBasicType() instanceof InstanceType) &&
                   (rhs.getType().getBasicType() instanceof SetType ||
                    rhs.getType().getBasicType() instanceof InstanceType)) {
            return SetType.createAnonymous(relationship.getAssocSpec().getDestinationObject().getType());
        } else {
            return BagType.createAnonymous(relationship.getAssocSpec().getDestinationObject().getType());
        }
    }

    @Override
    public String toString() {
        return lhs + " with " + rhs + " -> " + relationship.getAssocSpec();

    }

    @Override
    public int hashCode() {
        return lhs.hashCode() ^ rhs.hashCode() ^ relationship.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CorrelatedNavExpression obj2) {

            return lhs.equals(obj2.lhs) && rhs.equals(obj2.rhs) && relationship.equals(obj2.relationship);
        } else {
            return false;
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCorrelatedNavExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs, rhs);
    }

}
