/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake.language.arguments;

import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;

public class QuotedArgument extends SingleArgument {

    private static final Escaper
            escaper =
            new CharEscaperBuilder().addEscape('\\', "\\\\").addEscape('\"', "\\\"").addEscape('\n', "\\n").addEscape(
                    '\t',
                    "\\t").addEscape(';', "\\;").toEscaper();

    public QuotedArgument(final String value) {
        super(value);
    }

    @Override
    public String getText() {
        return "\"" + escaper.escape(super.getText()) + "\"";
    }
}