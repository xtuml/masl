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
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;

import java.util.List;

public final class ExceptionDeclaration extends Positioned
        implements org.xtuml.masl.metamodel.exception.ExceptionDeclaration {

    public class Reference extends ExceptionReference
            implements org.xtuml.masl.metamodel.exception.UserDefinedException {

        private Reference(final Position position) {
            super(position);
        }

        @Override
        public String getName() {
            return ExceptionDeclaration.this.getName();
        }

        @Override
        public ExceptionDeclaration getException() {
            return ExceptionDeclaration.this;
        }

        @Override
        public String toString() {
            return ExceptionDeclaration.this.getDomain().getName() + "::" + ExceptionDeclaration.this.getName();
        }

    }

    public Reference getReference(final Position position) {
        return new Reference(position);
    }

    private final String name;
    private final Visibility visibility;
    private final PragmaList pragmas;

    public ExceptionDeclaration(final Position position,
                                final Domain domain,
                                final String name,
                                final Visibility visibility,
                                final PragmaList pragmas) {
        super(position);
        this.domain = domain;
        this.name = name;
        this.visibility = visibility;
        this.pragmas = pragmas;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public org.xtuml.masl.metamodel.common.Visibility getVisibility() {
        return visibility.getVisibility();
    }

    private final Domain domain;

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return visibility + (visibility.toString().equals("") ? "" : " ") + "exception\t" + name + ";\n" + pragmas;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitExceptionDeclaration(this);
    }

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(pragmas);
    }

}
