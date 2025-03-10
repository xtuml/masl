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

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface TranslatorPreferences {

    List<String> getRunTranslators();

    List<String> getSkipTranslators();

    Map<String, Properties> getTranslatorProperties();

    boolean isOverride();

    String getName();
}
