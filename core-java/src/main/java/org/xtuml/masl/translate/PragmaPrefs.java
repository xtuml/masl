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

import org.xtuml.masl.metamodel.common.PragmaList;

import java.util.*;

public class PragmaPrefs implements TranslatorPreferences {

    static private final String RUN_TRANSLATOR = "RunTranslator";
    static private final String SKIP_TRANSLATOR = "SkipTranslator";
    static private final String OVERRIDE_TRANSLATOR = "OverrideTranslator";
    static private final String ONLY_TRANSLATOR = "OnlyTranslator";

    public PragmaPrefs(final PragmaList pragmas) {
        this.pragmas = pragmas;
    }

    @Override
    public List<String> getRunTranslators() {
        final List<String> result = new ArrayList<>(getList(ONLY_TRANSLATOR));
        result.addAll(getList(RUN_TRANSLATOR));
        return result;
    }

    @Override
    public List<String> getSkipTranslators() {
        return getList(SKIP_TRANSLATOR);
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        // At the moment the properties of a translator cannot be
        // set using pragma preferences so just return
        // an empty set.
        return Collections.emptyMap();
    }

    @Override
    public boolean isOverride() {
        for (final String value : getList(OVERRIDE_TRANSLATOR)) {
            if (value.equals("true")) {
                return true;
            }
        }
        return getList(ONLY_TRANSLATOR).size() > 0;
    }

    private final List<String> getList(final String key) {
        final List<String> result = pragmas.getPragmaValues(key);
        return result == null ? Collections.emptyList() : result;
    }

    private final PragmaList pragmas;

    @Override
    public String getName() {
        return "pragmas";
    }

}
