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

public class CommandLinePrefs implements TranslatorPreferences {

    @Override
    public List<String> getRunTranslators() {
        return CommandLine.INSTANCE.getAddTranslators();
    }

    @Override
    public List<String> getSkipTranslators() {
        return CommandLine.INSTANCE.getSkipTranslators();
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        // At the moment the properties of a translator cannot be
        // set using command line preferences so just return
        // an empty set.
        return Collections.emptyMap();
    }

    @Override
    public boolean isOverride() {
        return CommandLine.INSTANCE.isOverrideTranslators();
    }

    @Override
    public String getName() {
        return "command line";
    }

}
