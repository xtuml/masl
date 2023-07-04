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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.*;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectService extends Service implements org.xtuml.masl.metamodel.object.ObjectService {

    public static void create(final Position position,
                              final ObjectDeclaration object,
                              final String name,
                              final Visibility type,
                              final boolean isInstance,
                              final RelationshipDeclaration.Reference relRef,
                              final List<ParameterDefinition> parameters,
                              final BasicType returnType,
                              final List<ExceptionReference> exceptionSpecs,
                              final PragmaList pragmas) {
        if (object == null ||
            name == null ||
            type == null ||
            parameters == null ||
            exceptionSpecs == null ||
            pragmas == null) {
            return;
        }

        try {
            object.addService(new ObjectService(position,
                                                object,
                                                name,
                                                type,
                                                isInstance,
                                                relRef,
                                                parameters,
                                                returnType,
                                                exceptionSpecs,
                                                pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    private ObjectService(final Position position,
                          final ObjectDeclaration object,
                          final String name,
                          final Visibility type,
                          final boolean isInstance,
                          final RelationshipDeclaration.Reference relRef,
                          final List<ParameterDefinition> parameters,
                          final BasicType returnType,
                          final List<ExceptionReference> exceptionSpecs,
                          final PragmaList pragmas) throws SemanticError {
        super(position, name, type, parameters, returnType, exceptionSpecs, pragmas);
        this.parentObject = object;
        this.isInstance = isInstance;

        SubtypeRelationshipDeclaration subrel = null;

        if (relRef != null) {
            if (relRef.getRelationship() instanceof SubtypeRelationshipDeclaration &&
                object == ((SubtypeRelationshipDeclaration) relRef.getRelationship()).getSupertype()) {
                subrel = (SubtypeRelationshipDeclaration) relRef.getRelationship();
            } else {
                throw new SemanticError(SemanticErrorCode.OnlyDeferToSubtype, relRef.getPosition());
            }
        }

        this.relationship = subrel;
    }

    @Override
    public String getFileName() {
        if (getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME) != null &&
            getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).size() > 0) {
            return getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).get(0);
        } else {
            return parentObject.getName() +
                   "_" +
                   getName() +
                   (getOverloadNo() > 0 ? "." + getOverloadNo() : "") +
                   ".svc";
        }
    }

    @Override
    public boolean isInstance() {
        return isInstance;
    }

    @Override
    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    @Override
    public String getQualifiedName() {
        return parentObject.getDomain().getName() + "::" + parentObject.getName() + "." + getName();
    }

    @Override
    public RelationshipDeclaration getRelationship() {
        return relationship;
    }

    @Override
    public boolean isDeferred() {
        return relationship != null;
    }

    @Override
    public List<ObjectService> getDeferredTo() {
        final List<ObjectService> deferredTo = new ArrayList<>();
        for (final ObjectDeclaration subObject : relationship.getSubtypes()) {
            try {
                deferredTo.add(subObject.getPolymorphicService(this));
            } catch (final NotFound e) {
                e.report();
            }
        }
        return Collections.unmodifiableList(deferredTo);
    }

    @Override
    public String getServiceType() {
        return (isInstance ? "instance " : "") + (isDeferred() ? "deferred (" + relationship.getName() + ") " : "");
    }

    private final boolean isInstance;

    private final SubtypeRelationshipDeclaration relationship;

    private final ObjectDeclaration parentObject;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitObjectService(this);
    }

}
