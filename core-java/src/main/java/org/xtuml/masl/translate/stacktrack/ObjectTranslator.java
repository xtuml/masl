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
package org.xtuml.masl.translate.stacktrack;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;
import org.xtuml.masl.translate.main.object.StateActionTranslator;

import java.util.ArrayList;
import java.util.List;

public class ObjectTranslator {

    public ObjectTranslator(final ObjectDeclaration object) {
        for (final ObjectService service : object.getServices()) {
            serviceTranslators.add(new ActionTranslator(ObjectServiceTranslator.getInstance(service)));
        }

        for (final State state : object.getStates()) {
            stateTranslators.add(new ActionTranslator(StateActionTranslator.getInstance(state)));
        }

    }

    void translate() {

        for (final ActionTranslator serviceTranslator : serviceTranslators) {
            serviceTranslator.translate();
        }
        for (final ActionTranslator stateTranslator : stateTranslators) {
            stateTranslator.translate();
        }

    }

    private final List<ActionTranslator> serviceTranslators = new ArrayList<ActionTranslator>();
    private final List<ActionTranslator> stateTranslators = new ArrayList<ActionTranslator>();

}
