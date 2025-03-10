/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import java.util.prefs.Preferences;

public class ParseOptions {

    public static boolean defaultToNonAssocNavigate() {
        return getBooleanOption("DefaultToNonAssocNavigate", false);
    }

    private static boolean getBooleanOption(final String name, final boolean defaultValue) {
        return Preferences.userRoot().node("/masl/options").getBoolean(name,
                                                                       Preferences.systemRoot().node("/masl/options").getBoolean(
                                                                               name,
                                                                               defaultValue));
    }
}
