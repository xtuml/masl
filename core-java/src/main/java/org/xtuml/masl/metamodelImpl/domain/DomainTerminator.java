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
package org.xtuml.masl.metamodelImpl.domain;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.NotFoundOnParent;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.TerminatorNameExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.project.ProjectTerminatorService;
import org.xtuml.masl.metamodelImpl.type.TypeDefinition;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DomainTerminator extends Name implements org.xtuml.masl.metamodel.domain.DomainTerminator {

    public class ServiceOverload extends org.xtuml.masl.metamodelImpl.common.ServiceOverload<DomainTerminatorService> {

        public ServiceOverload(final String name) {
            super(name, SemanticErrorCode.ServiceAlreadyDefinedOnTerminator);

        }

        @Override
        public ServiceExpression getReference(final Position position) {
            return new ServiceExpression(position, this);
        }

    }

    public static class ServiceExpression
            extends org.xtuml.masl.metamodelImpl.expression.ServiceExpression<DomainTerminatorService>
            implements org.xtuml.masl.metamodel.expression.TerminatorServiceExpression {

        public ServiceExpression(final Position position, final ServiceOverload overload) {
            super(position, overload);
        }

    }

    public static DomainTerminator create(final Position position,
                                          final Domain domain,
                                          final String name,
                                          final PragmaList pragmas) {
        if (domain == null || name == null) {
            return null;
        }
        try {
            final DomainTerminator term = new DomainTerminator(position, domain, name, pragmas);
            domain.addTerminator(term);
            return term;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    public static Expression createServiceExpression(final Position position, final Expression lhs, final String name) {
        if (lhs == null || name == null) {
            return null;
        }

        try {

            final TypeDefinition type = lhs.getType().getBasicType().getDefinedType();
            if (lhs instanceof TerminatorNameExpression termName) {
                return termName.getTerminator().services.get(name).getReference(position);
            } else {
                throw new SemanticError(SemanticErrorCode.TerminatorMemberNotValid, position, type);
            }

        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private DomainTerminator(final Position position,
                             final Domain domain,
                             final String name,
                             final PragmaList pragmas) {
        super(position, name);
        this.domain = domain;

        this.pragmas = pragmas;

        final List<String> klPragma = pragmas.getPragmaValues(PragmaList.KEY_LETTER);
        if (klPragma == null || klPragma.size() == 0) {
            this.keyLetters = getName();
        } else {
            this.keyLetters = klPragma.get(0);
        }

        if (keyLetters.charAt(0) == '\"') {
            keyLetters = keyLetters.substring(1, keyLetters.length() - 1);
        }

    }

    public void addService(final DomainTerminatorService service) throws SemanticError {
        ServiceOverload overload = services.find(service.getName());
        if (overload == null) {
            overload = new ServiceOverload(service.getName());
            services.put(service.getName(), overload);
        }
        overload.add(service);
    }

    @Override
    public PragmaList getPragmas() {
        return pragmas;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public String getKeyLetters() {
        return keyLetters;
    }

    public DomainTerminatorService getMatchingService(final ProjectTerminatorService service) throws NotFound {
        final DomainTerminatorService
                found =
                services.get(service.getName()).get(service.getParameters(), service.isFunction());
        if (found == null ||
            !(service.getReturnType() == null ?
              found.getReturnType() == null :
              service.getReturnType().equals(found.getReturnType()))) {
            throw new NotFoundOnParent(SemanticErrorCode.ServiceNotFoundOnTerminator,
                                       service.getPosition(),
                                       service.getName(),
                                       service.getTerminator().getName());
        }
        return found;
    }

    @Override
    public List<DomainTerminatorService> getServices() {
        final List<DomainTerminatorService> result = new ArrayList<DomainTerminatorService>();
        for (final ServiceOverload overload : services.asList()) {
            result.addAll(overload.asList());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        return "terminator " +
               getName() +
               " is\n" +
               TextUtils.alignTabs(TextUtils.formatList(getServices(), "", "", "\n\n")) +
               "end terminator;\n" +
               pragmas;
    }

    private String keyLetters;

    private final CheckedLookup<ServiceOverload>
            services =
            new CheckedLookup<ServiceOverload>(SemanticErrorCode.ServiceAlreadyDefinedOnObject,
                                               SemanticErrorCode.ServiceNotFoundOnObject,
                                               this);

    private final PragmaList pragmas;

    private final Domain domain;

    @Override
    public TerminatorNameExpression getReference(final Position position) {
        return new TerminatorNameExpression(position, this);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitDomainTerminator(this, p);
    }

    private String comment;

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

}
