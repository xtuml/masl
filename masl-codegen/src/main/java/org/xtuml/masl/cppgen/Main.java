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

public class Main extends Function {

    private final Expression argc;
    private final Expression argv;

    public Main() {
        super("main");
        setReturnType(new TypeUsage(FundamentalType.INT));

        argc = createParameter(new TypeUsage(FundamentalType.INT), "argc").asExpression();
        argv =
                createParameter(new TypeUsage(false, FundamentalType.CHAR, new boolean[]{true, true}, false),
                                "argv").asExpression();

    }

    public Expression getArgc() {
        return argc;
    }

    public Expression getArgv() {
        return argv;
    }

}
