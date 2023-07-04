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
package org.xtuml.masl.metamodelImpl.relationship;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;

public class RelationshipSpecification implements org.xtuml.masl.metamodel.relationship.RelationshipSpecification {

    public class Reference extends Positioned {

        private Reference(final Position position) {
            super(position);
        }

        public RelationshipSpecification getRelationshipSpec() {
            return RelationshipSpecification.this;
        }

    }

    public Reference getReference(final Position position) {
        return new Reference(position);
    }

    private final RelationshipDeclaration relationship;
    private final ObjectDeclaration fromObject;
    private final boolean conditional;
    private final MultiplicityType mult;
    private final String role;
    private final ObjectDeclaration toObject;

    public static Reference createReference(final Expression lhs,
                                            final RelationshipDeclaration.Reference relRef,
                                            final String role,
                                            final ObjectNameExpression objRef,
                                            final boolean allowToAssoc,
                                            final boolean forceToAssoc) {
        if (lhs == null || relRef == null) {
            return null;
        }

        try {
            ObjectDeclaration object;
            if (lhs instanceof ObjectNameExpression) {
                object = ((ObjectNameExpression) lhs).getObject();
            } else {
                object = ObjectDeclaration.getObject(lhs, true);
            }

            return object.getRelationshipSpec(relRef,
                                              role,
                                              objRef,
                                              allowToAssoc,
                                              forceToAssoc).getReference(relRef.getPosition());
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public RelationshipSpecification(final RelationshipDeclaration relationship, final HalfRelationship half) {
        this(relationship,
             half.getFromObject().getObject(),
             half.isConditional(),
             half.getRole(),
             half.getMult(),
             half.getToObject().getObject());
    }

    private RelationshipSpecification reverseSpec = null;

    public void setReverseSpec(final RelationshipSpecification reverseSpec) {
        this.reverseSpec = reverseSpec;
    }

    @Override
    public RelationshipSpecification getReverseSpec() {
        return reverseSpec;
    }

    private RelationshipSpecification assocSpec = null;

    public void setAssocSpec(final RelationshipSpecification assocSpec) {
        this.assocSpec = assocSpec;
    }

    @Override
    public RelationshipSpecification getAssocSpec() {
        return assocSpec;
    }

    private RelationshipSpecification nonAssocSpec = null;

    public void setNonAssocSpec(final RelationshipSpecification nonAssocSpec) {
        this.nonAssocSpec = nonAssocSpec;
    }

    @Override
    public RelationshipSpecification getNonAssocSpec() {
        return nonAssocSpec;
    }

    public RelationshipSpecification(final RelationshipDeclaration relationship,
                                     final ObjectDeclaration fromObject,
                                     final boolean conditional,
                                     final String role,
                                     final MultiplicityType mult,
                                     final ObjectDeclaration toObject) {
        this.relationship = relationship;
        this.fromObject = fromObject;
        this.conditional = conditional;
        this.role = role;
        this.mult = mult;
        this.toObject = toObject;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public String toString() {
        return relationship.getName() + (role == null ? "" : "." + role) + "." + toObject.getName();
    }

    @Override
    public RelationshipDeclaration getRelationship() {
        return relationship;
    }

    @Override
    public ObjectDeclaration getDestinationObject() {
        return toObject;
    }

    @Override
    public ObjectDeclaration getFromObject() {
        return fromObject;
    }

    @Override
    public org.xtuml.masl.metamodel.relationship.MultiplicityType getCardinality() {
        return mult.getType();
    }

    @Override
    public boolean getConditional() {
        return conditional;
    }

    @Override
    public boolean isToAssociative() {
        final RelationshipDeclaration dec = getRelationship();
        return dec instanceof AssociativeRelationshipDeclaration &&
               ((AssociativeRelationshipDeclaration) dec).getAssocObject() == getDestinationObject();
    }

    @Override
    public boolean isFromAssociative() {
        final RelationshipDeclaration dec = getRelationship();
        return dec instanceof AssociativeRelationshipDeclaration &&
               ((AssociativeRelationshipDeclaration) dec).getAssocObject() == getFromObject();
    }

    public void setFormalisingEnd() {
        isFormalisingEnd = true;
    }

    @Override
    public boolean isFormalisingEnd() {
        return isFormalisingEnd;
    }

    private boolean isFormalisingEnd = false;

    public void setRequiresFormalising() {
        requiresFormalising = true;
        setFormalisingEnd();
    }

    public boolean requiresFormalising() {
        return requiresFormalising;
    }

    private boolean requiresFormalising = false;

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.vistRelationshipSpecification(this, p);
    }

}
