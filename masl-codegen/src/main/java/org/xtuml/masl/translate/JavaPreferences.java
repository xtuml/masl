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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class JavaPreferences implements TranslatorPreferences {

    public JavaPreferences(final Preferences node) {
        if (node.isUserNode()) {
            name = "user preferences";
        } else {
            name = "system preferences";
        }

        try {
            for (final String keyName : node.keys()) {
                if (keyName.equals("override")) {
                    override = node.getBoolean(keyName, false);
                } else {
                    final String value = node.get(keyName, "");
                    if (value.equals("add")) {
                        runTranslators.add(keyName);
                    } else if (value.equals("skip")) {
                        skipTranslators.add(keyName);
                    } else {
                        System.err.println("Warning: Unrecognised action '" + value + "' in " + node);
                    }
                }
            }
        } catch (final BackingStoreException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<String> getRunTranslators() {
        return runTranslators;
    }

    @Override
    public List<String> getSkipTranslators() {
        return skipTranslators;
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        // At the moment the properties of a translator cannot be
        // set using java preferences so just return
        // an empty set.
        return Collections.emptyMap();
    }

    @Override
    public boolean isOverride() {
        return override;
    }

    @Override
    public String getName() {
        return name;
    }

    private final String name;
    private boolean override = false;

    private final List<String> runTranslators = new ArrayList<>();

    private final List<String> skipTranslators = new ArrayList<>();

}
