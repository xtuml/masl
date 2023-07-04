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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.SetType;
import org.xtuml.masl.utils.HashCode;

import java.util.Collections;
import java.util.List;

public class NavigationExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.NavigationExpression {

    public static NavigationExpression create(final Position position,
                                              final Expression lhs,
                                              final RelationshipSpecification.Reference relRef,
                                              final Expression whereClause) {
        if (lhs == null || relRef == null) {
            return null;
        }

        return new NavigationExpression(position, lhs, relRef.getRelationshipSpec(), whereClause);
    }

    private final Expression lhs;
    private final RelationshipSpecification relationship;
    private final Expression condition;

    private NavigationExpression(final Position position,
                                 final Expression lhs,
                                 final RelationshipSpecification relationship,
                                 final Expression whereClause) {
        super(position);
        this.lhs = lhs;
        this.relationship = relationship;
        this.condition = whereClause;
    }

    public Expression getCondition() {
        return this.condition;
    }

    @Override
    public Expression getSkeleton() {
        if (condition != null) {
            return condition.getFindSkeleton();
        } else {
            return null;
        }
    }

    @Override
    public List<Expression> getArguments() {
        return Collections.unmodifiableList(condition.getFindArguments());
    }

    @Override
    public Expression getLhs() {
        return this.lhs;
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
        if (lhs.getType().getBasicType() instanceof SetType &&
            relationship.getReverseSpec().getCardinality() == MultiplicityType.ONE) {
            // If back relationship is single valued, then we can continue to
            // guarantee uniqueness
            return SetType.createAnonymous(relationship.getDestinationObject().getType());
        } else if (lhs.getType().getBasicType() instanceof CollectionType) {
            return BagType.createAnonymous(relationship.getDestinationObject().getType());
        } else if (relationship.getCardinality() == MultiplicityType.MANY) {
            // Single instance -> many, so can guarantee uniqueness
            return SetType.createAnonymous(relationship.getDestinationObject().getType());
        } else {
            // single instance -> one
            return relationship.getDestinationObject().getType();
        }
    }

    @Override
    public String toString() {
        return lhs + " -> " + relationship;

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NavigationExpression other = (NavigationExpression) obj;
        if (condition == null) {
            if (other.condition != null) {
                return false;
            }
        } else if (!condition.equals(other.condition)) {
            return false;
        }
        if (lhs == null) {
            if (other.lhs != null) {
                return false;
            }
        } else if (!lhs.equals(other.lhs)) {
            return false;
        }
        if (relationship == null) {
            return other.relationship == null;
        } else
            return relationship.equals(other.relationship);
    }

    @Override
    public int hashCode() {
        return HashCode.makeHash(lhs, relationship, condition);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitNavigationExpression(this, p);
    }

}
