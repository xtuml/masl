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

import org.xtuml.masl.javagen.astimpl.TypeParameterImpl;

import java.util.List;

public interface GenericItem {

    TypeParameter addTypeParameter(TypeParameter typeParameter);

    List<? extends TypeParameter> getTypeParameters();

    TypeParameterImpl addTypeParameter(String name);

}
