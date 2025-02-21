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

import java.util.ArrayList;
import java.util.List;

public class TypenameTemplateParameter extends TemplateParameter {

    private final TemplateType type;
    private final List<TemplateParameter> templateParameters = new ArrayList<>();

    public TypenameTemplateParameter() {
        this.type = null;
    }

    public TypenameTemplateParameter(final TemplateType type) {
        this.type = type;
    }

    public void addTemplateParameter(final TemplateParameter param) {
        templateParameters.add(param);
    }

    @Override
    public String getName() {
        final List<String> params = new ArrayList<>();
        for (final TemplateParameter param : templateParameters) {
            params.add(param.getName());
        }
        return TextUtils.formatList(params, "template<", ", ", "> ") + (type != null ? "class " + type.getName() : "");
    }

}
