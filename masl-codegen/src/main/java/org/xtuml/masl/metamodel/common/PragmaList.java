/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.common;

import org.xtuml.masl.metamodel.ASTNode;

import java.util.List;

public interface PragmaList extends ASTNode {

    String INDEX = "index";
    String SCOPE = "scope";
    String NUMBER = "number";

    List<String> getPragmaValues(String name);

    List<String> getPragmaValues(String name, boolean allowValueList);

    List<? extends PragmaDefinition> getPragmas();

    String getValue(String name);

    boolean hasValue(String name);

    boolean hasPragma(String name);

}
