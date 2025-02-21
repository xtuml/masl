/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.List;

public interface Callable {

    void setVisibility(Visibility visibility);

    Visibility getVisibility();

    boolean isVarArgs();

    List<? extends Parameter> getParameters();

    Parameter addParameter(Parameter parameter);

    void setVarArgs();

    Parameter addParameter(Type type, String name);

    CodeBlock getCodeBlock();

    CodeBlock setCodeBlock(CodeBlock block);

    CodeBlock setCodeBlock();

}
