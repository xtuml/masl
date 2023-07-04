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
package org.xtuml.masl.utils;

import java.io.*;
import java.util.*;

public final class TextUtils {

    public static class CombineFormatter<T> implements Formatter<T> {

        @SafeVarargs
        public CombineFormatter(final Formatter<? super T>... formatters) {
            this.formatters = formatters;
        }

        @Override
        public String format(final T value) {
            final StringBuilder result = new StringBuilder();
            for (final Formatter<? super T> formatter : formatters) {
                result.append(formatter.format(value));
            }
            return result.toString();
        }

        private final Formatter<? super T>[] formatters;
    }

    public interface Formatter<T> {

        String format(T value);
    }

    public static class ToStringFormatter<T> implements Formatter<T> {

        @Override
        public String format(final T value) {
            return value.toString();
        }
    }

    public static class WrapFormatter<T> implements Formatter<T> {

        public WrapFormatter(final Formatter<? super T> formatter, final String suffix) {
            this("", formatter, suffix);
        }

        public WrapFormatter(final String prefix, final Formatter<? super T> formatter) {
            this(prefix, formatter, "");
        }

        public WrapFormatter(final String prefix, final Formatter<? super T> formatter, final String suffix) {
            this.prefix = prefix;
            this.formatter = formatter;
            this.suffix = suffix;
        }

        @Override
        public String format(final T value) {
            return prefix + formatter.format(value) + suffix;
        }

        private final String prefix;
        private final Formatter<? super T> formatter;
        private final String suffix;
    }

    public static String RULED_LINE = "---";

    private static final String manySpaces = filledString(' ', 10000);

    private static int maxLineLength = 74;

    private static int minWrappingWidth = 40;

    private static String indent = "  ";

    private static final Map<Character, String> ruledLines = new HashMap<Character, String>();

    public static String alignTabs(final String text) {
        return alignText(text, '\t');
    }

    public static String alignText(final String text, final char align) {
        final Writer writer = new StringWriter();
        try {
            alignText(writer, text, align);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static void alignTabs(final Writer writer, final String text) throws IOException {
        alignText(writer, text, '\t');
    }

    public static void alignText(final Writer writer, final String text, final char align) throws IOException {
        final StringBuilder buf = new StringBuilder();

        // Look for tab-stops
        final List<Integer> tabOffsets = new ArrayList<Integer>();

        final String text2 = text + '\n';

        int lastNewLine = -1;
        int nextNewLine = text2.indexOf('\n', lastNewLine + 1);

        while (nextNewLine != -1) {
            int tabNo = 0;
            int lastTab = lastNewLine;
            int nextTab = text2.indexOf(align, lastTab + 1);

            while (nextTab != -1 && nextTab < nextNewLine) {
                int oldOffset = 0;
                if (tabNo < tabOffsets.size()) {
                    oldOffset = tabOffsets.get(tabNo).intValue();
                }
                final int newOffset = nextTab - lastTab;

                if (newOffset > oldOffset) {
                    if (tabNo < tabOffsets.size()) {
                        tabOffsets.set(tabNo, Integer.valueOf(newOffset));
                    } else {
                        tabOffsets.add(Integer.valueOf(newOffset));
                    }
                }

                lastTab = nextTab;
                nextTab = text2.indexOf(align, lastTab + 1);
                tabNo++;
            }
            lastNewLine = nextNewLine;
            nextNewLine = text2.indexOf('\n', lastNewLine + 1);
        }

        // Reformat the text
        int lineStart = 0;
        int lineEnd = text2.indexOf('\n', lineStart);

        while (lineEnd != -1) {
            int tabNo = 0;
            int sectionStart = lineStart;
            int sectionEnd = text2.indexOf(align, sectionStart);

            while (sectionEnd != -1 && sectionEnd < lineEnd) {
                final String section = text.substring(sectionStart, sectionEnd);
                final int pad = tabOffsets.get(tabNo).intValue() - section.length();
                final String space = manySpaces.substring(0, pad);
                buf.append(section + space);

                sectionStart = sectionEnd + 1;
                sectionEnd = text2.indexOf(align, sectionStart);
                tabNo++;
            }
            buf.append(text.substring(sectionStart, lineEnd) + '\n');

            lineStart = lineEnd + 1;
            lineEnd = text2.indexOf('\n', lineStart);
        }
        buf.setLength(buf.length() - 1);

        writer.write(buf.toString());
    }

    public static String filledString(final char c, final int length) {
        final char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = c;
        }
        return new String(chars);
    }

    public static <T> String formatList(final Collection<T> values,
                                        final String prefix,
                                        final String separator,
                                        final String suffix) {
        return formatList(values, prefix, new ToStringFormatter<T>(), separator, suffix);
    }

    public static <T> String formatList(final Collection<T> values,
                                        final String prefix,
                                        final String valPrefix,
                                        final String valSuffix,
                                        final String separator,
                                        final String suffix) {
        return formatList(values,
                          prefix,
                          new WrapFormatter<T>(valPrefix, new ToStringFormatter<T>(), valSuffix),
                          separator,
                          suffix);
    }

    public static <V> String formatList(final Collection<V> values,
                                        final String prefix,
                                        final Formatter<? super V> formatter,
                                        final String separator,
                                        final String suffix) {
        final Writer writer = new StringWriter();
        try {
            formatList(writer, values, prefix, formatter, separator, suffix);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static <T> void formatList(final Writer writer,
                                      final Collection<T> values,
                                      final String prefix,
                                      final String separator,
                                      final String suffix) throws IOException {
        formatList(writer, values, prefix, new ToStringFormatter<T>(), separator, suffix);
    }

    public static <T> void formatList(final Writer writer,
                                      final Collection<T> values,
                                      final String prefix,
                                      final String valPrefix,
                                      final String valSuffix,
                                      final String separator,
                                      final String suffix) throws IOException {
        formatList(writer,
                   values,
                   prefix,
                   new WrapFormatter<T>(valPrefix, new ToStringFormatter<T>(), valSuffix),
                   separator,
                   suffix);
    }

    public static <V> void formatList(final Writer writer,
                                      final Collection<V> values,
                                      final String prefix,
                                      final Formatter<? super V> formatter,
                                      final String separator,
                                      final String suffix) throws IOException {
        if (values == null) {
            return;
        }

        if (values.size() > 0) {
            writer.write(prefix);
        }
        final Iterator<V> it = values.iterator();
        while (it.hasNext()) {
            writer.write(formatter.format(it.next()));
            if (it.hasNext()) {
                writer.write(separator);
            }
        }

        if (values.size() > 0) {
            writer.write(suffix);
        }
    }

    public static String getIndent() {
        return indent;
    }

    public static String getIndent(final int level) {
        if (level <= 0) {
            return "";
        } else {
            return TextUtils.getIndent() + TextUtils.getIndent(level - 1);
        }
    }

    public static int getMaxLineLength() {
        return maxLineLength;
    }

    public static int getMinWrappingWidth() {
        return minWrappingWidth;
    }

    public static String getPadding(final String text) {
        return manySpaces.substring(0, text.length());
    }

    public static String indentText(final String indent, final String text) {
        final Writer writer = new StringWriter();
        try {
            indentText(writer, indent, text);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static void indentText(final Writer writer, final String indent, final String text) throws
                                                                                                     IOException {
        int start = 0;
        for (int end = text.indexOf('\n', start) + 1; end != 0; start = end, end = text.indexOf('\n', start) + 1) {
            writer.write(indent + text.substring(start, end));
        }
        if (start < text.length()) {
            writer.write(indent + text.substring(start));
        }
    }

    public static String lowerFirst(final String text) {
        return text.length() > 1 ?
               Character.toLowerCase(text.charAt(0)) + text.substring(1) :
               text.length() == 1 ? String.valueOf(Character.toLowerCase(text.charAt(0))) : text;
    }

    public static void setIndent(final String newIndent) {
        indent = newIndent;
    }

    public static void setMaxLineLength(final int length) {
        maxLineLength = length;
    }

    public static void setMinWrappingWidth(final int length) {
        minWrappingWidth = length;
    }

    public static String textBlock(final String indent,
                                   final String blockPrefix,
                                   final String linePrefix,
                                   final String text,
                                   final String blockSuffix,
                                   final boolean wrap) {
        final Writer writer = new StringWriter();
        try {
            textBlock(writer, indent, blockPrefix, linePrefix, text, blockSuffix, wrap);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static void textBlock(final Writer writer,
                                 final String indent,
                                 final String blockPrefix,
                                 final String linePrefix,
                                 final String text,
                                 final String blockSuffix,
                                 final boolean wrap) throws IOException {
        if (linePrefix != null) {
            // Open the comment if necessary
            if (blockPrefix != null) {
                writer.write(indent + blockPrefix + "\n");
            }

            final BufferedReader reader = new BufferedReader(new StringReader(text));

            // Read the comment text a line at a time so that any explicit line breaks
            // are preserved.
            String line = reader.readLine();
            while (line != null) {
                // Check for the magic character sequence of 'X---X' on a whole line,
                // where X is any character. If found this indicates that a line of
                // character 'X' should be ruled across the comment.
                if (line.length() == (RULED_LINE.length() + 2) &&
                    line.substring(1, 4).equals(RULED_LINE) &&
                    line.charAt(0) == line.charAt(4)) {
                    final char lineOf = line.charAt(0);

                    // There are only likely to be a few differrent lines to draw, so
                    // cache any that are used.
                    line = ruledLines.get(lineOf);
                    if (line == null) {
                        // Only a few likely lines to rule, so cache them.
                        line = TextUtils.filledString(lineOf, TextUtils.getMaxLineLength());
                        ruledLines.put(lineOf, line);
                    }

                    // Only use the characters we need to fill the line.
                    line = line.substring(indent.length() + linePrefix.length());

                    // Add the line to the comment
                    writer.write(indent + linePrefix + line);
                } else {
                    if (wrap) {
                        // Wrap the comment text to the line length, and append to the
                        // writer
                        TextUtils.wrapLine(writer, indent + linePrefix + " ", line, indent + linePrefix + " ");
                    } else {
                        // No wrap so just write it out
                        writer.write(indent + linePrefix + " " + line);
                    }
                }
                line = reader.readLine();
                if (line != null) {
                    writer.write("\n");
                }
            }
            // Close the comment off if necessary
            if (blockSuffix != null) {
                writer.write("\n" + indent + blockSuffix);
            }
        } else {
            writer.write(indent + blockPrefix + " " + text + " " + blockSuffix);
        }

    }

    public static String upperFirst(final String text) {
        return text.length() > 1 ?
               Character.toUpperCase(text.charAt(0)) + text.substring(1) :
               text.length() == 1 ? String.valueOf(Character.toUpperCase(text.charAt(0))) : text;
    }

    public static String wrapLine(final String firstLinePrefix, final String text) {
        final Writer writer = new StringWriter();
        try {
            wrapLine(writer, firstLinePrefix, text);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static String wrapLine(final String firstLinePrefix,
                                  final String text,
                                  final String continueLinePrefix) {
        final Writer writer = new StringWriter();
        try {
            wrapLine(writer, firstLinePrefix, text, continueLinePrefix);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public static void wrapLine(final Writer writer, final String firstLinePrefix, final String text) throws
                                                                                                            IOException {
        wrapLine(writer, firstLinePrefix, text, getPadding(firstLinePrefix));
    }

    public static void wrapLine(final Writer writer,
                                final String firstLinePrefix,
                                final String line,
                                final String continueLinePrefix) throws IOException {
        int start = 0;
        int lineWidth = Math.max(maxLineLength - firstLinePrefix.length(), minWrappingWidth);
        String prefix = firstLinePrefix;

        while (line.length() - start > lineWidth) {
            int end = 0;
            if (lineWidth > 0) {
                end = line.lastIndexOf(' ', start + lineWidth);
                if (end <= start) {
                    end = line.indexOf(' ', start + lineWidth);
                }
                if (end == -1) {
                    end = line.length();
                }
            }

            writer.write(prefix + line.substring(start, end) + (end < line.length() ? "\n" : ""));

            // Set up for next time round
            start = end == 0 ? end : end + 1;
            prefix = continueLinePrefix;
            lineWidth = Math.max(maxLineLength - prefix.length(), minWrappingWidth);

        }

        if (start < line.length()) {
            writer.write(prefix + line.substring(start));
        }
    }

    public static String singleLine(final String comment) {
        return comment.replace('\n', ' ');
    }

}
