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
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.name.Named;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.type.EnumerateItem;
import org.xtuml.masl.metamodelImpl.type.EnumerateType;
import org.xtuml.masl.metamodelImpl.type.TypeDeclaration;

import java.util.*;

public class Domain extends Positioned implements org.xtuml.masl.metamodel.domain.Domain, Named {

    public static class ServiceOverload extends org.xtuml.masl.metamodelImpl.common.ServiceOverload<DomainService> {

        public ServiceOverload(final String name) {
            super(name, SemanticErrorCode.ServiceAlreadyDefinedInDomain);
        }

        @Override
        public ServiceExpression getReference(final Position position) {
            return new ServiceExpression(position, this);
        }

        /**
         * Renumber overloads so that public ones are numbered first. This ensures
         * that numbering is consistent between .int and .mod files.
         */
        void renumberOverloads() {
            int overloadNo = 0;

            for (final DomainService service : asList()) {
                if (service.getVisibility() == Visibility.PUBLIC) {
                    service.setOverloadNo(overloadNo++);
                }
            }
            for (final DomainService service : asList()) {
                if (service.getVisibility() == Visibility.PRIVATE) {
                    service.setOverloadNo(overloadNo++);
                }
            }
        }
    }

    public static class ServiceExpression
            extends org.xtuml.masl.metamodelImpl.expression.ServiceExpression<DomainService>
            implements org.xtuml.masl.metamodel.expression.DomainServiceExpression {

        public ServiceExpression(final Position position, final ServiceOverload overload) {
            super(position, overload);
        }

    }

    public class Reference extends Positioned {

        private Reference(final String reference) {
            super(reference);
        }

        public Domain getDomain() {
            return Domain.this;
        }
    }

    public Reference getReference(final String reference) {
        return new Reference(reference);
    }

    public Domain(final Position position, final String name) {
        super(position);
        this.name = name;
    }

    public void addException(final org.xtuml.masl.metamodelImpl.exception.ExceptionDeclaration exception) {
        if (exception == null) {
            return;
        }

        try {
            exceptions.put(exception.getName(), exception);
        } catch (final AlreadyDefined e) {
            e.report();
        }
    }

    public void addObject(final org.xtuml.masl.metamodelImpl.object.ObjectDeclaration object) throws SemanticError {
        if (object == null) {
            return;
        }

        objects.put(object.getName(), object);
        nameLookup.addName(object);
    }

    public void addTerminator(final org.xtuml.masl.metamodelImpl.domain.DomainTerminator terminator) throws
                                                                                                     SemanticError {
        if (terminator == null) {
            return;
        }

        terminators.put(terminator.getName(), terminator);
        nameLookup.addName(terminator);
    }

    public void addRelationship(final RelationshipDeclaration relationship) throws SemanticError {
        relationships.put(relationship.getName(), relationship);
    }

    public void addService(final DomainService service) throws SemanticError {
        if (service == null) {
            return;
        }

        ServiceOverload overload = services.find(service.getName());
        if (overload == null) {
            overload = new ServiceOverload(service.getName());
            services.put(service.getName(), overload);
            nameLookup.addName(overload);
        }

        overload.add(service);

    }

    private final Map<String, List<EnumerateItem>> enumLookup = new HashMap<String, List<EnumerateItem>>();

    public void addTypeForwardDeclaration(final TypeDeclaration type) throws SemanticError {
        if (type == null) {
            return;
        }

        typeForwardDeclarations.put(type.getName(), type);
        types.put(type.getName(), type);
        nameLookup.addName(type);
    }

    public void addType(final TypeDeclaration type) throws SemanticError {
        if (type == null) {
            return;
        }

        types.put(type.getName(), type);
        nameLookup.addName(type);
    }

    public void addEnumerateItems(final EnumerateType enumerate) {
        for (final EnumerateItem item : enumerate.getItems()) {
            List<EnumerateItem> items = enumLookup.get(item.getName());
            if (items == null) {
                items = new ArrayList<EnumerateItem>();
                enumLookup.put(item.getName(), items);
            }
            items.add(item);
        }

    }

    public ObjectDeclaration findObject(final String name) {
        return objects.find(name);
    }

    public ExceptionDeclaration getException(final String name) throws NotFound {
        return exceptions.get(name);
    }

    public ExceptionDeclaration findException(final String name) {
        return exceptions.find(name);
    }

    @Override
    public List<ExceptionDeclaration> getExceptions() {
        return Collections.unmodifiableList(exceptions.asList());
    }

    @Override
    public String getName() {
        return name;
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    public ObjectDeclaration getObject(final String name) throws NotFound {
        return objects.get(name);
    }

    @Override
    public List<ObjectDeclaration> getObjects() {
        return Collections.unmodifiableList(objects.asList());
    }

    public DomainTerminator getTerminator(final String name) throws NotFound {
        return terminators.get(name);
    }

    @Override
    public List<DomainTerminator> getTerminators() {
        return Collections.unmodifiableList(terminators.asList());
    }

    @Override
    public org.xtuml.masl.metamodel.common.PragmaList getPragmas() {
        return pragmas;
    }

    public RelationshipDeclaration getRelationship(final String name) throws NotFound {
        return relationships.get(name);
    }

    @Override
    public List<RelationshipDeclaration> getRelationships() {
        return Collections.unmodifiableList(relationships.asList());
    }

    @Override
    public List<DomainService> getServices() {
        final List<DomainService> result = new ArrayList<DomainService>();
        for (final ServiceOverload overload : services.asList()) {
            result.addAll(overload.asList());
        }
        return Collections.unmodifiableList(result);
    }

    public TypeDeclaration getType(final String name) throws NotFound {
        return types.get(name);
    }

    public TypeDeclaration findType(final String name) {
        return types.find(name);
    }

    @Override
    public List<TypeDeclaration> getTypes() {
        return Collections.unmodifiableList(types.asList());
    }

    @Override
    public List<? extends org.xtuml.masl.metamodel.type.TypeDeclaration> getTypeForwardDeclarations() {
        return Collections.unmodifiableList(typeForwardDeclarations.asList());
    }

    public void setFullyDefined() {
        for (final ObjectDeclaration obj : objects.asList()) {
            obj.linkReferentialAttributes();
        }

        for (final ServiceOverload service : services.asList()) {
            service.renumberOverloads();
        }

        for (final TypeDeclaration type : types.asList()) {
            if (type.getTypeDefinition() == null) {
                new SemanticError(SemanticErrorCode.TypeNotDefined, type.getPosition(), type.getName()).report();
            }
        }

    }

    public void setPragmas(final PragmaList pragmas) {
        this.pragmas = pragmas;
    }

    @Override
    public String toString() {
        final List<String> objectNames = new ArrayList<String>();
        for (final ObjectDeclaration obj : objects.asList()) {
            objectNames.add(obj.getName());
        }

        return "domain " +
               name +
               " is\n" +
               org.xtuml.masl.utils.TextUtils.indentText("  ",
                                                         org.xtuml.masl.utils.TextUtils.formatList(objectNames,
                                                                                                   "",
                                                                                                   "object ",
                                                                                                   ";",
                                                                                                   "\n",
                                                                                                   "\n") +
                                                         org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(
                                                                 types.asList(),
                                                                 "",
                                                                 "\n",
                                                                 "\n")) +
                                                         org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(
                                                                 exceptions.asList(),
                                                                 "",
                                                                 "\n",
                                                                 "\n")) +
                                                         org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(
                                                                 relationships.asList(),
                                                                 "",
                                                                 "\n",
                                                                 "\n")) +
                                                         org.xtuml.masl.utils.TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(
                                                                 getServices(),
                                                                 "",
                                                                 "\n",
                                                                 "\n")) +
                                                         org.xtuml.masl.utils.TextUtils.formatList(terminators.asList(),
                                                                                                   "",
                                                                                                   "\n",
                                                                                                   "\n") +
                                                         org.xtuml.masl.utils.TextUtils.formatList(objects.asList(),
                                                                                                   "",
                                                                                                   "\n",
                                                                                                   "\n")) +
               "end domain;\n" +
               pragmas;
    }

    private final class DomainNameLookup extends NameLookup {

        private DomainNameLookup() {
            super(SemanticErrorCode.NameAlreadyDefinedInDomain, SemanticErrorCode.NameNotFoundInDomain, Domain.this);
        }

        @Override
        public Name find(final String name) {
            Name result = super.find(name);
            if (result == null) {
                // Look for an enumerate item
                final List<EnumerateItem> items = enumLookup.get(name);
                if (items == null) {
                    return null;
                }
                if (items.size() == 1) {
                    result = items.get(0);
                } else if (items.size() > 0) {
                    return new EnumerateItem.AmbiguousEnumItem(name, items);
                }
            }
            return result;
        }

    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(final boolean isInterface) {
        this.isInterface = isInterface;
    }

    public void addReferencedInterface(final Domain interfaceDependency) {
        referencedInterfaces.add(interfaceDependency);
    }

    @Override
    public Set<Domain> getReferencedInterfaces() {
        return Collections.unmodifiableSet(referencedInterfaces);
    }

    private final String name;
    private boolean isInterface = false;

    private PragmaList pragmas;

    private final NameLookup nameLookup = new DomainNameLookup();
    private final CheckedLookup<ServiceOverload>
            services =
            new CheckedLookup<ServiceOverload>(SemanticErrorCode.ServiceAlreadyDefinedInDomain,
                                               SemanticErrorCode.ServiceNotFoundInDomain,
                                               this);
    private final CheckedLookup<TypeDeclaration>
            typeForwardDeclarations =
            new CheckedLookup<TypeDeclaration>(SemanticErrorCode.TypeAlreadyDefinedInDomain,
                                               SemanticErrorCode.TypeNotFoundInDomain,
                                               this);

    private final CheckedLookup<TypeDeclaration>
            types =
            new CheckedLookup<TypeDeclaration>(SemanticErrorCode.TypeAlreadyDefinedInDomain,
                                               SemanticErrorCode.TypeNotFoundInDomain,
                                               this);

    private final CheckedLookup<ExceptionDeclaration>
            exceptions =
            new CheckedLookup<ExceptionDeclaration>(SemanticErrorCode.ExceptionAlreadyDefinedInDomain,
                                                    SemanticErrorCode.ExceptionNotFoundInDomain,
                                                    this);

    private final CheckedLookup<ObjectDeclaration>
            objects =
            new CheckedLookup<ObjectDeclaration>(SemanticErrorCode.ObjectAlreadyDefinedInDomain,
                                                 SemanticErrorCode.ObjectNotFoundInDomain,
                                                 this);
    private final CheckedLookup<DomainTerminator>
            terminators =
            new CheckedLookup<DomainTerminator>(SemanticErrorCode.TerminatorAlreadyDefinedInDomain,
                                                SemanticErrorCode.TerminatorNotFoundInDomain,
                                                this);

    private final CheckedLookup<RelationshipDeclaration>
            relationships =
            new CheckedLookup<RelationshipDeclaration>(SemanticErrorCode.RelationshipAlreadyDefinedInDomain,
                                                       SemanticErrorCode.RelationshipNotFoundInDomain,
                                                       this);
    private final Set<Domain> referencedInterfaces = new LinkedHashSet<Domain>();

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitDomain(this, p);
    }

}
