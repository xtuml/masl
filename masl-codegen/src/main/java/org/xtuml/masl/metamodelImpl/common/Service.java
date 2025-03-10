/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.code.CodeBlock;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.name.Named;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Service extends Positioned implements Named, org.xtuml.masl.metamodel.common.Service {

    public Service(final Position position,
                   final String name,
                   final Visibility visibility,
                   final List<ParameterDefinition> parameters,
                   final BasicType returnType,
                   final List<ExceptionReference> exceptionSpecs,
                   final PragmaList pragmas) {
        super(position);
        this.declarationPragmas = pragmas;
        this.name = name;
        this.visibility = visibility;
        this.returnType = returnType;
        this.exceptionSpecs = exceptionSpecs;
        this.params =
                new CheckedLookup<>(SemanticErrorCode.ParameterAlreadyDefinedOnService,
                                    SemanticErrorCode.ParameterNotFoundOnService,
                                    this);

        for (final ParameterDefinition param : parameters) {
            try {
                if (param != null) {
                    addParameter(param);
                }
            } catch (final AlreadyDefined e) {
                // Report error, and ignore parameter
                e.report();
            }
        }
    }

    public void addParameter(final ParameterDefinition param) throws AlreadyDefined {
        nameLookup.addName(param);
        params.put(param.getName(), param);
        signature.add(param.getType());
    }

    @Override
    public CodeBlock getCode() {
        return this.code;
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
    public List<ExceptionReference> getExceptionSpecs() {
        return Collections.unmodifiableList(exceptionSpecs);
    }

    @Override
    public String getFileHash() {
        return fileHash;
    }

    @Override
    public String getName() {
        return name;
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    @Override
    public int getOverloadNo() {
        return overloadNo;
    }

    public ParameterDefinition getParameter(final int i) {
        return params.asList().get(i);
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return Collections.unmodifiableList(params.asList());
    }

    @Override
    public abstract String getQualifiedName();

    @Override
    public BasicType getReturnType() {
        return returnType;
    }

    public abstract String getServiceType();

    public List<BasicType> getSignature() {
        return signature;
    }

    @Override
    public org.xtuml.masl.metamodel.common.Visibility getVisibility() {
        return visibility.getVisibility();
    }

    @Override
    public boolean isFunction() {
        return getReturnType() != null;
    }

    /**
     * The code to set.
     */
    public void setCode(final CodeBlock code) {
        this.code = code;
        if (isFunction()) {
            final ReturnCheckVisitor checkReturn = new ReturnCheckVisitor();
            try {
                checkReturn.visit(code);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!checkReturn.hasReturn()) {
                new SemanticError(SemanticErrorCode.FunctionMayNotReturnAValue, code.getPosition()).report();
            }
        }
    }

    @Override
    public List<org.xtuml.masl.metamodel.code.VariableDefinition> getLocalVariables() {
        if (code == null) {
            return Collections.emptyList();
        } else {
            return new LocalVariableCollector(code).getLocalVariables();
        }
    }

    public void setDefinitionPragmas(final PragmaList pragmas) {
        definitionPragmas = pragmas;
    }

    public void setFileHash(final String fileHash) {
        this.fileHash = fileHash;
    }

    public void setOverloadNo(final int overloadNo) {
        this.overloadNo = overloadNo;
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(getParameters(),
                                    returnType,
                                    exceptionSpecs,
                                    declarationPragmas,
                                    definitionPragmas,
                                    code);
    }

    @Override
    public String toString() {
        return visibility +
               (visibility.toString().equals("") ? "" : " ") +
               getServiceType() +
               "service\t" +
               name +
               "\t(\t" +
               org.xtuml.masl.utils.TextUtils.formatList(params.asList(), "", ",\n\t\t\t", "") +
               " )" +
               (returnType == null ? "" : "\n\t\t\treturn\t\t" + returnType) +
               org.xtuml.masl.utils.TextUtils.formatList(exceptionSpecs, " raises ", ", ", "") +
               ";" +
               org.xtuml.masl.utils.TextUtils.formatList(declarationPragmas.getPragmas(), "\t", "\n\t\t\t\t\t\t", "") +
               "\n";
    }

    private final NameLookup nameLookup = new NameLookup();

    private int overloadNo;
    private CodeBlock code = null;
    private final PragmaList declarationPragmas;
    private PragmaList definitionPragmas;
    private final List<ExceptionReference> exceptionSpecs;
    private final String name;

    private final CheckedLookup<ParameterDefinition> params;

    private final BasicType returnType;

    private final Visibility visibility;

    private String fileHash = null;

    private final List<BasicType> signature = new ArrayList<>();

}
