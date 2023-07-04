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
package org.xtuml.masl.metamodelImpl.statemodel;

public enum TransitionType {
    TO_STATE("", org.xtuml.masl.metamodel.statemodel.TransitionType.TO_STATE), CANNOT_HAPPEN("Cannot_Happen",
                                                                                             org.xtuml.masl.metamodel.statemodel.TransitionType.CANNOT_HAPPEN), IGNORE(
            "Ignore",
            org.xtuml.masl.metamodel.statemodel.TransitionType.IGNORE);

    private final String text;
    private final org.xtuml.masl.metamodel.statemodel.TransitionType type;

    TransitionType(final String text, final org.xtuml.masl.metamodel.statemodel.TransitionType type) {
        this.text = text;
        this.type = type;
    }

    public org.xtuml.masl.metamodel.statemodel.TransitionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }
}
