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

import java.util.Set;

public class FunctionPointer extends Expression {

    private final Function function;

    /**
     * Creates a function pointer expression from a given function. The function
     * can be either a free function or a member function.
     * <p>
     * <p>
     * the function to create a pointer to
     */
    public FunctionPointer(final Function function) {
        this.function = function;
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        if (function.isMember()) {
            return "&" +
                   function.getDeclaration().getParentClass().getQualifiedName(currentNamespace) +
                   "::" +
                   function.getName();
        } else {
            return "&" + function.getQualifiedName(currentNamespace);
        }
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(function.getCallIncludes());
        return result;
    }

    @Override
    int getPrecedence() {
        return 3;
    }

}
