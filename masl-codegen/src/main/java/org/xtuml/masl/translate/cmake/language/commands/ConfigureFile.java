/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language.commands;

import com.google.common.collect.Lists;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

public class ConfigureFile extends Command {
    public static ConfigureFile configure(final SingleArgument input, final SingleArgument output) {
        return new ConfigureFile(input, output);
    }

    public static ConfigureFile configureCopyOnly(final SingleArgument input, final SingleArgument output) {
        return new ConfigureFile(input, output, COPYONLY);
    }

    public static ConfigureFile configureAtOnly(final SingleArgument input, final SingleArgument output) {
        return new ConfigureFile(input, output, ATONLY);
    }

    private ConfigureFile(final SingleArgument input, final SingleArgument output) {
        super("configure_file", Lists.newArrayList(input, output));
    }

    private ConfigureFile(final SingleArgument input, final SingleArgument output, final SingleArgument type) {
        super("configure_file", Lists.newArrayList(input, output, type));
    }

    private static final SingleArgument COPYONLY = new SingleArgument("COPYONLY");
    private static final SingleArgument ATONLY = new SingleArgument("@ONLY");
}
