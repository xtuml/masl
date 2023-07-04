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
package org.xtuml.masl.translate;

import java.util.*;

public class ListPrefs implements TranslatorPreferences {

    public ListPrefs(final String name, final String... runTranslators) {
        this.runTranslators = runTranslators;
        this.name = name;
    }

    @Override
    public List<String> getRunTranslators() {
        return Arrays.asList(runTranslators);
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        // At the moment the properties of a translator cannot be
        // set using list preferences so just return
        // an empty set.
        return Collections.emptyMap();
    }

    @Override
    public List<String> getSkipTranslators() {
        return Collections.emptyList();
    }

    @Override
    public boolean isOverride() {
        return false;
    }

    private final String[] runTranslators;

    @Override
    public String getName() {
        return name;
    }

    private final String name;
}
