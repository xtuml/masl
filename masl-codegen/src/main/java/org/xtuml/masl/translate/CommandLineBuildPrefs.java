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

import org.xtuml.masl.CommandLine;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CommandLineBuildPrefs implements TranslatorPreferences {

    @Override
    public List<String> getRunTranslators() {
        return CommandLine.INSTANCE.getBuildTranslator() == null ?
               Collections.emptyList() :
               Collections.singletonList(CommandLine.INSTANCE.getBuildTranslator());
    }

    @Override
    public List<String> getSkipTranslators() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        // At the moment the properties of a translator cannot be
        // set using command line build preferences so just return
        // an empty set.
        return Collections.emptyMap();
    }

    @Override
    public boolean isOverride() {
        return false;
    }

    @Override
    public String getName() {
        return "command line";
    }
}
