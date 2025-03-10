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
import java.util.List;
import java.util.Set;

public class FunctionPtrType extends Type {

    class FunctionPtrDeclaration extends Declaration {

        @Override
        public boolean equals(final Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs instanceof FunctionPtrDeclaration rhsDec) {
                return FunctionPtrType.this.equals(rhsDec.getFunctionPtrType());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return FunctionPtrType.this.hashCode();
        }

        @Override
        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();
            result.addAll(FunctionPtrType.this.returnType.getDirectUsageForwardDeclarations());
            for (final TypeUsage typeUsage : FunctionPtrType.this.parameterList) {
                result.addAll(typeUsage.getDirectUsageForwardDeclarations());
            }
            return result;
        }

        @Override
        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();
            result.addAll(FunctionPtrType.this.returnType.getDirectUsageIncludes());
            for (final TypeUsage typeUsage : FunctionPtrType.this.parameterList) {
                result.addAll(typeUsage.getDirectUsageIncludes());
            }
            return result;
        }

        @Override
        void writeDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                          IOException {
            final StringBuilder definition = new StringBuilder();

            final List<String> strings = new ArrayList<>(FunctionPtrType.this.parameterList.size());
            for (final TypeUsage param : parameterList) {
                strings.add(param.getQualifiedName(currentNamespace));
            }

            definition.append(indent +
                              returnType.getQualifiedName(currentNamespace) +
                              TextUtils.formatList(strings, " (", ",", ")"));

            // The TextUtils.formatList call above will just append the return type to
            // the
            // definition if the function pointer has an empty list of parameter
            // types. This
            // is not what is required for function pointers. I have looked at fixing
            // the
            // formatList method but it is used everywhere and is a bit hairy!!.
            // Therefore
            // just patch the definition.
            if (strings.size() == 0) {
                definition.append("()");
            }
            TextUtils.alignTabs(writer, definition.toString());
        }

        @Override
        void writeForwardDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                 IOException {
            writeDeclaration(writer, indent, currentNamespace);
        }

        /**
         * Gets the FunctionPtrType that this declaration declares
         *
         * @return The FunctionPtrType
         */
        private FunctionPtrType getFunctionPtrType() {
            return FunctionPtrType.this;
        }
    }

    public FunctionPtrType() {
        super("");
    }

    public FunctionPtrType(final String name) {
        super(name);
    }

    public FunctionPtrType(final Namespace parentNamespace) {
        super("", parentNamespace);
        getDeclaration().setParentNamespace(parentNamespace);
    }

    public FunctionPtrType(final String name, final Namespace parentNamespace) {
        super(name, parentNamespace);
        getDeclaration().setParentNamespace(parentNamespace);
    }

    public void addReturnType(final TypeUsage retType) {
        returnType = retType;
    }

    public void addParameterType(final TypeUsage paramType) {
        parameterList.add(paramType);
    }

    public void addParameterType(final List<TypeUsage> types) {
        parameterList.addAll(types);
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

    @Override
    Declaration getDeclaration() {
        return declaration;
    }

    @Override
    Set<CodeFile> getDirectUsageIncludes() {
        final Set<CodeFile> result = super.getDirectUsageIncludes();
        result.addAll(declaration.getUsageIncludes());
        return result;
    }

    @Override
    Set<CodeFile> getNoRefDirectUsageIncludes() {
        final Set<CodeFile> result = super.getNoRefDirectUsageIncludes();
        result.addAll(declaration.getUsageIncludes());
        return result;
    }

    @Override
    Set<CodeFile> getIndirectUsageIncludes() {
        // We could redeclare the type locally, as a forward declaration, but less
        // messy in the generated code to include the file.
        return getDirectUsageIncludes();
    }

    @Override
    boolean preferPassByReference() {
        return false;
    }

    @Override
    String getQualifiedName(final Namespace currentNamespace) {
        final StringWriter stringWrt = new StringWriter();
        try {
            declaration.writeDeclaration(stringWrt, "", currentNamespace);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
            stringWrt.append("GENERATOR_ERROR");
        }
        return stringWrt.toString();
    }

    private final Declaration declaration = new FunctionPtrDeclaration();

    private TypeUsage returnType = new TypeUsage(FundamentalType.VOID);
    private final List<TypeUsage> parameterList = new ArrayList<>();

}
