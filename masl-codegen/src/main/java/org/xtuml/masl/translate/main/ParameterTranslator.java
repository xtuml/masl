/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.common.ParameterDefinition;

public class ParameterTranslator {

    /**
     * This constructor is used during the translation of service parameters as part
     * of a domain build.
     * <p>
     * <p>
     * The definition of the terminator service parameter.
     * <p>
     * The cpp function that the service will be translated into.
     */
    public ParameterTranslator(final ParameterDefinition param, final Function function) {
        this.param = param;
        final TypeUsage type = Types.getInstance().getType(param.getType());
        variable =
                function.createParameter(type.getOptimalParameterType(param.getMode() == ParameterDefinition.Mode.OUT),
                                         Mangler.mangleName(param));
    }

    private final ParameterDefinition param;
    private final Variable variable;

    public ParameterDefinition getParam() {
        return param;
    }

    public Variable getVariable() {
        return variable;
    }
}
