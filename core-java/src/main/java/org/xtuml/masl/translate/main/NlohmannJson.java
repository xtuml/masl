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
package org.xtuml.masl.translate.main;// CLASSIFICATION: OFFICIAL

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.building.BuildSet;

public class NlohmannJson {
    private static final BuildSet buildSet = new BuildSet("nlohmann_json");
    public final static Library core = new Library("nlohmann_json").inBuildSet(buildSet);

    public final static Namespace NAMESPACE = new Namespace("nlohmann");
    public final static CodeFile jsonInc = core.createInterfaceHeader("nlohmann/json.hpp");
    public final static Class json = new Class("json", NAMESPACE, jsonInc);
    public final static Class json_pointer = json.referenceNestedType("json_pointer");

    public static Expression get(Expression j, TypeUsage type) {
        Function get = new Function("get");
        get.addTemplateSpecialisation(type);
        return get.asFunctionCall(j, false);
    }

    public static Expression get_to(Expression j, Expression v) {
        Function get = new Function("get_to");
        return get.asFunctionCall(j, false, v);
    }

    public static Expression at(Expression j, Expression index) {
        Function get = new Function("at");
        return get.asFunctionCall(j, false, index);
    }

}
