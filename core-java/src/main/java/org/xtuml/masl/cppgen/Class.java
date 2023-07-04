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
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Represents a C++ class. It provides mechamisms for constructing and using
 * this definition, and writing it to a string. The members of a class may be
 * split into a number of groups. The order in which these groups are inserted
 * into the declaration is defined by the order in which they are added into the
 * class.
 */

public final class Class extends Type {

    /**
     * Inner class providing the Declaration functionality for the class.
     */
    private class ClassDeclaration extends Declaration {

        @Override
        public boolean equals(final Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs instanceof ClassDeclaration rhsCl) {
                return Class.this.equals(rhsCl.getDeclarable());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getDeclarable().hashCode();
        }

        @Override
        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();

            for (final DeclarationGroup dec : declarationGroups) {
                result.addAll(dec.getForwardDeclarations());
            }

            for (final TemplateSpecialisation spec : templateSpecialisations) {
                result.addAll(spec.getDirectUsageForwardDeclarations());
            }

            // No need to forward declare ourself, even if some members think they
            // need it.
            result.remove(this);

            return result;
        }

        @Override
        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();

            for (final DeclarationGroup dec : declarationGroups) {
                result.addAll(dec.getIncludes());
            }

            for (final Superclass sup : superclasses) {
                result.addAll(sup.getDeclarationIncludes());
            }

            for (final TemplateSpecialisation spec : templateSpecialisations) {
                result.addAll(spec.getDirectUsageIncludes());
            }

            return result;
        }

        @Override
        void writeDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                          IOException {
            writeComment(writer, indent, currentNamespace);
            if (templateParameters.size() == 0 && templateSpecialisations.size() > 0) {
                writer.write(indent + "template<>\n");
            }

            final List<String> templateParamNames = new ArrayList<String>();
            for (final TemplateParameter templateParameter : templateParameters) {
                templateParamNames.add(templateParameter.getName());
            }

            writer.write(TextUtils.formatList(templateParamNames, indent + "template<", ", ", ">\n") +
                         indent +
                         "class " +
                         getQualifiedName(currentNamespace));
            TextUtils.formatList(writer,
                                 superclasses,
                                 "\n" + indent + TextUtils.getIndent() + ": ",
                                 ",\n" + indent + TextUtils.getIndent() + "  ",
                                 "");

            writer.write("\n" + indent + "{\n\n");

            writeChildDeclarations(writer, indent + TextUtils.getIndent(), thisNamespace);

            writer.write(indent + "};\n");
        }

        @Override
        void writeForwardDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                 IOException {
            writer.write(indent + "class " + getQualifiedName(currentNamespace) + ";\n");
        }

        /**
         * Get the class that this is the declaration for
         *
         * @return the class
         */
        private Class getDeclarable() {
            return Class.this;
        }

    }

    /**
     * Defines the superclasses of a class, including the visibility of that
     * superclass.
     */
    private class Superclass {

        /**
         * Constructs a superclass definition
         * <p>
         * <p>
         * the type of the superclass
         * <p>
         * the visibiliyy of the superclass
         */
        Superclass(final Class type, final Visibility visibility) {
            this.type = type;
            this.visibility = visibility;
        }

        @Override
        public String toString() {
            return visibility + " " + type.getQualifiedName(thisNamespace);
        }

        /**
         * @return the list of includes needed to declare this superclass
         */
        Set<CodeFile> getDeclarationIncludes() {
            return type.getDirectUsageIncludes();
        }

        /**
         * The class of the superclass
         */
        private final Class type;

        /**
         * The visibility of the superclass
         */
        private final Visibility visibility;

    }

    /**
     * Constructs a class in the global namespace
     * <p>
     * <p>
     * The name of this class
     */
    public Class(final String name) {
        this(name, (Namespace) null);
    }

    /**
     * Constructs a class in the global namespace, which is declared in the
     * supplied include file. This is typically used for classes declared outside
     * the code currently being generated, where a full class definition is not
     * needed.
     * <p>
     * <p>
     * the name of the class to be created
     * <p>
     * the include file in which it is declared.
     */
    public Class(final String name, final CodeFile declaredIn) {
        this(name, null, declaredIn);
    }

    /**
     * Constructs a class contained in the specified namespace or parent class.
     * <p>
     * <p>
     * the name of the class to be created
     * <p>
     * the parent namespace or class
     */
    public Class(final String name, final Namespace parentNamespace) {
        super(name, parentNamespace);
        thisNamespace = new Namespace(name) {

            @Override
            public String getQualifiedName(final Namespace currentNamespace) {
                return Class.this.getQualifiedName(currentNamespace);
            }
        };
        setParentNamespace(parentNamespace);
    }

    /**
     * Constructs a class in contained in the specified namespace or parent class,
     * which is declared in the supplied include file. This is typically used for
     * classes declared outside the code currently being generated.
     * <p>
     * <p>
     * the name of the class to be created
     * <p>
     * the namespace to contain this class
     * <p>
     * the include file in which it is declared.
     */
    public Class(final String name, final Namespace namespace, final CodeFile declaredIn) {
        this(name, namespace);
        getDeclaration().addDeclaredIn(declaredIn);
        noForwardDec = true;

    }

    /**
     * Constructs a class in contained in the specified namespace or parent class,
     * which is declared in the supplied include files. This is typically used for
     * classes declared outside the code currently being generated, where a full
     * class definition is not needed.
     * <p>
     * <p>
     * the name of the class to be created
     * <p>
     * the namespace to contain this class
     * <p>
     * the include files in which it is declared.
     */
    public Class(final String name, final Namespace namespace, final Set<CodeFile> declaredIn) {
        this(name, namespace);
        getDeclaration().addDeclaredIn(declaredIn);
        noForwardDec = true;

    }

    /**
     * Adds an enumeration definition to the class
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The enumeration to add
     * <p>
     * The visibility of the typedef
     */
    public void addEnumeration(final DeclarationGroup groupKey,
                               final EnumerationType enumeration,
                               final Visibility visibility) {
        addDeclaration(groupKey, enumeration.getDeclaration(), visibility);
        enumeration.setParentNamespace(thisNamespace);
    }

    /**
     * Adds a nested class into the specified group
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the class to add
     * <p>
     * the visibility of the new class
     */
    public void addNestedClass(final DeclarationGroup groupKey, final Class clazz, final Visibility visibility) {
        addDeclaration(groupKey, clazz.getDeclaration(), visibility);
        clazz.setParentNamespace(thisNamespace);
    }

    /**
     * Adds a new superclass to the declaration of the class.
     * <p>
     * <p>
     * the class to be the new superclass
     * <p>
     * the visibility of the inheritance relationship
     */
    public void addSuperclass(final Class superclass, final Visibility visibility) {
        superclasses.add(new Superclass(superclass, visibility));
    }

    /**
     * Adds a template parameter to the class. Parameters will appear in the
     * definition of the class in the order they are added. This parameter can be
     * used in the rest of the class definition the same as any other type.
     * <p>
     * <p>
     * The template parameter to add.
     */
    public void addTemplateParameter(final TemplateParameter param) {
        templateParameters.add(param);
    }

    /**
     * Adds a non-type specialisation to the class. This is used when fully or
     * partially specialising a class definition, or when declaring an instance of
     * a templated class.
     * <p>
     * <p>
     * The expression to use as the template parameter value
     */
    public void addTemplateSpecialisation(final Expression param) {
        thisNamespace.addTemplateSpecialisation(param);
        templateSpecialisations.add(TemplateSpecialisation.create(param));
    }

    /**
     * Adds a type specialisation to the class. This is used when fully or
     * partially specialising a class definition, or when declaring an instance of
     * a templated class.
     * <p>
     * <p>
     * The expression to use as the template parameter value
     */
    public void addTemplateSpecialisation(final TypeUsage param) {
        thisNamespace.addTemplateSpecialisation(param);
        templateSpecialisations.add(TemplateSpecialisation.create(param));
    }

    /**
     * Adds a typedef declaration to the class
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The typedef to add
     * <p>
     * The visibility of the typedef
     */
    public void addTypedef(final DeclarationGroup groupKey, final TypedefType typedef, final Visibility visibility) {
        addDeclaration(groupKey, typedef.getDeclaration(), visibility);
        typedef.setParentNamespace(thisNamespace);
    }

    /**
     * Creates a function call to a constructor on the class. Note that this does
     * not actually add a constructor to the class definition, but simply returns
     * code to call a constructor. This is normally used to call constructors on
     * externally defined classes, where a full class definition is not needed.
     * <p>
     * <p>
     * A list of paramters to pass to the constructor
     *
     * @return a call to a constructor on the class
     */
    @Override
    public FunctionCall callConstructor(final List<Expression> params) {
        final Function function = new Function(getName());
        function.setReturnType(null);
        function.setConstructor(true);
        function.addTemplateSpecialisation(templateSpecialisations);
        function.getDeclaration().setParentClass(this);
        return function.asFunctionCall(params);
    }

    /**
     * Convenience wrapper around Class#callStaticFunction(String,List) generating
     * a function call with one parameter
     * <p>
     * <p>
     * the name of a function to call
     * <p>
     * A list of paramters to pass to the function
     *
     * @return an expression for the function call
     */
    public FunctionCall callStaticFunction(final String name, final Expression... params) {
        return callStaticFunction(name, Arrays.asList(params));
    }

    /**
     * Creates a function call to a static function with the given name on the
     * class. Note that this does not actually add a function to the class
     * definition, but simply returns code to call a function. This is normally
     * used to call functions on externally defined classes, where a full class
     * definition is not needed.
     * <p>
     * <p>
     * The name of the function to call
     * <p>
     * A list of paramters to pass to the function
     *
     * @return a call to a constructor on the class
     */
    public FunctionCall callStaticFunction(final String name, final List<Expression> params) {
        final Function function = new Function(name);
        function.setStatic(true);
        function.getDeclaration().setParentClass(this);
        return function.asFunctionCall(params);
    }

    /**
     * Adds an assignment operator to the class with the recommended parameters
     * for an assignement operator. The resulting function is returned ready for
     * the client to populate it with appropriate code.
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The visibility of the resulting operator
     *
     * @return the operator added
     */
    public Function createAssignmentOperator(final DeclarationGroup declarationGroup, final Visibility visibility) {
        final Function function = new Function("operator=");
        function.setReturnType(new TypeUsage(this, TypeUsage.Reference));
        function.createParameter(new TypeUsage(this, TypeUsage.ConstReference), "rhs");
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Adds a constructor to the class. The resulting function is returned ready
     * for the client to populate it with appropriate parameters and code.
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The visibility of the resulting constructor
     *
     * @return the constructor added
     */
    public Function createConstructor(final DeclarationGroup declarationGroup, final Visibility visibility) {
        final Function function = new Function(getName());
        function.setReturnType(null);
        function.setConstructor(true);
        function.addTemplateSpecialisation(templateSpecialisations);
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Adds a constructor to the class with the recommended parameters for a copy
     * constructor. The resulting function is returned ready for the client to
     * populate it with appropriate code.
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The visibility of the resulting constructor
     *
     * @return the constructor added
     */
    public Function createCopyConstructor(final DeclarationGroup declarationGroup, final Visibility visibility) {
        final Function function = new Function(getName());
        function.setReturnType(null);
        function.setConstructor(true);
        function.createParameter(new TypeUsage(this, TypeUsage.ConstReference), "rhs");
        function.addTemplateSpecialisation(templateSpecialisations);
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Creates a new declaration group to contain declarations within the class.
     * Each group is output in the order that it is added. Note that this grouping
     * is purely cosmetic, and is simply used to aid readability of the resulting
     * code. It might, for example, be used to group contructors together at the
     * top of a class, or private members at the bottom.
     *
     * @return the declaration group created
     */
    public DeclarationGroup createDeclarationGroup() {
        final DeclarationGroup group = new DeclarationGroup();
        declarationGroups.add(group);
        return group;
    }

    /**
     * Creates a new declaration group to contain declarations within the class.
     * Each group is output in the order that it is added, and will be displayed
     * with the supplied comment at the top. Note that this grouping is purely
     * cosmetic, and is simply used to aid readability of the resulting code. It
     * might, for example, be used to group contructors together at the top of a
     * class, or private members at the bottom.
     * <p>
     * <p>
     * a comment to add at the top of the group declaration
     *
     * @return the declaration group created
     */
    public DeclarationGroup createDeclarationGroup(final String comment) {
        final DeclarationGroup group = new DeclarationGroup(comment);
        declarationGroups.add(group);
        return group;
    }

    /**
     * Adds a destructor to the class. The resulting function is returned ready
     * for the client to populate it with appropriate code.
     * <p>
     * <p>
     * The declaration group to add into
     * <p>
     * The visibility of the resulting destructor
     *
     * @return the destructor added
     */
    public Function createDestructor(final DeclarationGroup declarationGroup, final Visibility visibility) {
        final Function function = new Function("~" + getName());
        function.setDestructor(true);
        function.setReturnType(null);
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Adds a member function into the specified group. The function is returned
     * to the client ready for population with appropriate code.
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the name of the function to add
     * <p>
     * the visibility of the new function
     *
     * @return the function that was created
     */
    public Function createMemberFunction(final DeclarationGroup declarationGroup,
                                         final String name,
                                         final Visibility visibility) {
        final Function function = new Function(name);
        function.setMember();
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    public Function createCastFunction(final DeclarationGroup declarationGroup,
                                       final TypeUsage type,
                                       final Visibility visibility) {
        final Function function = new Function("operator");
        function.setMember();
        function.setReturnType(type);
        function.setCast(true);
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Returns a specialised version of a member function. The function is
     * returned to the client ready for population with appropriate code and
     * addition to a code file.
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the name of the function to add
     * <p>
     * the visibility of the new function
     *
     * @return the function that was created
     */
    public Function specialiseMemberFunction(final String name, final TemplateSpecialisation... specialisations) {
        final Function function = new Function(name);
        function.setMember();
        function.getDeclaration().setParentClass(this);
        function.addTemplateSpecialisation(specialisations);
        return function;
    }

    /**
     * Adds a member variable into the specified group
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the name of the variable to add
     * <p>
     * the type of the variable to add
     * <p>
     * the visibility of the new class
     *
     * @return the function that was created
     */
    public Variable createMemberVariable(final DeclarationGroup declarationGroup,
                                         final String name,
                                         final TypeUsage type,
                                         final Visibility visibility) {
        final Variable variable = new Variable(type, name);
        variable.setMember();
        addDeclaration(declarationGroup, variable.getDeclaration(), visibility);
        declarationGroup.addVariable(variable);
        return variable;
    }

    /**
     * Adds a static function into the specified group. The function is returned
     * to the client ready for population with appropriate code.
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the name of the function to add
     * <p>
     * the visibility of the new function
     *
     * @return the function that was created
     */
    public Function createStaticFunction(final DeclarationGroup declarationGroup,
                                         final String name,
                                         final Visibility visibility) {
        final Function function = new Function(name);
        function.setStatic(true);
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Adds a static variable into the specified group
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the name of the varible to add
     * <p>
     * the type of the varible to add
     * <p>
     * the value to initialise the variable with in the definition.
     * <p>
     * the visibility of the new class
     *
     * @return the variable that was created
     */
    public Variable createStaticVariable(final DeclarationGroup declarationGroup,
                                         final String name,
                                         final TypeUsage type,
                                         final Expression initialValue,
                                         final Visibility visibility) {
        final Variable variable = new Variable(type, name, initialValue);
        variable.setStatic(true);
        addDeclaration(declarationGroup, variable.getDeclaration(), visibility);
        return variable;
    }

    @Override
    public boolean equals(final Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs instanceof Class rhsCl) {
            return super.equals(rhs) && templateSpecialisations.equals(rhsCl.templateSpecialisations);
        }
        return false;
    }

    /**
     * Returns an expression representing a pointer to a member function of this
     * class. Note that this does not actually add a function to the class
     * definition, but simply returns the function pointer expression. This is
     * normally used for functions on externally defined classes, where a full
     * class definition is not needed.
     * <p>
     * <p>
     * the name of the function to return a pointer to
     *
     * @return an expression representing a pointer to a member function of the
     * given name.
     */
    public Expression getFunctionPointer(final String name) {
        final Function function = new Function(name);
        function.getDeclaration().setParentClass(this);
        return function.asFunctionPointer();
    }

    /**
     * Gets a list of the superclasses of this class
     *
     * @return the superclasses
     */
    List<Class> getSuperclasses() {
        final List<Class> result = new ArrayList<Class>();
        for (final Superclass superclass : superclasses) {
            result.add(superclass.type);
        }
        return result;
    }

    /**
     * returns a variable representing 'this' for the current class.
     *
     * @return this
     */
    public Variable getThis() {
        return thisVar;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ templateSpecialisations.hashCode();
    }

    private boolean forceTemplate;

    @Override
    public boolean isTemplateType() {
        if (isForceTemplate()) {
            return true;
        }
        for (final TemplateSpecialisation templateSpecialisation : templateSpecialisations) {
            if (templateSpecialisation.isTemplateType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Overrides a member function from a superclass in the current class. The
     * function must be defined as virtual. The resulting function will have the
     * same interface as the supplied function. The return type may be overridden
     * later by a subclass of the current return type.
     * <p>
     * <p>
     * the key of the required declaration group
     * <p>
     * the function to override
     * <p>
     * the visibility of the new function
     *
     * @return the function that was created
     */
    public Function redefineFunction(final DeclarationGroup declarationGroup,
                                     final Function superFunction,
                                     final Visibility visibility) {
        if (!superFunction.isVirtual()) {
            throw new IllegalStateException("Attempt to redefine non-virtual function");
        }
        final Function function = new Function(superFunction.getName());
        function.setConst(superFunction.isConst());
        function.setVirtual(superFunction.isVirtual());
        function.setReturnType(superFunction.getReturnType());
        function.setParameters(superFunction.getParameters());
        function.setMember();
        addDeclaration(declarationGroup, function.getDeclaration(), visibility);
        return function;
    }

    /**
     * Returns a type representing a type nested or typedefed in this class. Note
     * that this does not actually add a nested type to the class definition, but
     * simply returns type. This is normally used for types on externally defined
     * classes, where a full class definition is not needed.
     * <p>
     * <p>
     * the name of the nested type
     *
     * @return a class representing the nested type
     */
    public Class referenceNestedType(final String name) {
        final Class nestedClass = new Class(name, getNamespace());
        nestedClass.getDeclaration().setParentClass(this);
        return nestedClass;
    }

    /**
     * Returns an expression representing a static member this class. Note that
     * this does not actually add a member to the class definition, but simply
     * returns type. This is normally used for members on externally defined
     * classes, where a full class definition is not needed.
     * <p>
     * <p>
     * the name of the member
     *
     * @return an expression representing the member
     */
    public Expression referenceStaticMember(final String name) {
        final Variable var = new Variable(name);
        var.setStatic(true);
        var.getDeclaration().setParentClass(this);
        return var.asExpression();
    }

    /**
     * Sets the comment to appear at the top of the declaration
     * <p>
     * <p>
     * the text of the comment
     */
    public void setComment(final String text) {
        this.comment = Comment.createComment(text);
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
    public Declaration getDeclaration() {
        return declaration;
    }

    public void addDeclaredIn(final CodeFile declaredIn) {
        getDeclaration().addDeclaredIn(declaredIn);
    }

    @Override
    Set<Declaration> getDirectUsageForwardDeclarations() {
        final Set<Declaration> result = new LinkedHashSet<Declaration>();
        for (final TemplateSpecialisation spec : templateSpecialisations) {
            result.addAll(spec.getDirectUsageForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getDirectUsageIncludes() {
        final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
        for (final TemplateSpecialisation spec : templateSpecialisations) {
            result.addAll(spec.getDirectUsageIncludes());
        }
        if (declaration.getParentClass() == null) {
            result.addAll(super.getDirectUsageIncludes());
            result.addAll(declaration.getUsageIncludes());
        } else {
            result.addAll(declaration.getParentClass().getDirectUsageIncludes());
        }

        return result;
    }

    @Override
    Set<CodeFile> getNoRefDirectUsageIncludes() {
        final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
        for (final TemplateSpecialisation spec : templateSpecialisations) {
            result.addAll(spec.getNoRefDirectUsageIncludes());
        }
        if (declaration.getParentClass() == null) {
            result.addAll(super.getNoRefDirectUsageIncludes());
            result.addAll(declaration.getUsageIncludes());
        } else {
            result.addAll(declaration.getParentClass().getNoRefDirectUsageIncludes());
        }

        return result;
    }

    @Override
    Set<Declaration> getIndirectUsageForwardDeclarations() {
        final Set<Declaration> result = new LinkedHashSet<Declaration>();
        for (final TemplateSpecialisation spec : templateSpecialisations) {
            result.addAll(spec.getIndirectUsageForwardDeclarations());
        }

        if (!noForwardDec && declaration.getParentClass() == null) {
            result.add(declaration);
        }
        return result;
    }

    @Override
    Set<CodeFile> getIndirectUsageIncludes() {
        final Set<CodeFile> result = new LinkedHashSet<CodeFile>();

        if (declaration.getParentClass() == null) {
            if (noForwardDec) {
                for (final TemplateSpecialisation spec : templateSpecialisations) {
                    result.addAll(spec.getIndirectUsageIncludes());
                }
                result.addAll(super.getIndirectUsageIncludes());
                result.addAll(declaration.getUsageIncludes());
            }
        } else {
            result.addAll(declaration.getParentClass().getDirectUsageIncludes());
        }
        return result;
    }

    /**
     * Returns a list of all non-static member variables declared in this class,
     * in the same order that they appear in the declaration of the class. These
     * are used by the constructors to initialise the values, the order being
     * consistent with the class order to comply with Meyers Effective C++ 2nd
     * Edition Item 13.
     *
     * @return A list of member variables declared in this class
     */
    List<Variable> getMemberVariables() {
        final List<Variable> result = new ArrayList<Variable>();

        for (final DeclarationGroup group : declarationGroups) {
            result.addAll(group.getVariables());
        }

        return result;

    }

    /**
     * @return the namespace that this class defines
     */
    public Namespace getNamespace() {
        return thisNamespace;
    }

    @Override
    String getQualifiedName(final Namespace currentNamespace) {
        // In c++ if a nested type is used that belongs to a templated
        // parent class, the type declaration needs to include the
        // typename qualifier.
        String typenameQualifier = "";
        if (this.getDeclaration().getParentClass() != null && this.getDeclaration().getParentClass().isTemplateType()) {
            typenameQualifier = "typename ";
        }

        final List<String> params = new ArrayList<String>();
        for (final TemplateSpecialisation param : templateSpecialisations) {
            params.add(param.getValue(currentNamespace));
        }
        // Avoid using '<:', as this parses as a digraph
        final String startTemplate = params.size() > 0 && ((params.get(0)).startsWith(":")) ? "< " : "<";

        // Avoid using '>>' as this parses as the right shift operator
        final String endTemplate = params.size() > 0 && ((params.get(params.size() - 1)).endsWith(">")) ? " >" : ">";

        return typenameQualifier +
               super.getQualifiedName(currentNamespace) +
               TextUtils.formatList(params, startTemplate, ",", endTemplate);
    }

    @Override
    /**
     * {@inheritDoc}A class should normally be passed by reference rather than by
     * value if possible, (ie 'const Class&' as opposed to 'Class') so this will
     * always return true.
     *
     * @return true
     */
    boolean preferPassByReference() {
        return true;
    }

    @Override
    void setParentNamespace(final Namespace parentNamespace) {
        super.setParentNamespace(parentNamespace);
        getDeclaration().setParentNamespace(parentNamespace);
        thisNamespace.setParentNamespace(parentNamespace);
    }

    /**
     * Writes all the declarations contained in this class to the supplied writer.
     * <p>
     * <p>
     * the writer to write to
     * <p>
     * the namespace that we are currently writing declarations for
     * <p>
     * the initial indentation level for the code
     *
     * @throws IOException
     */
    void writeChildDeclarations(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                            IOException {
        for (final DeclarationGroup group : declarationGroups) {
            group.write(writer, indent, currentNamespace);
        }
    }

    /**
     * If a comment has been set, write it to the writer with the correct
     * indentation
     * <p>
     * <p>
     * the writer to write to
     * <p>
     * the indentation for the comment
     * <p>
     * the namespace that we are currently writing declarations for
     *
     * @throws IOException
     */
    void writeComment(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        if (comment != null) {
            comment.write(writer, indent, currentNamespace);
        }
    }

    /**
     * Adds a declaration into the specified group
     * <p>
     * <p>
     * the required declaration group
     * <p>
     * the declaration to add
     * <p>
     * the visibility of the new class
     */
    private void addDeclaration(final DeclarationGroup declarationGroup,
                                final Declaration declaration,
                                final Visibility visibility) {
        declaration.setVisibility(visibility);
        declaration.setParentClass(this);
        declarationGroup.add(declaration);

    }

    /**
     * A comment on the declaration. This will appear above the main declaration.
     */
    private Comment comment = null;

    /**
     * Handles all things to do with a declaration of this class.
     */
    private final Declaration declaration = new ClassDeclaration();

    /**
     * The order in which the {@link DeclarationGroup}s appear in the class
     * declaration. This is set up by the order in which the declaration groups
     * are added to the class.
     */
    private final List<DeclarationGroup> declarationGroups = new ArrayList<DeclarationGroup>();

    /**
     * Whether this class can be forward declared. Normally classes which we have
     * fully defined can be forward declared, but those which are just referenced
     * from external include files can not.
     */
    private boolean noForwardDec = false;

    /**
     * An ordered list of the superclasses of this class.
     */
    private final List<Superclass> superclasses = new ArrayList<Superclass>();

    /**
     * An ordered list of the template parameters of this class.
     */
    private final List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();

    /**
     * An ordered list of the template parameter specialisations of this class.
     */
    private final List<TemplateSpecialisation> templateSpecialisations = new ArrayList<TemplateSpecialisation>();

    /**
     * The namespace that is defined by this class
     */
    private final Namespace thisNamespace;

    /**
     * A variable representing the 'this' pointer of the class.
     */
    private final Variable thisVar = new Variable(new TypeUsage(this, TypeUsage.Pointer), "this");

    List<TemplateParameter> getTemplateParameters() {
        return templateParameters;
    }

    List<TemplateSpecialisation> getTemplateSpecialisations() {
        return templateSpecialisations;
    }

    public boolean isForceTemplate() {
        return forceTemplate;
    }

    public void setForceTemplate(boolean forceTemplate) {
        this.forceTemplate = forceTemplate;
    }

}
