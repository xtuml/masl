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
package org.xtuml.masl.metamodelImpl.project;

import org.xtuml.masl.metamodelImpl.common.*;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class ProjectTerminatorService extends Service
        implements org.xtuml.masl.metamodel.project.ProjectTerminatorService {

    public static void create(final Position position,
                              final ProjectTerminator terminator,
                              final String name,
                              final Visibility type,
                              final List<ParameterDefinition> parameters,
                              final BasicType returnType,
                              final List<ExceptionReference> exceptionSpecs,
                              final PragmaList pragmas) {
        if (terminator == null ||
            name == null ||
            type == null ||
            parameters == null ||
            exceptionSpecs == null ||
            pragmas == null) {
            return;
        }

        try {
            terminator.addService(new ProjectTerminatorService(position,
                                                               terminator,
                                                               name,
                                                               type,
                                                               parameters,
                                                               returnType,
                                                               exceptionSpecs,
                                                               pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    private ProjectTerminatorService(final Position position,
                                     final ProjectTerminator terminator,
                                     final String name,
                                     final Visibility type,
                                     final List<ParameterDefinition> parameters,
                                     final BasicType returnType,
                                     final List<ExceptionReference> exceptionSpecs,
                                     final PragmaList pragmas) throws SemanticError {
        super(position, name, type, parameters, returnType, exceptionSpecs, pragmas);
        this.terminator = terminator;
        domainTerminatorService = terminator.getDomainTerminator().getMatchingService(this);
    }

    @Override
    public DomainTerminatorService getDomainTerminatorService() {
        return domainTerminatorService;
    }

    @Override
    public String getFileName() {
        if (getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME) != null &&
            getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).size() > 0) {
            return getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).get(0);
        } else {
            return terminator.getDomainTerminator().getDomain().getName() +
                   "_" +
                   terminator.getDomainTerminator().getKeyLetters() +
                   "_" +
                   domainTerminatorService.getName() +
                   (getOverloadNo() > 0 ? "." + getOverloadNo() : "") +
                   ".tr";
        }
    }

    @Override
    public String getQualifiedName() {
        return terminator.getDomainTerminator().getDomain().getName() +
               "::" +
               terminator.getDomainTerminator().getName() +
               "=>" +
               getName();
    }

    @Override
    public String getServiceType() {
        return "";
    }

    @Override
    public ProjectTerminator getTerminator() {
        return terminator;
    }

    private final ProjectTerminator terminator;
    private final DomainTerminatorService domainTerminatorService;

}
