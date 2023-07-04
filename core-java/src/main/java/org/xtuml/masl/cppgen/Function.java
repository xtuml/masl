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
package org.xtuml.masl.cppgen;

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Creates code to declare and define a C++ function
 */
public class Function {

    /**
     * The declaration for the enclosing function
     */
    private class FunctionDeclaration extends Declaration {

        @Override
        public Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();
            if (returnType != null) {
                result.addAll(returnType.getIndirectUsageForwardDeclarations());
            }

            for (final Variable arg : parameters) {
                result.addAll(arg.getType().getIndirectUsageForwardDeclarations());
            }

            if (code != null && isDeclaredInClass) {
                result.addAll(definition.getForwardDeclarations());
            }

            return result;
        }

        @Override
        public Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();
            if (returnType != null) {
                result.addAll(returnType.getIndirectUsageIncludes());
            }

            for (final Variable arg : parameters) {
                result.addAll(arg.getType().getIndirectUsageIncludes());
            }

            if (specialisationFrom != null) {
                result.add(specialisationFrom);
            }

            if (code != null && isDeclaredInClass) {
                result.addAll(definition.getIncludes());
            }

            return result;
        }

        @Override
        public void writeDeclaration(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                 IOException {
            writeComment(writer, indent, currentNamespace);

            final StringWriter definition = new StringWriter();

            final List<String> templateParamNames = new ArrayList<String>();
            for (final TemplateParameter templateParameter : templateParameters) {
                templateParamNames.add(templateParameter.getName());
            }

            definition.write(TextUtils.formatList(templateParamNames, indent + "template<", ", ", ">\n"));
            definition.write(indent);
            if (isExternC) {
                definition.write("Extern \"C\" ");
            }
            if (isVirtual) {
                definition.write("virtual ");
            }
            if (isStatic) {
                definition.write("static ");
            }
            if (isExplicit) {
                definition.write("explicit ");
            }
            if (isDeclaredInClass && !(isMember || isConstructor || isDestructor)) {
                definition.write("inline ");
            } else {
                if (isInline) {
                    definition.write("inline ");
                }
            }

            if (isCast) {
                definition.write(name + " " + returnType.getQualifiedName(getParentNamespace()) + " (");
            } else {
                if (returnType != null) {
                    definition.write(returnType.getQualifiedName(getParentNamespace()) + " ");
                }
                definition.write(name + " (");
            }

            Variable.writeParameterDeclaration(definition, parameters, getParentNamespace());

            definition.write(" )");
            definition.write(isConst ? " const" : "");

            if (isPure) {
                definition.write(" = 0");
            }

            TextUtils.alignTabs(writer, definition.toString());

            if ((isDestructor || !isPure) && isDeclaredInClass) {
                if (isDeclaredInClass) {
                    if (isConstructor()) {
                        final List<String> inits = new ArrayList<String>();

                        final List<Class> superclasses = getDeclaration().getParentClass().getSuperclasses();

                        for (final Class clazz : superclasses) {
                            final List<Expression> params = superclassArgs.get(clazz);
                            if (params != null && params.size() > 0) {
                                final List<String> paramCode = new ArrayList<String>();
                                for (final Expression param : params) {
                                    paramCode.add(param.getCode(declaration.getParentNamespace()));
                                }

                                inits.add(clazz.getQualifiedName(declaration.getParentNamespace()) +
                                          "(" +
                                          TextUtils.formatList(paramCode, "", " ", "", ", ", "") +
                                          ")");
                            }
                        }

                        final List<Variable> memberVariables = getDeclaration().getParentClass().getMemberVariables();
                        for (final Variable var : memberVariables) {
                            final Expression initialExp = memberValues.get(var);
                            final String
                                    initialVal =
                                    initialExp == null ? "" : initialExp.getCode(declaration.getParentNamespace());
                            inits.add(var.getQualifiedName(declaration.getParentNamespace()) + "(" + initialVal + ")");
                        }

                        writer.write("\n");
                        TextUtils.formatList(writer,
                                             inits,
                                             indent + TextUtils.getIndent() + ": ",
                                             ",\n" + indent + TextUtils.getIndent() + "  ",
                                             "\n");

                    }
                }
                code.write(writer, indent, getParentNamespace(), true);
            } else {
                writer.write(";");
            }

            writer.write("\n");
        }

        @Override
        public void writeForwardDeclaration(final Writer writer,
                                            final String indent,
                                            final Namespace currentNamespace) throws IOException {

            final StringWriter definition = new StringWriter();

            final List<String> templateParamNames = new ArrayList<String>();
            for (final TemplateParameter templateParameter : templateParameters) {
                templateParamNames.add(templateParameter.getName());
            }

            if (isExternC) {
                definition.append("extern \"C\" ");
            }

            definition.write(TextUtils.formatList(templateParamNames, indent + "template<", ", ", ">\n"));

            definition.write(indent);

            if (returnType != null) {
                definition.write(returnType.getQualifiedName(getParentNamespace()) + " ");
            }
            definition.write(name + " (");

            Variable.writeParameterDeclaration(definition, parameters, getParentNamespace());

            definition.write(" )");

            TextUtils.alignTabs(writer, definition.toString());

            writer.write(";\n");
        }

    }

    /**
     * The definition for the enclosing function
     */
    private class FunctionDefinition extends Definition {

        /**
         * Creates a definition for the supplied declaration
         * <p>
         * <p>
         * the declaration to define
         */
        FunctionDefinition(final Declaration declaration) {
            super(declaration);
        }

        @Override
        public void writeDefinition(final Writer writer, final String indent, final Namespace currentNamespace) throws
                                                                                                                IOException {
            if (isDestructor || !isPure) {
                final StringWriter definition = new StringWriter();
                writeComment(definition, indent, currentNamespace);

                if (isExternC) {
                    definition.append("extern \"C\" ");
                }

                final List<String> templateParamNames = new ArrayList<String>();
                for (final TemplateParameter templateParameter : templateParameters) {
                    templateParamNames.add(templateParameter.getName());
                }

                Class parentClass = getDeclaration().getParentClass();
                while (parentClass != null) {
                    final List<String> parentTemplateParamNames = new ArrayList<String>();
                    for (final TemplateParameter templateParameter : parentClass.getTemplateParameters()) {
                        parentTemplateParamNames.add(templateParameter.getName());
                    }
                    if (parentClass.getTemplateSpecialisations().size() > 0 && parentTemplateParamNames.size() == 0) {
                        definition.write(indent + "template<>\n");
                    } else {
                        definition.write(TextUtils.formatList(parentTemplateParamNames,
                                                              indent + "template<",
                                                              "",
                                                              "",
                                                              ", ",
                                                              ">\n"));
                    }

                    parentClass = parentClass.getDeclaration().getParentClass();
                }

                if ((isImplicitSpecialization || templateSpecialisations.size() > 0) &&
                    templateParamNames.size() == 0) {
                    definition.write(indent + "template<>\n");
                } else {
                    definition.write(TextUtils.formatList(templateParamNames,
                                                          indent + "template<",
                                                          "",
                                                          "",
                                                          ", ",
                                                          ">\n"));
                }
                definition.write(indent);

                if (isInline) {
                    definition.append("inline ");
                }

                if (isCast) {
                    definition.write(getQualifiedName(currentNamespace) +
                                     " " +
                                     returnType.getQualifiedName(currentNamespace) +
                                     " (");
                } else {
                    if (returnType != null) {
                        definition.write(returnType.getQualifiedName(currentNamespace) + " ");
                    }
                    definition.write(getQualifiedName(currentNamespace) + " (");
                }

                Variable.writeParameterDefinition(definition, parameters, declaration.getParentNamespace());

                definition.write(" )");
                definition.write(isConst ? " const" : "");

                TextUtils.alignTabs(writer, definition.toString());
                writer.write("\n");

                if (isConstructor()) {
                    final List<String> inits = new ArrayList<String>();

                    final List<Class> superclasses = getDeclaration().getParentClass().getSuperclasses();

                    for (final Class clazz : superclasses) {
                        final List<Expression> params = superclassArgs.get(clazz);
                        if (params != null && params.size() > 0) {
                            final List<String> paramCode = new ArrayList<String>();
                            for (final Expression param : params) {
                                paramCode.add(param.getCode(declaration.getParentNamespace()));
                            }

                            inits.add(clazz.getQualifiedName(declaration.getParentNamespace()) +
                                      "(" +
                                      TextUtils.formatList(paramCode, "", " ", "", ", ", "") +
                                      ")");
                        }
                    }

                    final List<Variable> memberVariables = getDeclaration().getParentClass().getMemberVariables();
                    for (final Variable var : memberVariables) {
                        final Expression initialExp = memberValues.get(var);
                        final String
                                initialVal =
                                initialExp == null ? "" : initialExp.getCode(declaration.getParentNamespace());
                        inits.add(var.getQualifiedName(declaration.getParentNamespace()) + "(" + initialVal + ")");
                    }

                    TextUtils.formatList(writer,
                                         inits,
                                         indent + TextUtils.getIndent() + ": ",
                                         ",\n" + indent + TextUtils.getIndent() + "  ",
                                         "\n");
                }

                code.write(writer, indent, declaration.getParentNamespace());

            }
        }

        @Override
        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = super.getForwardDeclarations();
            result.addAll(code.getForwardDeclarations());
            for (final Expression initialExp : memberValues.values()) {
                result.addAll(initialExp.getForwardDeclarations());
            }
            for (final List<Expression> params : superclassArgs.values()) {
                for (final Expression param : params) {
                    result.addAll(param.getForwardDeclarations());
                }
            }
            return result;
        }

        @Override
        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = super.getIncludes();

            // Make sure we have full declarations for parameter types and template
            // specialisations. Using the NoRef version may include more than strictly
            // necessary,
            // but the chances are that if even if a parameter is passed in by
            // reference, the full definition
            // will be needed. Usually this would be picked up by the code
            // requirements, but if a templated inlined function call is called (or an
            // external function where we don't have the full definition available),
            // we cannot tell what types it will actually need.
            if (returnType != null) {
                result.addAll(returnType.getNoRefDirectUsageIncludes());
            }

            for (final Variable arg : parameters) {
                result.addAll(arg.getType().getNoRefDirectUsageIncludes());
            }

            for (final TemplateSpecialisation arg : templateSpecialisations) {
                result.addAll(arg.getNoRefDirectUsageIncludes());
            }

            result.addAll(code.getIncludes());
            for (final Expression initialExp : memberValues.values()) {
                result.addAll(initialExp.getIncludes());
            }

            for (final List<Expression> params : superclassArgs.values()) {
                for (final Expression param : params) {
                    result.addAll(param.getIncludes());
                }
            }

            return result;
        }

    }

    /**
     * Creates a function in the global namespace
     * <p>
     * <p>
     * The name of the function to create
     */
    public Function(final String name) {
        this(name, null);
    }

    /**
     * Creates a function in the supplied namespace
     * <p>
     * <p>
     * The name of the function to create
     * <p>
     * The namespace that the function is declared in
     */
    public Function(final String name, final Namespace parentNamespace) {
        this.name = name;
        setParentNamespace(parentNamespace);
        code.setParentFunction(this);
    }

    /**
     * Creates a function in the supplied namespace, and marks is as declared in
     * the supplied CodeFile. This would normally be used for functions which are
     * declared in external header files, and hence need no further definition.
     * <p>
     * <p>
     * the name of the function
     * <p>
     * the namespace the function is declared in
     * <p>
     * the code file containing the declaration
     */
    public Function(final String name, final Namespace parentNamespace, final CodeFile declaredIn) {
        this.name = name;
        setParentNamespace(parentNamespace);
        code.setParentFunction(this);
        declaration.addDeclaredIn(declaredIn);
    }

    /**
     * Creates a function in the supplied namespace, and marks is as declared in
     * the supplied CodeFiles. This would normally be used for functions which are
     * declared in external header files, and hence need no further definition. It
     * may be necessray to use this version rather than the single code file
     * version when a function is overloaded in more than one header file for
     * different parameter types, but you don't want to be concerned with working
     * out which particular one is required. This happens frequently with system
     * header files, particularly math functions overloaded for different numeric
     * types
     * <p>
     * <p>
     * the name of the function
     * <p>
     * the namespace the function is declared in
     * <p>
     * the code files containing the declaration
     */
    private Function(final String name, final Namespace parentNamespace, final Set<CodeFile> declaredIn) {
        this.name = name;
        setParentNamespace(parentNamespace);
        declaration.addDeclaredIn(declaredIn);
    }

    /**
     * Adds a template parameter to the function. Parameters will appear in the
     * declaration of the function in the order they are added. This parameter can
     * be used in the rest of the function declaration and definition the same as
     * any other type.
     * <p>
     * eg. Given a function <code>void f()</code>, adding template parameter
     * <code>class T</code> would create the declaration
     * <code>template<class T> void f()</code>
     * <p>
     * <p>
     * The template parameter to add.
     */
    public void addTemplateParameter(final TemplateParameter arg) {
        templateParameters.add(arg);
    }

    /**
     * Adds a template specialisation to the function.
     * <p>
     * <p>
     * The template parameter to add.
     */
    public void addTemplateSpecialisation(final Expression arg) {
        templateSpecialisations.add(TemplateSpecialisation.create(arg));
    }

    /**
     * Adds a template specialisations to the function.
     * <p>
     * <p>
     * a list of specialisations to add.
     */
    public void addTemplateSpecialisation(final List<TemplateSpecialisation> args) {
        templateSpecialisations.addAll(args);
    }

    /**
     * Adds a template specialisations to the function.
     * <p>
     * <p>
     * a list of specialisations to add.
     */
    public void addTemplateSpecialisation(final TemplateSpecialisation... args) {
        templateSpecialisations.addAll(Arrays.asList(args));
    }

    /**
     * Adds a template specialisation to the function.
     * <p>
     * <p>
     * The template parameter to add.
     */
    public void addTemplateSpecialisation(final TypeUsage arg) {
        templateSpecialisations.add(TemplateSpecialisation.create(arg));
    }

    /**
     * Creates a member function call for this function as a member function on
     * the supplied object, passing the supplied arguments.
     * <p>
     * <p>
     * The object containing the member function
     * <p>
     * pass <code>true</code> if the
     * <code>{@literal obj->function}</code> syntax is to be used, or
     * <code>false</code> if the <code>{@literal obj.function}</code>
     * syntax is to be used.
     * <p>
     * the arguments to pass to the function
     *
     * @return a function call expression
     */
    public Expression asFunctionCall(final Expression obj, final boolean pointer, final Expression... args) {
        return asFunctionCall(obj, pointer, Arrays.asList(args));
    }

    /**
     * Creates a member function call for this function as a member function on
     * the supplied object, passing the supplied arguments.
     * <p>
     * <p>
     * The object containing the member function
     * <p>
     * pass <code>true</code> if the
     * <code>{@literal obj->function}</code> syntax is to be used, or
     * <code>false</code> if the <code>{@literal obj.function}</code>
     * syntax is to be used.
     * <p>
     * the arguments to pass to the function
     *
     * @return a function call expression
     */
    public Expression asFunctionCall(final Expression obj, final boolean pointer, final List<Expression> args) {
        if (templateSpecialisations.size() > 0 && obj.isTemplateType()) {
            return new BinaryExpression(obj,
                                        pointer ? BinaryOperator.PTR_REF_TEMPLATE : BinaryOperator.OBJ_REF_TEMPLATE,
                                        asFunctionCall(args));
        } else {
            return new BinaryExpression(obj,
                                        pointer ? BinaryOperator.PTR_REF : BinaryOperator.OBJ_REF,
                                        asFunctionCall(args));
        }

    }

    /**
     * Creates a function call for this function passing the supplied arguments
     * <p>
     * <p>
     * the arguments to pass to the function call
     *
     * @return a function call expression
     */
    public FunctionCall asFunctionCall(final Expression... args) {
        return asFunctionCall(Arrays.asList(args));
    }

    /**
     * Creates a function call for this function passing the supplied arguments
     * <p>
     * <p>
     * the arguments to pass to the function call
     *
     * @return a function call expression
     */
    public FunctionCall asFunctionCall(final List<Expression> args) {
        return new FunctionCall(this, args);
    }

    /**
     * Creates a function pointer expression from this function
     *
     * @return a function pointer expression referencing this function
     */
    public FunctionPointer asFunctionPointer() {
        return new FunctionPointer(this);
    }

    /**
     * Creates a function pointer type from this function definition, so for a
     * function declaration like void foo(int,int,std::string) will return its
     * type; void (*)(int,int,std::string)
     *
     * @return the function pointer type
     */
    public Type asFunctionPointerType() {
        final List<TypeUsage> paramTypeList = new ArrayList<TypeUsage>();
        for (final Variable currentParamVar : getParameters()) {
            paramTypeList.add(currentParamVar.getType());
        }
        final TypeUsage returnType = getReturnType();

        final FunctionPtrType funcPtr = new FunctionPtrType();
        funcPtr.addReturnType(returnType);
        funcPtr.addParameterType(paramTypeList);
        return funcPtr;
    }

    /**
     * Adds a parameter to the function definition
     * <p>
     * <p>
     * the type of the parameter
     * <p>
     * the name of the parameter
     *
     * @return the parameter that was added
     */
    public Variable createParameter(final TypeUsage type, final String name) {
        final Variable result = new Variable(type, name);
        parameters.add(result);
        return result;
    }

    /**
     * Adds a parameter to the function definition with a default value
     * <p>
     * <p>
     * the type of the parameter
     * <p>
     * the name of the parameter
     * <p>
     * the default value for the parameter
     *
     * @return the parameter that was added
     */
    public Variable createParameter(final TypeUsage type, final String name, final Expression defaultValue) {
        final Variable result = new Variable(type, name, defaultValue);
        parameters.add(result);
        return result;
    }

    /**
     * Gets the {@link CodeBlock}for the definition of this function, ready to add
     * actual code into.
     *
     * @return the code block containing this function's code
     */
    public CodeBlock getCode() {
        return code;
    }

    /**
     * Gets the code file that contains the definition for this function. If the
     * function is an inline member function this will be the file containing the
     * parent class's declarationn.
     *
     * @return the code file containing this functions declaration
     */
    public CodeFile getCodeFile() {
        if (isMember && isDeclaredInClass) {
            return declaration.getParentClass().getDeclaredIn();
        } else {
            return definition.getDefinedIn();
        }
    }

    /**
     * Gets the name of this function
     *
     * @return the name of this function
     */
    public String getName() {
        return name;
    }

    public List<Variable> getParameters() {
        return new ArrayList<Variable>(this.parameters);
    }

    public TypeUsage getReturnType() {
        return this.returnType;
    }

    /**
     * Inherits this function into one of its parent class's children. This will
     * not actually add a declaration of the function in the child class, but will
     * allow calls of the function as if it was declared in the child class.
     * Typically this is not necesary for most member functions, as no reference
     * is made to the class containing the declaration, however when calling a
     * static member function inside a subclass, it is much neater to use this
     * function to pull the declaration down, rather than using the fully
     * qualified name of the base class.
     * <p>
     * For example, given a function <code>static void f()</code> declared in a
     * class <code>Base</code>, and referenced in a class <code>Derived</code>
     * derived from <code>Base</code>, use of this function would allow the
     * generated code to read <code>f();</code> rather than <code>Base::f()</code>.
     * <p>
     * Note that this is purely cosmetic, and the resultant code would have no
     * semantic difference.
     * <p>
     * <p>
     * <p>
     * the derived class to inherit into
     *
     * @return a new version of the function attached to the derived class.
     */
    public Function inheritInto(final Class subClass) {
        return new Function(name, subClass.getNamespace(), subClass.getDeclaration().getUsageIncludes());
    }

    public Function copyForSecondaryDefinition() {
        final Function copy = new Function(getName(), declaration.getParentNamespace(), declaration.getUsageIncludes());
        copy.setReturnType(returnType);
        copy.parameters = Collections.unmodifiableList(parameters);
        copy.isConst = isConst;
        copy.isConstructor = isConstructor;

        copy.isDestructor = isDestructor;
        copy.isExplicit = isExplicit;

        copy.isMember = isMember;

        copy.isPure = isPure;
        copy.isStatic = isStatic;
        copy.isVirtual = isVirtual;
        copy.isCast = isCast;
        copy.isExternC = isExternC;
        copy.templateParameters = Collections.unmodifiableList(templateParameters);
        copy.templateSpecialisations = Collections.unmodifiableList(templateSpecialisations);

        return copy;
    }

    /**
     * Sets the comment on the function
     */
    public void setComment(final String comment) {
        this.comment = Comment.createComment(comment);
    }

    /**
     * Changes whether this function is a constant function. By default a function
     * is not const.
     *
     *
     * <code>true</code> if this function is to be declared
     * <code>const</code>
     */
    public void setConst(final boolean isConst) {
        this.isConst = isConst;
    }

    /**
     * Changes whether this function is a destructor. By default a function is not
     * a destructor.
     *
     *
     * <code>true</code> if this function is a destructor
     */
    public void setDestructor(final boolean isDestructor) {
        this.isDestructor = isDestructor;
    }

    /**
     * Changes whether this function is a cast operator. By default a function is
     * not a cast operator.
     *
     *
     * <code>true</code> if this function is a cast operator
     */
    public void setCast(final boolean isCast) {
        this.isCast = isCast;
    }

    /**
     * Changes whether this function is an explicit constructor. By default a
     * constructor is not explicit.
     *
     *
     * <code>true</code> if this function is an explicit constructor
     */
    public void setExplicit(final boolean isExplicit) {
        this.isExplicit = isExplicit;
    }

    /**
     * Sets the initial value for a class member in this constructors
     * initialisation section. The generated code for the constructor will
     * initialise every member in the order that the members appear in the
     * declaration (as recommended by Meyers, Effective C++). This function allows
     * a member variable to be initialised with the supplied value, rather than by
     * using its default contructor.
     * <p>
     * <p>
     * the class member variable to initialise
     * <p>
     * the initial value for the member
     */
    public void setInitialValue(final Variable member, final Expression value) {
        memberValues.put(member, value);
    }

    /**
     * Under some special circumstances the inline modifier needs to be added to a
     * functions definition. An example of this is when a template member function
     * is fully specialised within a source file but the specialisation should
     * only have internal linkage (prevents multiple symbol definition errors when
     * linking against archive files).
     * <p>
     * If you are thinking of using this method, check that
     * <code>declareInClass</code> is not a better fit for your requriements.
     *
     *
     * <code>true</code> if this function is to be declared
     * <code>inline</code>
     */
    public void setInlineModifier(final boolean isInLine) {
        this.isInline = isInLine;
    }

    /**
     * Changes whether the member function definition is placed along side the
     * member function declaration (For class member functions this makes the
     * function inline).
     *
     *
     * <code>true</code> if this function is to be declared
     * <code>inline</code>
     */
    public void declareInClass(final boolean isInClass) {
        this.isDeclaredInClass = isInClass;
    }

    /**
     * Changes whether this function is an declared as a pure virtual function.
     * Marking a function as pure will also mark it as virtual. Setting a
     * previously pure function as not pure will leave it as virtual. By default a
     * function is not pure or virtual.
     *
     *
     * <code>true</code> if this function is to be declared as a pure
     * virtual function
     */
    public void setPure(final boolean isPure) {
        this.isPure = isPure;
        if (isPure) {
            isVirtual = true;
        }
    }

    /**
     * Sets the return type for this function. The default return type upon
     * creation is <void>void</code>
     * <p>
     * <p>
     * the required return type
     */
    public void setReturnType(final TypeUsage returnType) {
        this.returnType = returnType;
    }

    /**
     * Changes whether this function is an declared static. By default a function
     * is not static.
     *
     *
     * <code>true</code> if this function is to be declared
     * <code>static</code>
     */
    public void setStatic(final boolean isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * Changes whether this function is declared as an extern "C" function so that
     * it uses C linkage rather then C++ linkage. If set the symbol name
     * associated with the function name will not be a C++ mangled name.By default
     * a function is not declared as extern "C".
     *
     *
     * <code>true</code> if this function is to be declared as extern "C"
     * <code>extern "C"</code>
     */
    public void setExternC(final boolean isExternC) {
        this.isExternC = isExternC;
    }

    /**
     * Sets the arguments to pass to the superclass constructor in the
     * initialisation section of a constructor.
     * <p>
     * <p>
     * the superclass to initialise
     * <p>
     * the arguments to pass to the superclass constructir
     */
    public void setSuperclassArgs(final Class superclass, final List<Expression> args) {
        superclassArgs.put(superclass, args);
    }

    /**
     * Changes whether this function is an declared as a virtual function. Marking
     * a function as not virtual will also mark it as not pure. By default a
     * function is not vitual.
     *
     *
     * <code>true</code> if this function is to be declared as a virtual
     * function
     */
    public void setVirtual(final boolean isVirtual) {
        this.isVirtual = isVirtual;
        if (!isVirtual) {
            isPure = false;
        }
    }

    @Override
    public String toString() {
        final Writer writer = new StringWriter();
        try {
            getDefinition().writeDefinition(writer, "", null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Gets the set of include files that would be needed to call this function.
     * This will typically be the include files needed by the return type and any
     * parameters, as well as the file that the function itself is declared in.
     *
     * @return a set of files needed to be included to call this function
     */
    Set<CodeFile> getCallIncludes() {
        final Set<CodeFile> result = declaration.getUsageIncludes();
        if (returnType != null) {
            result.addAll(returnType.getNoRefDirectUsageIncludes());
        }

        for (final Variable arg : parameters) {
            result.addAll(arg.getType().getNoRefDirectUsageIncludes());
        }

        for (final TemplateSpecialisation arg : templateSpecialisations) {
            result.addAll(arg.getNoRefDirectUsageIncludes());
        }

        return result;
    }

    String getCallName(final Namespace currentNamespace) {
        if (isMember ||
            getDeclaration().getParentNamespace() == null ||
            getDeclaration().getParentNamespace().contains(currentNamespace)) {
            return getFullName(currentNamespace);
        } else {
            if (isConstructor) {
                return getDeclaration().getParentNamespace().getQualifiedName(currentNamespace);
            } else {
                return getQualifiedName(currentNamespace);
            }
        }

    }

    Declaration getDeclaration() {
        return declaration;
    }

    Definition getDefinition() {
        return definition;
    }

    String getFullName(final Namespace currentNamespace) {
        final List<String> args = new ArrayList<String>();
        for (final TemplateSpecialisation arg : templateSpecialisations) {
            args.add(arg.getValue(currentNamespace));
        }
        // Avoid using '<:', as this parses as a digraph
        final String startTemplate = args.size() > 0 && (args.get(0).startsWith(":")) ? "< " : "<";

        // Avoid using '>>' as this parses as the right shift operator
        final String endTemplate = args.size() > 0 && (args.get(args.size() - 1).endsWith(">")) ? " >" : ">";

        return name + TextUtils.formatList(args, startTemplate, ",", endTemplate);

    }

    String getLabelName(final Label label) {
        String name = labelNameLookup.get(label);
        if (name == null) {
            name = "Label_" + ++labelNo;
            labelNameLookup.put(label, name);
        }
        return name;
    }

    String getQualifiedName() {
        return getQualifiedName(null);
    }

    String getQualifiedName(final Namespace currentNamespace) {
        String qualName = null;
        if (getDeclaration().getParentNamespace() == null ||
            getDeclaration().getParentNamespace().contains(currentNamespace)) {
            qualName = getFullName(currentNamespace);
        } else {
            qualName =
                    getDeclaration().getParentNamespace().getQualifiedName(currentNamespace) +
                    "::" +
                    getFullName(currentNamespace);
        }

        return qualName;

    }

    /**
     * Returns whether or not this function is declared <code>const</code>.
     *
     * @return <code>true</code> if the function is declared const,
     * <code>false</code> otherwise
     */
    boolean isConst() {
        return this.isConst;
    }

    /**
     * Returns whether or not this function is a constructor.
     *
     * @return <code>true</code> if the function is a constructor,
     * <code>false</code> otherwise
     */
    boolean isConstructor() {
        return isConstructor;
    }

    /**
     * Returns whether or not this function is an explicit constructor.
     *
     * @return <code>true</code> if the function is declared explicit,
     * <code>false</code> otherwise
     */
    boolean isExplicit() {
        return isExplicit;
    }

    /**
     * Returns whether or not this function delcared inline.
     *
     * @return <code>true</code> if the function is declared inline,
     * <code>false</code> otherwise
     */
    boolean isInline() {
        return this.isDeclaredInClass;
    }

    /**
     * Returns whether or not this function is a member function.
     *
     * @return <code>true</code> if the function is a member function,
     * <code>false</code> otherwise
     */
    boolean isMember() {
        return this.isMember;
    }

    boolean isPure() {
        return isPure;
    }

    boolean isStatic() {
        return isStatic;
    }

    boolean isVirtual() {
        return isVirtual;
    }

    /**
     * Changes whether this function is a constructor
     *
     *
     * <code>true</code> if this function is a constructor
     */
    void setConstructor(final boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    void setMember() {
        isMember = true;
    }

    /**
     * If a function is a specialisation of another, then the original needs to be
     * visible. This adds the relevant header into the include lit for the
     * declaration
     */
    public void setSpecialisationFrom(final CodeFile file) {
        specialisationFrom = file;
    }

    public void setImplicitSpecialization(final boolean value) {
        isImplicitSpecialization = value;
    }

    /**
     * Sets the parameters for this function to be the list supplied. Used when
     * overriding a virtual function in a derived class to make sure that the
     * parameters match.
     * <p>
     * <p>
     * The new parameter list for this function.
     */
    void setParameters(final List<Variable> parameters) {
        this.parameters = parameters;
    }

    void setParentNamespace(final Namespace parentNamespace) {
        getDeclaration().setParentNamespace(parentNamespace);
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
     * the namespace that is currently open to write into
     *
     * @throws IOException
     */
    void writeComment(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        if (comment != null) {
            comment.write(writer, indent, currentNamespace);
            writer.write("\n");
        }

    }

    private final CodeBlock code = new CodeBlock();

    /**
     * A comment on the function.
     */
    private Comment comment = null;

    private final Declaration declaration = new FunctionDeclaration();
    private final Definition definition = new FunctionDefinition(declaration);
    private boolean isConst = false;
    private boolean isConstructor = false;

    private boolean isDestructor = false;
    private boolean isExplicit = false;
    private boolean isInline = false;
    private boolean isDeclaredInClass = false;

    private boolean isMember = false;
    private boolean isImplicitSpecialization = false;

    private boolean isPure = false;
    private boolean isStatic = false;
    private boolean isVirtual = false;
    private boolean isCast = false;
    private boolean isExternC = false;

    private final Map<Label, String> labelNameLookup = new HashMap<Label, String>();

    private int labelNo = 0;

    private final Map<Variable, Expression> memberValues = new HashMap<Variable, Expression>();
    private final String name;
    private List<Variable> parameters = new ArrayList<Variable>();
    private TypeUsage returnType = TypeUsage.VOID;
    private final Map<Class, List<Expression>> superclassArgs = new HashMap<Class, List<Expression>>();

    private CodeFile specialisationFrom = null;

    /**
     * An ordered list of the template parameters of this function.
     */
    private List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();
    /**
     * An ordered list of the template parameter specialisations of this function.
     */
    private List<TemplateSpecialisation> templateSpecialisations = new ArrayList<TemplateSpecialisation>();
}
