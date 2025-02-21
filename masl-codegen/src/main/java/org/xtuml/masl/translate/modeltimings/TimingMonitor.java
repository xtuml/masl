/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.modeltimings;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.main.Architecture;

public class TimingMonitor {

    public final static Namespace NAMESPACE = new Namespace("SWA");

    public final static CodeFile monitorInc = Architecture.library.createInterfaceHeader("swa/TimingMonitor.hh");
    public final static Class monitorClass = new Class("TimingMonitor", NAMESPACE, monitorInc);

    public final static Statement getScopedTimingBlock(final String methodName) {
        return new Variable(new TypeUsage(monitorClass),
                            "timingBlockMarker",
                            new Expression[]{Literal.createStringLiteral(methodName)}).asStatement();
    }

    public final static Statement getBeginTimingBlock(final String identifier) {
        return new ExpressionStatement(monitorClass.callStaticFunction("beginTimingBlock", new Literal(identifier)));
    }

    public final static Statement getEndTimingBlock(final String identifier) {
        return new ExpressionStatement(monitorClass.callStaticFunction("endTimingBlock", new Literal(identifier)));
    }
}
