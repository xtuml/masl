/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CharacterType;

public class CharacterLiteral extends LiteralExpression
        implements org.xtuml.masl.metamodel.expression.CharacterLiteral {

    public static CharacterLiteral create(final Position position, final String literal) {
        if (literal == null) {
            return null;
        }

        try {
            return new CharacterLiteral(position, literal);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    private CharacterLiteral(final Position position, final String literal) throws SemanticError {
        super(position);
        original = literal;
        final String noQuotes = literal.substring(1, literal.length() - 1);

        if (noQuotes.length() == 0) {
            throw new SemanticError(SemanticErrorCode.CharacterLiteralInvalidLength, position);
        }
        if (noQuotes.charAt(0) == '\\') {
            if (noQuotes.length() > 1) {
                switch (noQuotes.charAt(1)) {
                    case 'n':
                        value = '\n';
                        break;
                    case 'r':
                        value = '\r';
                        break;
                    case 't':
                        value = '\t';
                        break;
                    case 'b':
                        value = '\b';
                        break;
                    case 'f':
                        value = '\f';
                        break;
                    case '\'':
                        value = '\'';
                        break;
                    case '"':
                        value = '"';
                        break;
                    case '\\':
                        value = '\\';
                        break;
                    case 'u': {
                        // Unicode escape
                        if (noQuotes.length() != 6) {
                            throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
                        }

                        try {
                            value = (char) Integer.parseInt(noQuotes.substring(2), 16);
                        } catch (final NumberFormatException e) {
                            throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
                        }
                    }
                    break;
                    case '0':
                    case '1':
                    case '2':
                    case '3': {
                        // Octal escape
                        if (noQuotes.length() != 4) {
                            throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
                        }

                        try {
                            value = (char) Integer.parseInt(noQuotes.substring(1), 8);
                        } catch (final NumberFormatException e) {
                            throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
                        }
                    }
                    break;
                    default:
                        throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);

                }

            } else {
                throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
            }
        } else if (noQuotes.length() != 1) {
            throw new SemanticError(SemanticErrorCode.CharacterLiteralInvalidLength, position);
        } else {
            value = literal.charAt(1);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharacterLiteral obj2)) {
            return false;
        } else {

            return value == obj2.value;
        }
    }

    @Override
    public BasicType getType() {
        return CharacterType.createAnonymous();
    }

    @Override
    public char getValue() {
        return value;
    }

    @Override
    public int hashCode() {

        return value;
    }

    @Override
    public String toString() {
        return original;
    }

    private final char value;
    private final String original;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCharacterLiteral(this);
    }

}
