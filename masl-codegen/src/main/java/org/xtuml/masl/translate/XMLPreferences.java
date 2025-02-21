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

import org.xtuml.masl.TranslatorParser;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class XMLPreferences implements TranslatorPreferences {

    public XMLPreferences(final TranslatorParser translatorXML) {
        this.translatorXML = translatorXML;
    }

    @Override
    public List<String> getRunTranslators() {
        return translatorXML.getTranslatorAddList();
    }

    @Override
    public List<String> getSkipTranslators() {
        return translatorXML.getTranslatorSkipList();
    }

    @Override
    public Map<String, Properties> getTranslatorProperties() {
        return translatorXML.getTranslatorProperties();
    }

    @Override
    public boolean isOverride() {
        return translatorXML.isOverride();
    }

    private final TranslatorParser translatorXML;

    @Override
    public String getName() {
        return "translator.xml";
    }

}
