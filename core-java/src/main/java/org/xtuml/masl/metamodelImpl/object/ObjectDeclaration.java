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
package org.xtuml.masl.metamodelImpl.object;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.ParseOptions;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.NotFoundOnParent;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.statemodel.EventDeclaration;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.metamodelImpl.statemodel.TransitionTable;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.utils.TextUtils;

import java.util.*;

public class ObjectDeclaration extends Name implements org.xtuml.masl.metamodel.object.ObjectDeclaration {

    public class ServiceOverload extends org.xtuml.masl.metamodelImpl.common.ServiceOverload<ObjectService> {

        boolean isInstance = false;
        boolean isObject = false;

        public ServiceOverload(final String name) {
            super(name, SemanticErrorCode.ServiceAlreadyDefinedOnObject);

        }

        @Override
        protected void checkCompatible(final ObjectService service) throws SemanticError {
            if (isObject && service.isInstance() || isInstance && !service.isInstance()) {
                throw new SemanticError(SemanticErrorCode.NoOverloadOfInstanceAndObjectService, service.getPosition());
            }
            isInstance = service.isInstance();
            isObject = !service.isInstance();
        }

        @Override
        public ServiceExpression getReference(final Position position) {
            return new ServiceExpression(position, this);
        }

        public ServiceExpression getReference(final Position position, final Expression lhs) {
            return new ServiceExpression(position, lhs, this);
        }

        public boolean isInstance() {
            return isInstance;
        }

        public boolean isObject() {
            return isObject;
        }
    }

    public static class ServiceExpression
            extends org.xtuml.masl.metamodelImpl.expression.ServiceExpression<ObjectService>
            implements org.xtuml.masl.metamodel.expression.ObjectServiceExpression {

        public ServiceExpression(final Position position, final ServiceOverload overload) {
            super(position, overload);
        }

        public ServiceExpression(final Position position, final Expression lhs, final ServiceOverload overload) {
            super(position, overload);
            this.lhs = lhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        private Expression lhs;

    }

    public static ObjectDeclaration create(final Position position,
                                           final Domain domain,
                                           final String name,
                                           final PragmaList pragmas) {
        if (domain == null || name == null) {
            return null;
        }
        try {
            final ObjectDeclaration obj = new ObjectDeclaration(position, domain, name, pragmas);
            domain.addObject(obj);
            return obj;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    private ObjectDeclaration(final Position position,
                              final Domain domain,
                              final String name,
                              final PragmaList pragmas) {
        super(position, name);
        this.domain = domain;

        type = InstanceType.createAnonymous(this);
        this.declarationPragmas = pragmas;
    }

    public void addAttribute(final AttributeDeclaration attribute) throws SemanticError {
        attributes.put(attribute.getName(), attribute);
        nameLookup.addName(attribute);

        if (attribute.isPreferredIdentifier()) {
            preferredIdentifier.addAttribute(attribute);
        }
    }

    public void addEvent(final EventDeclaration event) throws SemanticError {
        events.put(event.getName(), event);
        nameLookup.addName(event);
    }

    public void addIdentifier(final IdentifierDeclaration identifier) {
        if (identifier == null) {
            return;
        }
        identifiers.add(identifier);
    }

    public void addRelationship(final RelationshipSpecification relSpec) {
        if (relSpec == null) {
            return;
        }
        relationships.add(relSpec);

        List<RelationshipSpecification> specs = relSpecLookup.get(relSpec.getRelationship());

        if (specs == null) {
            specs = new ArrayList<>();
            relSpecLookup.put(relSpec.getRelationship(), specs);
        }
        specs.add(relSpec);
    }

    public void addService(final ObjectService service) throws SemanticError {
        ServiceOverload overload = services.find(service.getName());
        if (overload == null) {
            overload = new ServiceOverload(service.getName());
            services.put(service.getName(), overload);
            nameLookup.addName(overload);
        }
        overload.add(service);
    }

    public void addState(final State state) throws SemanticError {
        states.put(state.getName(), state);
        if (state.isAssigner()) {
            hasAssignerState = true;
        } else {
            hasCurrentState = true;
        }
    }

    public void addSupertype(final ObjectDeclaration supertype) {
        supertypes.add(supertype);
    }

    public void addTransitionTable(final TransitionTable table) throws SemanticError {
        if (table == null) {
            return;
        }
        if (table.isAssigner()) {
            if (assignerStateMachine == null) {
                assignerStateMachine = table;
            } else {
                throw new SemanticError(SemanticErrorCode.OnlyOneAssignerStateMachine, table.getPosition(), getName());
            }
        } else {
            if (stateMachine == null) {
                stateMachine = table;
            } else {
                throw new SemanticError(SemanticErrorCode.OnlyOneStateMachine, table.getPosition(), getName());
            }
        }
    }

    public void setFullyDefined() {
        // Final semantic checks
        if (preferredIdentifier.getAttributes().size() == 0) {
            new SemanticError(SemanticErrorCode.NoPreferredIdentifier, getPosition(), getName()).report();
        } else {
            // Replace any identifier matching the preferred one with the preferred
            // one.
            identifiers.remove(preferredIdentifier);
            identifiers.add(preferredIdentifier);
        }

    }

    public AttributeDeclaration getAttribute(final String name) throws NotFound {
        return attributes.get(name);
    }

    public AttributeDeclaration findAttribute(final String name) {
        return attributes.find(name);
    }

    @Override
    public List<AttributeDeclaration> getAttributes() {
        return Collections.unmodifiableList(attributes.asList());
    }

    @Override
    public PragmaList getDeclarationPragmas() {
        return declarationPragmas;
    }

    @Override
    public PragmaList getDefinitionPragmas() {
        return definitionPragmas;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    public EventDeclaration getEvent(final String name) throws NotFound {
        return events.get(name);
    }

    @Override
    public List<EventDeclaration> getEvents() {
        return Collections.unmodifiableList(events.asList());
    }

    @Override
    public List<EventDeclaration> getAllEvents() {
        final List<EventDeclaration> allEvents = new ArrayList<>();
        getAllEvents(getSupertypes(), allEvents);
        allEvents.addAll(getEvents());
        return Collections.unmodifiableList(allEvents);
    }

    private void getAllEvents(final List<? extends ObjectDeclaration> object, final List<EventDeclaration> eventList) {
        for (final ObjectDeclaration superObj : object) {
            eventList.addAll(superObj.getEvents());
            getAllEvents(superObj.getSupertypes(), eventList);
        }
    }

    @Override
    public List<IdentifierDeclaration> getIdentifiers() {
        return Collections.unmodifiableList(new ArrayList<>(identifiers));
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    public ObjectService getPolymorphicService(final ObjectService service) throws NotFound {
        final ObjectService found = services.get(service.getName()).get(service.getParameters(), service.isFunction());
        if (found == null) {
            throw new NotFoundOnParent(SemanticErrorCode.ServiceNotFoundOnObject,
                                       service.getPosition(),
                                       service.getName(),
                                       getName());
        }
        return found;
    }

    @Override
    public IdentifierDeclaration getPreferredIdentifier() {
        return preferredIdentifier;
    }

    @Override
    public List<RelationshipSpecification> getRelationships() {
        return Collections.unmodifiableList(relationships);
    }

    public static ObjectDeclaration getObject(final Expression expression) throws SemanticError {
        return getObject(expression, false);
    }

    public static ObjectDeclaration getObject(final Expression expression, final boolean allowCollection) throws
                                                                                                          SemanticError {
        BasicType basicType = expression.getType().getPrimitiveType();

        // Dig into collection
        if (allowCollection && basicType instanceof CollectionType) {
            basicType = basicType.getContainedType().getPrimitiveType();
        }

        if (basicType instanceof InstanceType) {
            return ((InstanceType) basicType).getObjectDeclaration();
        } else {
            throw new SemanticError(allowCollection ?
                                    SemanticErrorCode.NotInstanceOrCollType :
                                    SemanticErrorCode.NotInstanceType,
                                    expression.getPosition(),
                                    expression.getType().toString());
        }

    }

    public RelationshipSpecification getRelationshipSpec(final RelationshipDeclaration.Reference relRef,
                                                         String role,
                                                         final ObjectNameExpression objRef,
                                                         final boolean allowToAssoc,
                                                         final boolean forceToAssoc) throws SemanticError {
        ObjectDeclaration object = (objRef == null ? null : objRef.getObject());
        final RelationshipDeclaration relationship = relRef.getRelationship();
        if (relationship == null) {
            return null;
        }

        if (role != null && objRef == null) {
            object = domain.findObject(role);
            if (object != null) {
                role = null;
            }
        }
        final String
                specText =
                relationship.getName() +
                (role == null ? "" : "." + role) +
                (object == null ? "" : "." + object.getName());

        final List<RelationshipSpecification> specs = relSpecLookup.get(relationship);

        if (specs == null) {
            throw new SemanticError(SemanticErrorCode.NoLink, relRef.getPosition(), relationship.getName(), getName());
        }

        final List<RelationshipSpecification> matchingSpecs = new ArrayList<>();

        for (final RelationshipSpecification spec : specs) {
            if ((role == null || role.equals(spec.getRole())) &&
                (object == null || object.equals(spec.getDestinationObject()))) {
                if (spec.isToAssociative()) {
                    if (allowToAssoc || forceToAssoc) {
                        if (forceToAssoc || object != null || !ParseOptions.defaultToNonAssocNavigate()) {
                            matchingSpecs.add(spec);
                        }
                    }
                } else {
                    if (!forceToAssoc) {
                        matchingSpecs.add(spec);
                    }
                }
            }
        }

        if (matchingSpecs.size() == 0) {
            if (forceToAssoc) {
                throw new SemanticError(SemanticErrorCode.NoRelationshipMatchToAssoc, relRef.getPosition(), specText);
            } else {
                new SemanticError(SemanticErrorCode.NoRelationshipMatch,
                                  relRef.getPosition(),
                                  specText,
                                  specs.get(0)).report();
                return specs.get(0);
            }
        } else if (matchingSpecs.size() > 1) {
            new SemanticError(SemanticErrorCode.AmbiguousRelationship,
                              relRef.getPosition(),
                              specText,
                              matchingSpecs.get(0)).report();
            return matchingSpecs.get(0);
        }

        return matchingSpecs.get(0);
    }

    @Override
    public List<ObjectService> getServices() {
        final List<ObjectService> result = new ArrayList<>();
        for (final ServiceOverload overload : services.asList()) {
            result.addAll(overload.asList());
        }
        return Collections.unmodifiableList(result);
    }

    public State getState(final String name) throws NotFound {
        return states.get(name);
    }

    @Override
    public List<State> getStates() {
        return Collections.unmodifiableList(states.asList());
    }

    @Override
    public boolean hasCurrentState() {
        return hasCurrentState;
    }

    @Override
    public boolean hasAssignerState() {
        return hasAssignerState;
    }

    @Override
    public List<ObjectDeclaration> getSupertypes() {
        return Collections.unmodifiableList(supertypes);
    }

    @Override
    public TransitionTable getStateMachine() {
        return stateMachine;
    }

    @Override
    public TransitionTable getAssignerStateMachine() {
        return assignerStateMachine;
    }

    public InstanceType getType() {
        return type;
    }

    public void linkReferentialAttributes() {
        // This can't be done as attributes are added, because related objects may
        // not be fully defined.
        final Set<ReferentialAttributeDefinition> allFormalisms = new HashSet<>();

        for (final AttributeDeclaration att : attributes) {
            att.linkReferentialAttributes();
            for (final ReferentialAttributeDefinition formalism : att.getRefAttDefs()) {
                allFormalisms.add(formalism);
            }
        }

        for (final RelationshipSpecification spec : relationships) {
            try {
                if (spec.isFormalisingEnd()) {
                    final ObjectDeclaration identifierEnd = spec.getDestinationObject();
                    final Set<AttributeDeclaration> formalisedAtts = new HashSet<>();

                    for (final Iterator<ReferentialAttributeDefinition> it = allFormalisms.iterator(); it.hasNext(); ) {
                        final ReferentialAttributeDefinition formalism = it.next();
                        if (formalism.getRelationship() == spec &&
                            formalism.getDestinationAttribute().getParentObject() == identifierEnd) {
                            formalisedAtts.add(formalism.getDestinationAttribute());
                            it.remove();
                        }
                    }
                    if (formalisedAtts.size() == 0) {
                        throw new SemanticError(SemanticErrorCode.RelationshipFormalismMissing,
                                                getPosition(),
                                                getName(),
                                                spec);
                    }

                    boolean foundMatch = false;
                    for (final IdentifierDeclaration ident : identifierEnd.getIdentifiers()) {
                        if (formalisedAtts.equals(new HashSet<>(ident.getAttributes()))) {
                            foundMatch = true;
                            break;
                        }
                    }

                    if (!foundMatch) {
                        throw new SemanticError(SemanticErrorCode.RelationshipFormalismIncorrect,
                                                getPosition(),
                                                getName(),
                                                spec);
                    }

                }
            } catch (final SemanticError e) {
                e.report();
            }
        }
    }

    public void setDefinitionPragmas(final PragmaList pragmas) {
        this.definitionPragmas = pragmas;
    }

    @Override
    public String toString() {
        return "object " +
               getName() +
               " is\n" +
               TextUtils.indentText("  ",
                                    TextUtils.alignTabs(org.xtuml.masl.utils.TextUtils.formatList(attributes.asList(),
                                                                                                  "",
                                                                                                  "",
                                                                                                  "",
                                                                                                  "",
                                                                                                  "\n")) +
                                    TextUtils.formatList(identifiers, "", "", "\n") +
                                    TextUtils.alignTabs(TextUtils.formatList(services.asList(), "", "", "\n") +
                                                        TextUtils.formatList(events.asList(), "", "", "\n") +
                                                        TextUtils.formatList(states.asList(), "", "", "\n")) +
                                    (stateMachine == null ? "" : TextUtils.alignTabs(stateMachine + "\n")) +
                                    (assignerStateMachine == null ?
                                     "" :
                                     TextUtils.alignTabs(assignerStateMachine + "\n"))) +
               "end object;\n" +
               definitionPragmas;
    }

    private final NameLookup
            nameLookup =
            new NameLookup(SemanticErrorCode.NameAlreadyDefinedOnObject, SemanticErrorCode.NotObjectMember, this);

    private final CheckedLookup<AttributeDeclaration>
            attributes =
            new CheckedLookup<>(SemanticErrorCode.AttributeAlreadyDefinedOnObject,
                                SemanticErrorCode.AttributeNotFoundOnObject,
                                this);

    private final LinkedHashSet<IdentifierDeclaration> identifiers = new LinkedHashSet<>();

    private final IdentifierDeclaration preferredIdentifier = IdentifierDeclaration.createPreferred();

    private final CheckedLookup<ServiceOverload>
            services =
            new CheckedLookup<>(SemanticErrorCode.ServiceAlreadyDefinedOnObject,
                                SemanticErrorCode.ServiceNotFoundOnObject,
                                this);

    private final CheckedLookup<EventDeclaration>
            events =
            new CheckedLookup<>(SemanticErrorCode.EventAlreadyDefinedOnObject,
                                SemanticErrorCode.EventNotFoundOnObject,
                                this);

    private final CheckedLookup<State>
            states =
            new CheckedLookup<>(SemanticErrorCode.StateAlreadyDefinedOnObject,
                                SemanticErrorCode.StateNotFoundOnObject,
                                this);

    private TransitionTable stateMachine = null;
    private TransitionTable assignerStateMachine = null;
    private final List<RelationshipSpecification> relationships = new ArrayList<>();

    private PragmaList definitionPragmas;

    private final PragmaList declarationPragmas;

    private final List<ObjectDeclaration> supertypes = new ArrayList<>();

    private final Domain domain;

    private final InstanceType type;

    private final Map<RelationshipDeclaration, List<RelationshipSpecification>> relSpecLookup = new HashMap<>();

    private boolean hasCurrentState;
    private boolean hasAssignerState;

    private Position defPosition = null;

    @Override
    public Position getPosition() {
        return defPosition == null ? super.getPosition() : defPosition;
    }

    public void setPosition(final Position position) {
        this.defPosition = position;
    }

    @Override
    public ObjectNameExpression getReference(final Position position) {
        return new ObjectNameExpression(position, this);
    }

    public boolean canReceive(final EventDeclaration event) {
        if (event.getParentObject() == this) {
            return true;
        }
        for (final ObjectDeclaration supertype : supertypes) {
            if (supertype.canReceive(event)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitObjectDeclaration(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(getAttributes(),
                                    getIdentifiers(),
                                    getServices(),
                                    getEvents(),
                                    getStates(),
                                    stateMachine,
                                    assignerStateMachine,
                                    getRelationships(),
                                    definitionPragmas,
                                    declarationPragmas);
    }

}
