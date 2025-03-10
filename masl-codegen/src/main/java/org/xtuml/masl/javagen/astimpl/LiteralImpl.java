/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Literal;

abstract class LiteralImpl extends ExpressionImpl implements Literal {

    static class BooleanLiteralImpl extends LiteralImpl implements BooleanLiteral {

        BooleanLiteralImpl(final ASTImpl ast, final boolean value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitBooleanLiteral(this);
        }

        @Override
        public boolean getValue() {
            return value;
        }

        @Override
        public void setValue(final boolean value) {
            this.value = value;
        }

        private boolean value;
    }

    static class CharacterLiteralImpl extends LiteralImpl implements CharacterLiteral {

        CharacterLiteralImpl(final ASTImpl ast, final char value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitCharacterLiteral(this);
        }

        @Override
        public char getValue() {
            return value;
        }

        @Override
        public void setValue(final char value) {
            this.value = value;
        }

        private char value;
    }

    static class DoubleLiteralImpl extends LiteralImpl implements DoubleLiteral {

        DoubleLiteralImpl(final ASTImpl ast, final double value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitDoubleLiteral(this);
        }

        @Override
        public double getValue() {
            return value;
        }

        @Override
        public void setValue(final double value) {
            this.value = value;
        }

        private double value;
    }

    static class FloatLiteralImpl extends LiteralImpl implements FloatLiteral {

        FloatLiteralImpl(final ASTImpl ast, final float value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitFloatLiteral(this);
        }

        @Override
        public float getValue() {
            return value;
        }

        @Override
        public void setValue(final float value) {
            this.value = value;
        }

        private float value;
    }

    static class IntegerLiteralImpl extends LiteralImpl implements IntegerLiteral {

        IntegerLiteralImpl(final ASTImpl ast, final int value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitIntegerLiteral(this);
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public void setValue(final int value) {
            this.value = value;
        }

        private int value;
    }

    static class LongLiteralImpl extends LiteralImpl implements LongLiteral {

        LongLiteralImpl(final ASTImpl ast, final long value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitLongLiteral(this);
        }

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void setValue(final long value) {
            this.value = value;
        }

        private long value;
    }

    static class NullLiteralImpl extends LiteralImpl implements NullLiteral {

        NullLiteralImpl(final ASTImpl ast) {
            super(ast);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitNullLiteral(this);
        }

    }

    static class StringLiteralImpl extends LiteralImpl implements StringLiteral {

        StringLiteralImpl(final ASTImpl ast, final String value) {
            super(ast);
            setValue(value);
        }

        @Override
        public void accept(final ASTNodeVisitor v) throws Exception {
            v.visitStringLiteral(this);
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(final String value) {
            this.value = value;
        }

        private String value;
    }

    LiteralImpl(final ASTImpl ast) {
        super(ast);

    }

    @Override
    protected int getPrecedence() {
        return Integer.MAX_VALUE;
    }

}
