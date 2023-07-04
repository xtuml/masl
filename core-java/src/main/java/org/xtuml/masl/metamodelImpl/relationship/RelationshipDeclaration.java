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

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;

import java.util.List;

public abstract class RelationshipDeclaration extends Positioned
        implements org.xtuml.masl.metamodel.relationship.RelationshipDeclaration {

    public static Reference createReference(final Domain.Reference domainRef, final String relName) {
        if (domainRef == null || relName == null) {
            return null;
        }

        try {
            final Position
                    position =
                    domainRef.getPosition() == null ? Position.getPosition(relName) : domainRef.getPosition();
            return domainRef.getDomain().getRelationship(relName).getReference(position);

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public class Reference extends Positioned {

        private Reference(final Position position) {
            super(position);
        }

        public RelationshipDeclaration getRelationship() {
            return RelationshipDeclaration.this;
        }

    }

    public Reference getReference(final Position position) {
        return new Reference(position);
    }

    private final String name;
    private final PragmaList pragmas;

    RelationshipDeclaration(final Position position, final Domain domain, final String name, final PragmaList pragmas) {
        super(position);
        this.domain = domain;
        this.pragmas = pragmas;
        this.name = name;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public String getName() {
        return name;
    }

    private final Domain domain;

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "relationship " + getName() + "\tis\t";
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(pragmas);
    }

}
