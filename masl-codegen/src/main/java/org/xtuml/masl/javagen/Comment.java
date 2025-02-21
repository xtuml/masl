/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen;

import org.xtuml.masl.utils.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment {

    private static class JavadocField {

        public JavadocField(final String field, final String name, final String comment) {
            this.field = field;
            this.name = name;
            this.comment = comment;
        }

        public void write(final Writer writer, final String prefix) throws IOException {
            final String prefix2 = "@" + field + " " + (name != null ? name + " " : "");
            TextUtils.wrapLine(writer, prefix + prefix2, comment, prefix + TextUtils.getPadding(prefix2));
        }

        private final String field;

        private final String name;

        private final String comment;
    }

    private static final Map<Character, String> ruledLines = new HashMap<>();

    public static Comment createCStyleComment(final String comment) {
        return new Comment("/*", " * ", comment, " */");
    }

    public static Comment createJavadocComment(final String comment) {
        return new Comment("/**", " * ", comment, " */");
    }

    public static Comment createJavaStyleComment(final String comment) {
        return new Comment(null, "// ", comment, null);
    }

    private Comment(final String openComment,
                    final String continueComment,
                    final String comment,
                    final String closeComment) {
        this.openComment = openComment;
        this.continueComment = continueComment;
        this.comment = new StringBuilder(comment);
        this.closeComment = closeComment;
    }

    public void addComment(final String comment) {
        this.comment.append(comment);
    }

    public void addJavadocField(final String field, final String comment) {
        javadocFields.add(new JavadocField(field, null, comment));
    }

    public void addJavadocField(final String field, final String name, final String comment) {
        javadocFields.add(new JavadocField(field, name, comment));
    }

    public void write(final Writer writer, final String indent) throws IOException {
        if (openComment != null) {
            writer.write(indent + openComment + "\n");
        }

        final BufferedReader reader = new BufferedReader(new StringReader(comment.toString()));

        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 5 && line.startsWith("---", 1) && line.charAt(0) == line.charAt(4)) {
                    final char lineOf = line.charAt(0);
                    line = ruledLines.get(lineOf);
                    if (line == null) {
                        line = TextUtils.filledString(lineOf, TextUtils.getMaxLineLength());
                        ruledLines.put(lineOf, line);
                    }
                    line = line.substring(indent.length() + continueComment.length());
                }
                TextUtils.wrapLine(writer, indent + continueComment, line, indent + continueComment);
                writer.write("\n");
                line = reader.readLine();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        for (final JavadocField field : javadocFields) {
            field.write(writer, indent + continueComment);
            writer.write("\n");
        }

        if (closeComment != null) {
            writer.write(indent + closeComment + "\n");
        }
    }

    private final StringBuilder comment;

    private final List<JavadocField> javadocFields = new ArrayList<>();

    private final String openComment;

    private final String continueComment;

    private final String closeComment;

}
