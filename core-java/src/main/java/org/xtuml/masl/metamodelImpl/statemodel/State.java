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
package org.xtuml.masl.metamodelImpl.statemodel;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.code.CodeBlock;
import org.xtuml.masl.metamodelImpl.common.*;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.name.Named;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State extends Positioned implements org.xtuml.masl.metamodel.statemodel.State, Named {

    public static void create(final Position position,
                              final ObjectDeclaration object,
                              final String name,
                              final StateType type,
                              final List<ParameterDefinition> parameters,
                              final PragmaList pragmas) {
        if (object == null || name == null || type == null) {
            return;
        }
        try {
            object.addState(new State(position, object, name, type, parameters, pragmas));
        } catch (final SemanticError e) {
            e.report();
        }
    }

    private State(final Position position,
                  final ObjectDeclaration object,
                  final String name,
                  final StateType type,
                  final List<ParameterDefinition> parameters,
                  final PragmaList pragmas) {
        super(position);
        this.name = name;
        this.parentObject = object;
        this.type = type;
        this.params =
                new CheckedLookup<>(SemanticErrorCode.ParameterAlreadyDefinedOnState,
                                    SemanticErrorCode.ParameterNotFoundOnState,
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
        this.declarationPragmas = pragmas;

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
    public String getFileHash() {
        return fileHash;
    }

    @Override
    public String getFileName() {
        return parentObject.getName() + (isAssigner() ? "-A_" : "_") + name + ".al";
    }

    @Override
    public String getName() {
        return name;
    }

    public NameLookup getNameLookup() {
        return nameLookup;
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return Collections.unmodifiableList(params.asList());
    }

    @Override
    public List<org.xtuml.masl.metamodel.code.VariableDefinition> getLocalVariables() {
        if (code == null) {
            return Collections.emptyList();
        } else {
            return new LocalVariableCollector(code).getLocalVariables();
        }
    }

    @Override
    public ObjectDeclaration getParentObject() {
        return parentObject;
    }

    @Override
    public String getQualifiedName() {
        return parentObject.getDomain().getName() + "::" + parentObject.getName() + "." + name;
    }

    @Override
    public Type getType() {
        return type.getType();
    }

    public boolean isAssigner() {
        return type == StateType.ASSIGNER_START || type == StateType.ASSIGNER;
    }

    public boolean isInstance() {
        return type == StateType.NORMAL || type == StateType.TERMINAL;
    }

    /**
     * The code to set.
     */
    public void setCode(final CodeBlock code) {
        this.code = code;
    }

    public void setDefinitionPragmas(final PragmaList pragmas) {
        definitionPragmas = pragmas;
    }

    public void setFileHash(final String fileHash) {
        this.fileHash = fileHash;
    }

    @Override
    public String toString() {
        final String title = type + (type == StateType.NORMAL ? "" : " ") + "state\t" + name + "\t(\t";
        return title +
               org.xtuml.masl.utils.TextUtils.formatList(params.asList(), "", ",\n\t\t\t", "") +
               " );\n" +
               declarationPragmas;
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitState(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(params.asList(), code, declarationPragmas, definitionPragmas);
    }

    private final String name;

    private final StateType type;

    private final CheckedLookup<ParameterDefinition> params;

    private final PragmaList declarationPragmas;

    private CodeBlock code = null;

    private PragmaList definitionPragmas = null;

    private final NameLookup nameLookup = new NameLookup();

    private final List<BasicType> signature = new ArrayList<>();

    private final ObjectDeclaration parentObject;

    private String fileHash = null;

}
