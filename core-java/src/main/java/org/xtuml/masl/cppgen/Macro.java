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

import java.util.Arrays;
import java.util.List;

/**
 * Creates code to declare a C++ macro. There is no facility at present to
 * define a macro, as in automatically generated code that would be of limited
 * use. This class is intended for referencing macros defined in external header
 * files.
 */
public class Macro {

    /**
     * Create a reference to a macro defined in the specified header file.
     * <p>
     * <p>
     * The name of the macro
     * <p>
     * The header file in which the macro is defined
     */
    public Macro(final String name, final CodeFile declaredIn) {
        this.name = name;
        this.declaredIn = declaredIn;
    }

    /**
     * Creates a macro call passing the supplied arguments
     * <p>
     * <p>
     * the arguments to pass
     *
     * @return a call to this macro
     */
    public MacroCall asMacroCall(final MacroArgument... args) {
        return asMacroCall(Arrays.asList(args));
    }

    /**
     * Creates a macro call passing the supplied arguments
     * <p>
     * <p>
     * the arguments to pass
     *
     * @return a call to this macro
     */
    public MacroCall asMacroCall(final List<MacroArgument> args) {
        return new MacroCall(this, args);
    }

    /**
     * Gets the code file that this macro is declared in
     *
     * @return the code file
     */
    public CodeFile getDeclaredIn() {
        return declaredIn;
    }

    /**
     * Gets the name of this macro
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    private final CodeFile declaredIn;

    private final String name;
}
