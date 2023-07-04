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
