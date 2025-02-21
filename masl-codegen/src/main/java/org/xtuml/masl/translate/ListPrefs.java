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
