/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
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
