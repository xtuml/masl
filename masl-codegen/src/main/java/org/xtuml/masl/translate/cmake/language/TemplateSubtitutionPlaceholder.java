/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language;

import org.xtuml.masl.translate.cmake.CMakeListsItem;

import java.io.IOException;
import java.io.Writer;

public class TemplateSubtitutionPlaceholder implements CMakeListsItem {

    private final String placeholder;

    public TemplateSubtitutionPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public void writeCode(final Writer writer, final String indent) throws IOException {

        writer.write(indent + "@" + placeholder + "@");

    }

}
