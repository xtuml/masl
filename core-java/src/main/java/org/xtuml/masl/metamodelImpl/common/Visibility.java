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
package org.xtuml.masl.metamodelImpl.common;

public enum Visibility {

    PUBLIC("public", org.xtuml.masl.metamodel.common.Visibility.PUBLIC), PRIVATE("private",
                                                                                 org.xtuml.masl.metamodel.common.Visibility.PRIVATE);

    private final String text;
    private final org.xtuml.masl.metamodel.common.Visibility visibility;

    Visibility(final String text, final org.xtuml.masl.metamodel.common.Visibility visibility) {
        this.text = text;
        this.visibility = visibility;
    }

    public org.xtuml.masl.metamodel.common.Visibility getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return text;
    }
}
