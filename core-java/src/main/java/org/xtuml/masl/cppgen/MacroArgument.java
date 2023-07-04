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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A macro argument is an argument supplied to a C++ macro. As a C++ macro is a
 * simple text substitution at preprocessor time, the code generator can have no
 * knowlege of the resultant processed code, and therefore cannot make many
 * assumptions about which include files and forward declarations are needed.
 */
public abstract class MacroArgument {

    /**
     * Returns the code representing the expression.
     * <p>
     * <p>
     * The namespace that the expression is in. This is used to determine how much
     * scope information is required to uniquely resolve any names used in the
     * expression.
     *
     * @return the code for the expression
     */
    abstract String getCode(Namespace currentNamespace);

    /**
     * Calculates the set of forward declarations needed to allow this parameter to
     * compile.
     *
     * @return forward declarations required
     */
    abstract Set<Declaration> getForwardDeclarations();

    /**
     * Calculates the set of include files needed to allow this argument to compile.
     *
     * @return include files required
     */
    abstract Set<CodeFile> getIncludes();

    /**
     * Creates a macro argument using the supplied type. Note that the generated
     * code will have no knowlege of how the type is used within the macro, but the
     * assumption is made that the type will be instantiated, so include files and
     * forward declarations needed to use the type will be added to the resultant
     * code. Any further forward declarations and include files required will have
     * to be added manually.
     * <p>
     * <p>
     * the type to use
     *
     * @return a macro parameter containing the supplied type
     */
    static public MacroArgument createArgument(final TypeUsage arg) {
        return new MacroArgument() {

            final private TypeUsage type = arg;

            @Override
            String getCode(final Namespace currentNamespace) {
                return type.getQualifiedName(currentNamespace);
            }

            @Override
            Set<Declaration> getForwardDeclarations() {
                return type.getDirectUsageForwardDeclarations();
            }

            @Override
            Set<CodeFile> getIncludes() {
                return type.getDirectUsageIncludes();
            }
        };

    }

    /**
     * Creates a macro argument using the supplied expression. Note that the
     * generated code will have no knowlege of how the string is used within the
     * macro, but the assumption is made that the supplied expression will be needed
     * as-is somewhere, so include files and forward declarations needed by the
     * expression will be added to the resultant code. Any further forward
     * declarations and include files required will have to be added manually.
     * <p>
     * <p>
     * the expression to use
     *
     * @return a macro parameter containing the supplied expression
     */
    static public MacroArgument createArgument(final Expression arg) {
        return new MacroArgument() {

            final private Expression expression = arg;

            @Override
            String getCode(final Namespace currentNamespace) {
                return expression.getCode(currentNamespace);
            }

            @Override
            Set<Declaration> getForwardDeclarations() {
                return expression.getForwardDeclarations();
            }

            @Override
            Set<CodeFile> getIncludes() {
                return expression.getIncludes();
            }
        };

    }

    /**
     * Creates a macro argument using the supplied string. Note that the generated
     * code will have no knowlege of how the string is used within the macro, and so
     * any forward declarations and include files required will have to be added
     * manually.
     * <p>
     * <p>
     * the string to use
     *
     * @return a macro parameter containing the supplied string
     */
    static public MacroArgument createArgument(final String arg) {
        return new MacroArgument() {

            final private String string = arg;

            @Override
            String getCode(final Namespace currentNamespace) {
                return string;
            }

            @Override
            Set<Declaration> getForwardDeclarations() {
                return new LinkedHashSet<>();
            }

            @Override
            Set<CodeFile> getIncludes() {
                return new LinkedHashSet<>();
            }
        };

    }

}
