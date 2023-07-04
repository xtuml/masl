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

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.code.CodeBlock;
import org.xtuml.masl.metamodelImpl.code.VariableDefinition;
import org.xtuml.masl.metamodelImpl.common.*;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.expression.*;
import org.xtuml.masl.metamodelImpl.expression.CreateExpression.CreateAggregateValue;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;

import java.util.ArrayList;
import java.util.List;

public class DomainTerminatorService extends Service
        implements org.xtuml.masl.metamodel.domain.DomainTerminatorService {

    public static DomainTerminatorService create(final Position position,
                                                 final DomainTerminator terminator,
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
            return null;
        }

        try {
            final DomainTerminatorService
                    service =
                    new DomainTerminatorService(position,
                                                terminator,
                                                name,
                                                type,
                                                parameters,
                                                returnType,
                                                exceptionSpecs,
                                                pragmas);
            terminator.addService(service);
            return service;
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private DomainTerminatorService(final Position position,
                                    final DomainTerminator terminator,
                                    final String name,
                                    final Visibility type,
                                    final List<ParameterDefinition> parameters,
                                    final BasicType returnType,
                                    final List<ExceptionReference> exceptionSpecs,
                                    final PragmaList pragmas) throws SemanticError {
        super(position, name, type, parameters, returnType, exceptionSpecs, pragmas);
        this.terminator = terminator;

        if (CommandLine.INSTANCE.isForTest()) {
            final ObjectDeclaration
                    testObject =
                    ObjectDeclaration.create(position,
                                             terminator.getDomain(),
                                             "_TEST_" + terminator.getName() + "_" + name,
                                             new PragmaList());
            testObject.setDefinitionPragmas(new PragmaList());
            AttributeDeclaration.create(testObject,
                                        "_TEST_unique_id",
                                        IntegerType.createAnonymous(),
                                        true,
                                        true,
                                        new ArrayList<ReferentialAttributeDefinition>(),
                                        null,
                                        new PragmaList());

            final List<CreateAggregateValue> aggregate = new ArrayList<CreateAggregateValue>();
            final AttributeDeclaration
                    timeAtt =
                    AttributeDeclaration.create(testObject,
                                                "_TEST_call_time",
                                                TimestampType.createAnonymous(),
                                                false,
                                                false,
                                                new ArrayList<ReferentialAttributeDefinition>(),
                                                null,
                                                new PragmaList());
            aggregate.add(new CreateExpression.NormalAttribute(position,
                                                               timeAtt,
                                                               CharacteristicExpression.create(position,
                                                                                               new TypeNameExpression(
                                                                                                       position,
                                                                                                       TimestampType.createAnonymous()),
                                                                                               "now",
                                                                                               new ArrayList<Expression>())));

            for (final ParameterDefinition param : parameters) {
                final AttributeDeclaration
                        att =
                        AttributeDeclaration.create(testObject,
                                                    param.getName(),
                                                    param.getType(),
                                                    false,
                                                    false,
                                                    new ArrayList<ReferentialAttributeDefinition>(),
                                                    null,
                                                    new PragmaList());
                aggregate.add(new CreateExpression.NormalAttribute(position,
                                                                   att,
                                                                   new ParameterNameExpression(position, param)));
            }
            testObject.setFullyDefined();

            final Expression
                    createExpression =
                    CreateExpression.create(position, new ObjectNameExpression(null, testObject), aggregate);

            testInstance =
                    new VariableDefinition("_TEST_instance",
                                           InstanceType.createAnonymous(testObject),
                                           true,
                                           createExpression,
                                           new PragmaList());
            final CodeBlock code = new CodeBlock(position, true);
            code.addVariableDefinition(testInstance);
            super.setCode(code);
        }

    }

    @Override
    public void setCode(final CodeBlock code) {
        if (testInstance != null) {
            code.addVariableDefinition(testInstance);
        }
        super.setCode(code);
    }

    @Override
    public String getFileName() {
        if (getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME) != null &&
            getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).size() > 0) {
            return getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).get(0);
        } else {
            return terminator.getKeyLetters() +
                   "_" +
                   getName() +
                   (getOverloadNo() > 0 ? "." + getOverloadNo() : "") +
                   ".tr";
        }
    }

    @Override
    public DomainTerminator getTerminator() {
        return terminator;
    }

    @Override
    public String getQualifiedName() {
        return terminator.getDomain().getName() + "::" + terminator.getName() + "~>" + getName();
    }

    @Override
    public String getServiceType() {
        return "";
    }

    private final DomainTerminator terminator;
    private VariableDefinition testInstance = null;

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitDomainTerminatorService(this, p);
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
