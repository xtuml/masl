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
package org.xtuml.masl.metamodelImpl.relationship;

import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;

public class HalfRelationship {

    public static HalfRelationship create(final ObjectNameExpression fromObject,
                                          final boolean conditional,
                                          final String role,
                                          final MultiplicityType mult,
                                          final ObjectNameExpression toObject) {
        if (fromObject == null || toObject == null) {
            return null;
        }

        return new HalfRelationship(fromObject, conditional, role, mult, toObject);
    }

    private HalfRelationship(final ObjectNameExpression fromObject,
                             final boolean conditional,
                             final String role,
                             final MultiplicityType mult,
                             final ObjectNameExpression toObject) {
        this.fromObject = fromObject;
        this.conditional = conditional;
        this.role = role;
        this.mult = mult;
        this.toObject = toObject;
    }

    private final ObjectNameExpression fromObject;
    private final boolean conditional;
    private final String role;
    private final MultiplicityType mult;

    private final ObjectNameExpression toObject;

    public boolean isConditional() {
        return conditional;
    }

    public ObjectNameExpression getFromObject() {
        return fromObject;
    }

    public MultiplicityType getMult() {
        return mult;
    }

    public String getRole() {
        return role;
    }

    public ObjectNameExpression getToObject() {
        return toObject;
    }

    @Override
    public String toString() {
        return fromObject.getObject().getName() +
               (conditional ? " conditionally " : " unconditionally ") +
               role +
               " " +
               mult +
               " " +
               toObject.getObject().getName();
    }

}
