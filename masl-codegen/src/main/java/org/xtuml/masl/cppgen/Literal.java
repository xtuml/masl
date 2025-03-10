/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

public class Literal extends Expression {

    private final String literal;

    /**
     * The c++ literal <code>true</code>
     */
    public static Literal TRUE = new Literal("true");

    /**
     * The c++ literal <code>false</code>
     */
    public static Literal FALSE = new Literal("false");

    /**
     * The c++ literal <code>0</code> used for null pointer expressions
     */
    public static Literal NULL = new Literal("0");

    /**
     * The C++ literal <code>{@literal "\n"}</code>. Note that the type of this
     * literal is a string.
     */
    public static Literal NEWLINE = new Literal("\"\\n\"");

    /**
     * The C++ literal <code>0</code>
     */
    public static Literal ZERO = new Literal("0");

    /**
     * The C++ literal <code>1</code>
     */
    public static Literal ONE = new Literal("1");

    /**
     * The C++ literal <code>{@literal ""}</code>.
     */
    public static Literal EMPTY_STRING = new Literal("\"\"");

    /**
     * Creates a C++ string literal containing the supplied text. Eg
     * <code>new createStringLiteral("hello")</code> would return a C++ literal
     * containing the value <code>{@literal "hello"}</code>. Any characters
     * requiring escaping will be escaped correctly.
     * <p>
     * <p>
     * the text of the literal
     *
     * @return a literal containing the supplied text surrounded by quotes.
     */
    public static Literal createStringLiteral(final String literal) {
        final StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (final char ch : literal.toCharArray()) {
            builder.append(escapeChar(ch, '"'));
            if (isWide(ch)) {
                prefix = "L";
            }
        }
        return new Literal(prefix + "\"" + builder + "\"");
    }

    /**
     * Creates a C++ char literal containing the supplied text. Eg
     * <code>new createCharLiteral('h')</code> would return a C++ literal containing
     * the value <code>{@literal 'h'}</code>. Any characters requiring escaping will
     * be escaped correctly.
     * <p>
     * <p>
     * the text of the literal
     *
     * @return a literal containing the supplied text surrounded by quotes.
     */
    public static Literal createCharLiteral(final char literal) {
        return new Literal((isWide(literal) ? "L" : "") + "'" + escapeChar(literal, '\'') + "'");
    }

    private static boolean isWide(final char ch) {
        return ch > '\u00ff';
    }

    private static String escapeChar(final char ch, final char quoteToEscape) {
        if (isWide(ch)) {
            final String hexRep = Integer.toHexString(ch);
            return "\\u" + "0000".substring(hexRep.length()) + hexRep;
        } else {
            if (Character.isISOControl(ch)) {
                switch (ch) {
                    case '\n':
                        return "\\n";
                    case '\t':
                        return "\\t";
                    case '\013':
                        return "\\v";
                    case '\b':
                        return "\\b";
                    case '\r':
                        return "\\r";
                    case '\f':
                        return "\\f";
                    case '\007':
                        return "\\a";
                    default: {
                        final String octRep = Integer.toOctalString(ch);
                        return "\\" + "000".substring(octRep.length()) + octRep;
                    }
                }
            } else {
                switch (ch) {
                    case '\\':
                        return "\\\\";
                    case '\'':
                        return quoteToEscape == '\'' ? "\\'" : "'";
                    case '"':
                        return quoteToEscape == '"' ? "\\\"" : "\"";
                    default:
                        return Character.toString(ch);
                }
            }
        }
    }

    /**
     * Creates a C++ literal for the text supplied.
     * <p>
     * <p>
     * the literal
     */
    public Literal(final String literal) {
        this.literal = literal;
    }

    /**
     * Creates a numeric literal from the supplied value
     * <p>
     * <p>
     * the value of the literal
     */
    public Literal(final int i) {
        this.literal = String.valueOf(i);
    }

    @Override
    String getCode(final Namespace currentNamespace, final String alignment) {
        return literal;
    }

    @Override
    int getPrecedence() {
        return 0;
    }

}
