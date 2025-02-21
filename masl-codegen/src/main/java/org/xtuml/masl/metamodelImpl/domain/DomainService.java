/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.domain;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.*;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.List;

public class DomainService extends Service implements org.xtuml.masl.metamodel.domain.DomainService {

    public static DomainService create(final Position position,
                                       final Domain domain,
                                       final String name,
                                       final Visibility type,
                                       final List<ParameterDefinition> parameters,
                                       final BasicType returnType,
                                       final List<ExceptionReference> exceptionSpecs,
                                       final PragmaList pragmas) {
        if (pragmas.getValue(PragmaList.TEST_ONLY).equals("true") && !CommandLine.INSTANCE.isForTest()) {
            return null;
        }

        if (domain == null ||
            name == null ||
            type == null ||
            parameters == null ||
            exceptionSpecs == null ||
            pragmas == null) {
            return null;
        }
        try {
            final DomainService
                    service =
                    new DomainService(position, domain, name, type, parameters, returnType, exceptionSpecs, pragmas);
            domain.addService(service);
            return service;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private DomainService(final Position position,
                          final Domain domain,
                          final String name,
                          final Visibility visibility,
                          final List<ParameterDefinition> parameters,
                          final BasicType returnType,
                          final List<ExceptionReference> exceptionSpecs,
                          final PragmaList pragmas) {
        super(position, name, visibility, parameters, returnType, exceptionSpecs, pragmas);

        this.domain = domain;

        if (isExternal()) {
            fileExtension = ".ext";
        } else if (isScenario()) {
            fileExtension = ".scn";
        } else {
            fileExtension = ".svc";
        }
        if (visibility == Visibility.PUBLIC) {
            for (final ParameterDefinition parameter : parameters) {
                parameter.getType().checkCanBePublic();
            }
            if (returnType != null) {
                returnType.checkCanBePublic();
            }
        }

    }

    private final String fileExtension;

    @Override
    public String getFileName() {
        if (getDeclarationPragmas().hasValue(PragmaList.FILENAME)) {
            return getDeclarationPragmas().getValue(PragmaList.FILENAME);
        } else {
            return getName() + (getOverloadNo() > 0 ? "." + getOverloadNo() : "") + fileExtension;
        }
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public boolean isExternal() {
        return getDeclarationPragmas().hasPragma(PragmaList.EXTERNAL);
    }

    @Override
    public boolean isScenario() {
        return getDeclarationPragmas().hasPragma(PragmaList.SCENARIO);
    }

    @Override
    public int getExternalNo() {
        return Integer.parseInt(getDeclarationPragmas().getValue(PragmaList.EXTERNAL));
    }

    @Override
    public int getScenarioNo() {
        return Integer.parseInt(getDeclarationPragmas().getValue(PragmaList.SCENARIO));
    }

    @Override
    public String getQualifiedName() {
        return domain.getName() + "::" + getName();
    }

    @Override
    public String getServiceType() {
        return "";
    }

    private final Domain domain;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDomainService(this);
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
