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

    private final List<String> runTranslators = new ArrayList<String>();

    private final List<String> skipTranslators = new ArrayList<String>();

}
