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
