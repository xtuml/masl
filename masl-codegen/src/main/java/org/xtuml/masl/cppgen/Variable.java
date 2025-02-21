/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Variable {

    private class VariableDeclaration extends Declaration {

        @Override
        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();
            result.addAll(type.getDirectUsageForwardDeclarations());
            return result;
        }

        @Override
        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();
            result.addAll(type.getDirectUsageIncludes());
            return result;
        }

        @Override
        public void writeDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                 IOException {
            if (comment != null) {
                comment.write(writer, indent, currentNamespace);
            }
            writer.write(indent);
            if (isStatic) {
                writer.write("static ");
            }
            writer.write(type.getQualifiedName(currentNamespace) +
                         " " +
                         getQualifiedName(currentNamespace) +
                         getArraySize());
            if (!isStatic && initialValue != null) {
                writer.write(" = " + initialValue.getCode(currentNamespace));
            }
            writer.write(";\n");
        }

        @Override
        public void writeForwardDeclaration(final Writer writer,
                                            final String indent,
                                            final Namespace currentNamespace) throws IOException {
            writer.write(indent);
            writer.write("extern ");
            writer.write(type.getQualifiedName(currentNamespace) +
                         " " +
                         getQualifiedName(currentNamespace) +
                         getArraySize());
            writer.write(";\n");
        }

    }

    class VariableDefinition extends Definition {

        VariableDefinition(final Declaration declaration) {
            super(declaration);
        }

        public void writeCodeDefinition(final Writer writer,
                                        final String indent,
                                        final Namespace currentNamespace) throws IOException {
            writer.write(indent +
                         (isStatic ? "static " : "") +
                         type.getQualifiedName(currentNamespace) +
                         " " +
                         getQualifiedName(currentNamespace) +
                         getArraySize());
            writeInitialisation(writer, currentNamespace);
            writer.write(";");
        }

        public void writeInitialisation(final Writer writer, final Namespace currentNamespace) throws IOException {
            if (initialValue != null) {
                writer.write(TextUtils.alignTabs(" = " + initialValue.getCode(currentNamespace)));
            } else if (constructorParams != null) {
                final List<String> paramCode = new ArrayList<>();
                for (final Expression param : constructorParams) {
                    paramCode.add(param.getCode(currentNamespace));
                }

                writer.write("(" + TextUtils.alignTabs(TextUtils.formatList(paramCode, "", ", ", "") + ")"));

            }
        }

        @Override
        public void writeDefinition(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                IOException {
            writer.write(indent +
                         type.getQualifiedName(currentNamespace) +
                         " " +
                         getQualifiedName(currentNamespace) +
                         getArraySize());
            writeInitialisation(writer, currentNamespace);
            writer.write(";");
        }

        @Override
        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();
            if (initialValue != null) {
                result.addAll(initialValue.getForwardDeclarations());
            }
            if (constructorParams != null) {
                for (final Expression param : constructorParams) {
                    result.addAll(param.getForwardDeclarations());
                }
            }
            return result;
        }

        @Override
        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();

            // The chances are that if we are instantiating a type, we are going to
            // need full access to it's definition, regardless of whether it is a
            // reference or not. This might pull in more headers than strictly
            // necessary, but it's the best we can do.
            result.addAll(type.getNoRefDirectUsageIncludes());
            if (initialValue != null) {
                result.addAll(initialValue.getIncludes());
            }
            if (constructorParams != null) {
                for (final Expression param : constructorParams) {
                    result.addAll(param.getIncludes());
                }
            }
            return result;
        }
    }

    public static void writeParameterDeclaration(final Writer writer,
                                                 final List<Variable> variables,
                                                 final Namespace currentNamespace) throws IOException {
        TextUtils.formatList(writer, variables, "", var -> {
            String
                    varDef =
                    "\t" + var.type.getQualifiedName(currentNamespace) + "\t" + var.getQualifiedName(currentNamespace);
            if (var.initialValue != null) {
                varDef = varDef + TextUtils.alignTabs(" = " + var.initialValue.getCode(currentNamespace));
            }
            return varDef;
        }, ",\n", "");
    }

    public static void writeParameterDefinition(final Writer writer,
                                                final List<Variable> variables,
                                                final Namespace currentNamespace) throws IOException {
        TextUtils.formatList(writer,
                             variables,
                             "",
                             var -> "\t" +
                                    var.type.getQualifiedName(currentNamespace) +
                                    "\t" +
                                    var.getQualifiedName(currentNamespace),
                             ",\n",
                             "");
    }

    public String getParameterDefinition(final Namespace currentNamespace) {
        return type.getQualifiedName(currentNamespace) + " " + getQualifiedName(currentNamespace);
    }

    public Variable(final String name, final Namespace parentNamespace, final CodeFile codeFile) {
        this(null, name);
        setParentNamespace(parentNamespace);
        declaration.addDeclaredIn(codeFile);
    }

    public Variable(final String name) {
        this(null, name);
    }

    public Variable(final TypeUsage type, final String name) {
        this.type = type;
        this.name = name;
        this.initialValue = null;
        this.constructorParams = null;
    }

    public Variable(final TypeUsage type, final String name, final Expression initialValue) {
        this.type = type;
        this.name = name;
        this.initialValue = initialValue;
        this.constructorParams = null;
    }

    public Variable(final TypeUsage type, final String name, final List<Expression> constructorParams) {
        this.type = type;
        this.name = name;
        this.initialValue = null;
        this.constructorParams = constructorParams;
    }

    public Variable(final TypeUsage type, final String name, final Expression... constructorParams) {
        this(type, name, Arrays.asList(constructorParams));
    }

    public Variable(final TypeUsage type,
                    final String name,
                    final Namespace parentNamespace,
                    final Expression initialValue) {
        this(type, name, initialValue);
        setParentNamespace(parentNamespace);
    }

    public Variable(final TypeUsage type,
                    final String name,
                    final Namespace parentNamespace,
                    final Expression... constructorParams) {
        this(type, name, constructorParams);
        setParentNamespace(parentNamespace);
    }

    public Variable(final TypeUsage type,
                    final String name,
                    final Namespace parentNamespace,
                    final List<Expression> constructorParams) {
        this(type, name, constructorParams);
        setParentNamespace(parentNamespace);
    }

    public Variable(final TypeUsage type, final String name, final Namespace parentNamespace) {
        this(type, name);
        setParentNamespace(parentNamespace);
    }

    public void setArray() {
        setArraySize(0);
    }

    public void setArraySize(final Integer... dimensions) {
        arraySize = Arrays.asList(dimensions);
    }

    private List<Integer> arraySize = null;

    public Expression asExpression() {
        return new Expression() {

            @Override
            String getCode(final Namespace currentNamespace, final String alignment) {
                return getQualifiedName(currentNamespace);
            }

            @Override
            Set<Declaration> getForwardDeclarations() {
                final Set<Declaration> result = super.getForwardDeclarations();
                return result;
            }

            @Override
            Set<CodeFile> getIncludes() {
                final Set<CodeFile> result = super.getIncludes();
                result.addAll(getDeclaration().getUsageIncludes());
                return result;
            }

            @Override
            int getPrecedence() {
                return 0;
            }

            @Override
            public boolean isTemplateType() {
                return getType().isTemplateType();
            }

        };
    }

    private String getArraySize() {
        if (arraySize == null) {
            return "";
        } else {
            final StringBuilder buf = new StringBuilder();
            for (final Integer size : arraySize) {
                buf.append("[" + (size == 0 ? "" : String.valueOf(size)) + "]");
            }
            return buf.toString();
        }
    }

    public Expression asMemberReference(final Expression obj, final boolean pointer) {
        return new BinaryExpression(obj,
                                    pointer ? BinaryOperator.PTR_REF : BinaryOperator.OBJ_REF,
                                    this.asExpression());
    }

    public VariableDefinitionStatement asStatement() {
        return new VariableDefinitionStatement(this);
    }

    public boolean isStatic() {
        return isStatic;
    }

    /**
     * The comment to set.
     */
    public void setComment(final Comment comment) {
        this.comment = comment;
    }

    public void setStatic(final boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public String toString() {
        final Writer writer = new StringWriter();
        try {
            getDeclaration().writeDeclaration(writer, "", null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    Declaration getDeclaration() {
        return declaration;
    }

    Definition getDefinition() {
        return definition;
    }

    public String getName() {
        return name;
    }

    String getQualifiedName() {
        return getQualifiedName(null);
    }

    String getQualifiedName(final Namespace currentNamespace) {
        if (isMember ||
            getDeclaration().getParentNamespace() == null ||
            getDeclaration().getParentNamespace().contains(currentNamespace)) {
            return name;
        } else {
            return getDeclaration().getParentNamespace().getQualifiedName(currentNamespace) + "::" + name;
        }

    }

    public TypeUsage getType() {
        return type;
    }

    private void setParentNamespace(final Namespace parentNamespace) {
        getDeclaration().setParentNamespace(parentNamespace);
    }

    private Comment comment = null;
    private final List<Expression> constructorParams;
    private final Declaration declaration = new VariableDeclaration();
    private final Definition definition = new VariableDefinition(declaration);
    private final Expression initialValue;
    private boolean isStatic = false;
    final private String name;
    final private TypeUsage type;
    private boolean isMember = false;

    public void setMember() {
        isMember = true;
    }

}
