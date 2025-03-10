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

import org.xtuml.masl.javagen.ast.AST;
import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.AbstractASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.*;
import org.xtuml.masl.javagen.ast.def.Comment;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.*;
import org.xtuml.masl.javagen.ast.types.*;
import org.xtuml.masl.utils.TextUtils;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;

public class CodeWriter extends AbstractASTNodeVisitor {

    @Override
    public final void visitNull() throws Exception {
        writer.write("###NULL###");

    }

    public void clear() {
        writer = new StringWriter();
        indent = 0;
    }

    public String getCode() {
        return writer.toString();
    }

    @Override
    public void visitArrayType(final ArrayType node) throws Exception {
        visit(node.getElementType());
        writer.write("[]");

    }

    @Override
    public void visitAssert(final Assert node) throws Exception {
        writer.write(getIndent());
        visit(node.getCondition());
        if (node.getMessage() != null) {
            writer.write(" : ");
            visit(node.getMessage());
        }
        writer.write(";\n");

    }

    @Override
    public void visitAST(final AST node) throws Exception {
        visit(node.getChildNodes());

    }

    @Override
    public void visitBreak(final Break node) throws Exception {
        writer.write(getIndent());
        writer.write("break");
        if (node.getReferencedLabel() != null) {
            writer.write(" " + node.getReferencedLabel().getName());
        }
        writer.write(";\n");

    }

    @Override
    public void visitCatch(final Catch node) throws Exception {
        writer.write(getIndent());
        writer.write("catch ( ");
        visit(node.getException());
        writer.write(" )\n");
        ++indent;
        visit(node.getCodeBlock());
        --indent;

    }

    @Override
    public void visitCharacterLiteral(final Literal.CharacterLiteral node) throws Exception {
        writer.write("'");
        appendCharLiteral(node.getValue());
        writer.write("'");

    }

    @Override
    public void visitCodeBlock(final CodeBlock node) throws Exception {
        --indent;
        writer.write(getIndent() + "{\n");
        ++indent;
        visit(node.getStatements());
        --indent;
        writer.write(getIndent() + "}\n");
        ++indent;

    }

    @Override
    public void visitComment(final Comment node) throws Exception {
        TextUtils.textBlock(writer, getIndent(), "", "//", node.getText(), "", true);

    }

    @Override
    public void visitCompilationUnit(final CompilationUnit node) throws Exception {
        if (node.getPackage() != null) {
            writer.write("package " + node.getPackage().getName() + ";\n\n");
        }
        visit(node.getImportDeclarations());
        writer.write("\n");
        visit(node.getTypeDeclarations());
        writer.write("\n");

    }

    @Override
    public void visitConditional(final Conditional node) throws Exception {
        visit(node.getCondition());
        writer.write(" ? ");
        visit(node.getTrueValue());
        writer.write(" : ");
        visit(node.getFalseValue());

    }

    @Override
    public void visitConstructor(final Constructor node) throws Exception {
        writer.write(getIndent());
        visit(node.getModifiers());
        formatList(node.getTypeParameters(), "<", ",", "> ");
        writer.write(node.getEnclosingTypeDeclaration().getName() + "(");
        formatList(node.getParameters(), " ", ", ", " ");
        writer.write(")");
        formatList(node.getThrownExceptions(), " throws ", ", ", "");
        if (node.getCodeBlock() == null) {
            writer.write(";");
        } else {
            writer.write("\n");
            ++indent;
            visit(node.getCodeBlock());
            --indent;
        }
        writer.write("\n\n");

    }

    @Override
    public void visitContinue(final Continue node) throws Exception {
        writer.write(getIndent());
        writer.write("continue");
        if (node.getReferencedLabel() != null) {
            writer.write(" " + node.getReferencedLabel().getName());
        }
        writer.write(";\n");

    }

    @Override
    public void visitDeclaredType(final DeclaredType node) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getTypeDeclaration().getName());
        formatList(node.getTypeArguments(), "<", ",", ">");

    }

    @Override
    public void visitDoubleLiteral(final Literal.DoubleLiteral node) throws Exception {
        writer.write(String.valueOf(node.getValue()));

    }

    @Override
    public void visitDoWhile(final DoWhile node) throws Exception {
        writer.write(getIndent());
        writer.write("do\n");
        ++indent;
        visit(node.getStatement());
        --indent;
        writer.write(getIndent());
        writer.write("while (");
        visit(node.getCondition());
        writer.write(" )\n");

    }

    @Override
    public void visitEmptyStatement(final EmptyStatement node) throws Exception {
        writer.write(getIndent());
        writer.write(";\n");

    }

    @Override
    public void visitExpressionStatement(final ExpressionStatement node) throws Exception {
        writer.write(getIndent());
        visit(node.getExpression());
        writer.write(";\n");

    }

    @Override
    public void visitField(final Field node) throws Exception {
        writer.write(getIndent());
        visit(node.getModifiers());
        visit(node.getType());
        writer.write(" " + node.getName());
        if (node.getInitialValue() != null) {
            writer.write(" = ");
            visit(node.getInitialValue());
        }
        writer.write(";\n");

    }

    @Override
    public void visitFieldAccess(final FieldAccess node) throws Exception {
        if (node.getInstance() != null) {
            visit(node.getInstance());
            writer.write(".");
        } else if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getField().getName());

    }

    @Override
    public void visitArrayLengthAccess(final ArrayLengthAccess node) throws Exception {
        visit(node.getInstance());
        writer.write(".length");

    }

    @Override
    public void visitFloatLiteral(final Literal.FloatLiteral node) throws Exception {
        writer.write(node.getValue() + "f");

    }

    @Override
    public void visitFor(final For node) throws Exception {
        writer.write(getIndent());
        writer.write("for ( ");
        if (node.getVariable() != null) {
            visit(node.getVariable());
        } else {
            formatList(node.getStartExpressions(), "", ", ", "");
        }
        if (node.getCollection() != null) {
            writer.write(" : ");
            visit(node.getCollection());
        } else {
            writer.write("; ");
            visit(node.getCondition());
            writer.write("; ");
            formatList(node.getUpdateExpressions(), "", ", ", "");
        }
        writer.write(" )\n");
        ++indent;
        visit(node.getStatement());
        --indent;

    }

    @Override
    public void visitIf(final If node) throws Exception {
        if (!(node.getParentNode() instanceof If && node == ((If) node.getParentNode()).getElse())) {
            writer.write(getIndent());
        }
        writer.write("if ( ");
        visit(node.getCondition());
        writer.write(" )\n");
        ++indent;
        visit(node.getThen());
        --indent;
        if (node.getElse() != null) {
            writer.write(getIndent());
            writer.write("else");
            if (node.getElse() instanceof If) {
                writer.write(" ");
                visit(node.getElse());
            } else {
                writer.write("\n");
                ++indent;
                visit(node.getElse());
                --indent;
            }
        }

    }

    @Override
    public void visitInitializerBlock(final InitializerBlock node) throws Exception {
        if (node.isStatic()) {
            writer.write(getIndent() + "static\n");
        }
        ++indent;
        visit(node.getCodeBlock());
        --indent;

    }

    @Override
    public void visitIntegerLiteral(final Literal.IntegerLiteral node) throws Exception {
        writer.write(String.valueOf(node.getValue()));

    }

    @Override
    public void visitLabeledStatement(final LabeledStatement node) throws Exception {
        writer.write(getIndent());
        writer.write(node.getName() + " :\n");
        ++indent;
        visit(node.getStatement());
        --indent;

    }

    @Override
    public void visitLocalVariable(final LocalVariable node) throws Exception {
        visit(node.getModifiers());
        visit(node.getType());
        writer.write(" " + node.getName());
        if (node.getInitialValue() != null) {
            writer.write(" = ");
            visit(node.getInitialValue());
        }

    }

    @Override
    public void visitLocalVariableDeclaration(final LocalVariableDeclaration node) throws Exception {
        writer.write(getIndent());
        visit(node.getLocalVariable());
        writer.write(";\n");

    }

    @Override
    public void visitLongLiteral(final Literal.LongLiteral node) throws Exception {
        writer.write(node.getValue() + "L");

    }

    @Override
    public void visitMethod(final Method node) throws Exception {
        writer.write(getIndent());
        visit(node.getModifiers());
        formatList(node.getTypeParameters(), "<", ",", "> ");
        visit(node.getReturnType());
        writer.write(" " + node.getName() + " (");
        formatList(node.getParameters(), " ", ", ", " ");
        writer.write(")");
        formatList(node.getThrownExceptions(), " throws ", ", ", "");
        if (node.getCodeBlock() == null) {
            writer.write(";\n");
        } else {
            writer.write("\n");
            ++indent;
            visit(node.getCodeBlock());
            --indent;
        }
        writer.write("\n");

    }

    @Override
    public void visitMethodInvocation(final MethodInvocation node) throws Exception {
        if (node.getInstance() != null) {
            visit(node.getInstance());
            writer.write(".");
        } else if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }

        formatList(node.getTypeArguments(), "<", ",", ">");
        writer.write(node.getMethod().getName());
        writer.write("(");
        formatList(node.getArguments(), "", ", ", "");
        writer.write(")");

    }

    @Override
    public void visitConstructorInvocation(final ConstructorInvocation node) throws Exception {
        writer.write(getIndent());
        if (node.getEnclosingInstance() != null) {
            visit(node.getEnclosingInstance());
            writer.write(".");
        }
        formatList(node.getTypeArguments(), "<", ",", ">");
        if (node.isSuper()) {
            writer.write("super");
        } else {
            writer.write("this");
        }
        writer.write("(");
        formatList(node.getArguments(), "", ", ", "");
        writer.write(");\n");

    }

    @Override
    public void visitModifiers(final Modifiers node) throws Exception {
        for (final Modifier modifier : node.getModifiers()) {
            writer.write(modifier.toString().toLowerCase() + " ");
        }

    }

    @Override
    public void visitNewInstance(final NewInstance node) throws Exception {
        if (node.getOuterInstance() != null) {
            visit(node.getOuterInstance());
            writer.write(".");
        }
        writer.write("new ");
        formatList(node.getTypeArguments(), "<", ",", ">");
        visit(node.getInstanceType());
        writer.write(" (");
        formatList(node.getArguments(), " ", ", ", " ");
        writer.write(")");
        if (node.getTypeBody() != null) {
            writer.write("\n");
            ++indent;
            visit(node.getTypeBody());
            --indent;
        }

    }

    @Override
    public void visitNullLiteral(final Literal.NullLiteral node) throws Exception {
        writer.write("null");

    }

    @Override
    public void visitPackage(final Package node) throws Exception {
        formatList(node.getCompilationUnits(),
                   "",
                   "\n==================================================================================\n",
                   "");

    }

    @Override
    public void visitPackageQualifier(final PackageQualifier node) throws Exception {
        writer.write(node.getPackage().getName());

    }

    @Override
    public void visitParameter(final Parameter node) throws Exception {
        visit(node.getModifiers());
        if (node.getType() instanceof ArrayType &&
            node.getParentCallable().isVarArgs() &&
            node.getParentCallable().getParameters().get(node.getParentCallable().getParameters().size() - 1) == node) {
            visit(((ArrayType) node.getType()).getElementType());
            writer.write("...");
        } else {
            visit(node.getType());
        }
        writer.write(" " + node.getName());

    }

    @Override
    public void visitParenthesizedExpression(final ParenthesizedExpression node) throws Exception {
        writer.write("(");
        visit(node.getExpression());
        writer.write(")");

    }

    @Override
    public void visitPostfixExpression(final PostfixExpression node) throws Exception {
        visit(node.getExpression());
        switch (node.getOperator()) {
            case INCREMENT:
                writer.write("++");
                break;
            case DECREMENT:
                writer.write("--");
                break;
        }

    }

    @Override
    public void visitPrefixExpression(final PrefixExpression node) throws Exception {
        switch (node.getOperator()) {
            case INCREMENT:
                writer.write("++");
                break;
            case DECREMENT:
                writer.write("--");
                break;
        }
        visit(node.getExpression());

    }

    @Override
    public void visitPrimitiveType(final PrimitiveType node) throws Exception {
        switch (node.getTag()) {
            case BOOLEAN:
                writer.write("boolean");
                break;
            case BYTE:
                writer.write("byte");
                break;
            case CHAR:
                writer.write("char");
                break;
            case DOUBLE:
                writer.write("double");
                break;
            case FLOAT:
                writer.write("float");
                break;
            case INT:
                writer.write("int");
                break;
            case LONG:
                writer.write("long");
                break;
            case SHORT:
                writer.write("short");
                break;
            case VOID:
                writer.write("void");
                break;
        }

    }

    @Override
    public void visitReturn(final Return node) throws Exception {
        writer.write(getIndent());
        writer.write("return");
        if (node.getReturnValue() != null) {
            writer.write(" ");
            visit(node.getReturnValue());
        }
        writer.write(";\n");

    }

    @Override
    public void visitStringLiteral(final Literal.StringLiteral node) throws Exception {
        writer.write("\"");
        for (final char a : node.getValue().toCharArray()) {
            appendCharLiteral(a);
        }
        writer.write("\"");

    }

    @Override
    public void visitSuperQualifier(final SuperQualifier node) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write("super");

    }

    @Override
    public void visitSwitch(final Switch node) throws Exception {
        writer.write(getIndent());
        writer.write("switch ( ");
        visit(node.getDiscriminator());
        writer.write(")\n");
        writer.write(getIndent());
        writer.write("{\n");
        ++indent;
        visit(node.getSwitchBlocks());
        --indent;
        writer.write(getIndent());
        writer.write("}\n");

    }

    @Override
    public void visitSwitchBlock(final Switch.SwitchBlock node) throws Exception {
        formatList(node.getCaseLabels(), getIndent() + "case ", ":\n" + getIndent() + "case ", ":\n");
        if (node.isDefault()) {
            writer.write(getIndent() + "default:\n");
        }
        ++indent;
        visit(node.getStatements());
        --indent;

    }

    @Override
    public void visitSynchronizedBlock(final SynchronizedBlock node) throws Exception {
        writer.write(getIndent());
        writer.write("synchronised (");
        visit(node.getLockExpression());
        writer.write(" )\n");
        ++indent;
        visit(node.getCodeBlock());
        --indent;

    }

    @Override
    public void visitThis(final This node) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write("this");

    }

    @Override
    public void visitThrow(final Throw node) throws Exception {
        writer.write(getIndent());
        writer.write("throw ");
        visit(node.getThrownExpression());
        writer.write(";\n");

    }

    @Override
    public void visitTry(final Try node) throws Exception {
        writer.write(getIndent());
        writer.write("try\n");
        ++indent;
        visit(node.getMainBlock());
        --indent;
        visit(node.getCatches());
        if (node.getFinallyBlock() != null) {
            writer.write(getIndent());
            writer.write("finally\n");
            ++indent;
            visit(node.getFinallyBlock());
            --indent;
        }

    }

    @Override
    public void visitTypeBody(final TypeBody node) throws Exception {
        writer.write(getIndent() + "{\n");
        ++indent;
        formatList(node.getEnumConstants(), "", ",\n", ";\n");
        visit(node.getMembers());
        --indent;
        writer.write(getIndent() + "}");

    }

    @Override
    public void visitTypeDeclaration(final TypeDeclaration node) throws Exception {
        writer.write(getIndent());
        visit(node.getModifiers());
        if (node.isInterface()) {
            if (node.isAnnotation()) {
                writer.write("@");
            }
            writer.write("interface");
        } else if (node.isEnum()) {
            writer.write("enum");
        } else {
            writer.write("class");
        }
        writer.write(" " + node.getName());
        formatList(node.getTypeParameters(), "<", ",", ">");
        if (node.isInterface()) {
            formatList(node.getInterfaces(), "\n" + getIndent() + "  extends ", ",\n" + getIndent() + "          ", "");
        } else {
            if (node.getSupertype() != null && !isJavaLangObject(node.getSupertype())) {
                writer.write("\n" + getIndent() + "  extends ");
                visit(node.getSupertype());
            }
            formatList(node.getInterfaces(),
                       "\n" + getIndent() + "  implements ",
                       ",\n" + getIndent() + "            ",
                       "");
        }
        writer.write("\n");
        visit(node.getTypeBody());
        writer.write("\n\n");

    }

    @Override
    public void visitTypeDeclarationStatement(final TypeDeclarationStatement node) throws Exception {
        visit(node.getTypeDeclaration());

    }

    @Override
    public void visitTypeParameter(final TypeParameter node) throws Exception {
        writer.write(node.getName());
        formatList(node.getExtendsBounds(), " extends ", " & ", "");

    }

    @Override
    public void visitTypeQualifier(final TypeQualifier node) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getTypeDeclaration().getName());

    }

    @Override
    public void visitTypeVariable(final TypeVariable node) throws Exception {
        writer.write(node.getName());

    }

    @Override
    public void visitUnaryExpression(final UnaryExpression node) throws Exception {
        switch (node.getOperator()) {
            case PLUS:
                writer.write("+");
                break;
            case MINUS:
                writer.write("-");
                break;
            case BITWISE_COMPLEMENT:
                writer.write("~");
                break;
            case NOT:
                writer.write("!");
                break;
        }
        visit(node.getExpression());

    }

    @Override
    public void visitvariableAccess(final VariableAccess node) throws Exception {
        writer.write(node.getVariable().getName());

    }

    @Override
    public void visitWhile(final While node) throws Exception {
        writer.write(getIndent());
        writer.write("while (");
        visit(node.getCondition());
        writer.write(" )\n");
        ++indent;
        visit(node.getStatement());
        --indent;

    }

    @Override
    public void visitWildcardType(final WildcardType node) throws Exception {
        writer.write("?");
        if (node.getSuperBound() != null) {
            writer.write(" super ");
            visit(node.getSuperBound());
        }
        if (node.getExtendsBound() != null && !isJavaLangObject(node.getExtendsBound())) {
            writer.write(" extends ");
            visit(node.getExtendsBound());
        }

    }

    private void formatList(final Collection<? extends ASTNode> nodes,
                            final String before,
                            final String separator,
                            final String after) throws Exception {
        if (nodes.size() > 0) {
            writer.write(before);
        }
        for (final Iterator<? extends ASTNode> it = nodes.iterator(); it.hasNext(); ) {
            visit(it.next());
            if (it.hasNext()) {
                writer.write(separator);
            }
        }
        if (nodes.size() > 0) {
            writer.write(after);
        }
    }

    private String getIndent() {
        return spaces.substring(0, indent * 2);
    }

    private boolean isJavaLangObject(final Type type) {
        return type instanceof DeclaredType &&
               ((DeclaredType) type).getTypeDeclaration() == type.getAST().getTypeDeclaration(Object.class);
    }

    private Writer writer;

    private int indent = 0;

    private static final String
            spaces =
            "                                                                                                                           ";

    @Override
    public void visitArrayAccess(final ArrayAccess node) throws Exception {
        visit(node.getArrayExpression());
        writer.write("[");
        visit(node.getIndexExpression());
        writer.write("]");

    }

    @Override
    public void visitArrayInitializer(final ArrayInitializer node) throws Exception {
        writer.write("{");
        formatList(node.getElements(), " ", ", ", " ");
        writer.write("}");

    }

    @Override
    public void visitAssignmen(final Assignment node) throws Exception {
        visit(node.getTarget());
        writer.write(" ");
        switch (node.getOperator()) {
            case ADD:
                writer.write("+=");
                break;
            case SUBTRACT:
                writer.write("-=");
                break;
            case ASSIGN:
                writer.write("=");
                break;
            case DIVIDE:
                writer.write("/=");
                break;
            case MULTIPLY:
                writer.write("*=");
                break;
            case REMAINDER:
                writer.write("%=");
                break;
            case LEFT_SHIFT:
                writer.write("<<=");
                break;
            case RIGHT_SHIFT:
                writer.write(">>=");
                break;
            case RIGHT_SHIFT_ZERO_EXTEND:
                writer.write(">>>=");
                break;
            case BITWISE_AND:
                writer.write("&=");
                break;
            case BITWISE_OR:
                writer.write("|=");
                break;
            case BITWISE_XOR:
                writer.write("^=");
                break;
        }
        writer.write(" ");
        visit(node.getSource());

    }

    @Override
    public void visitBinaryExpression(final BinaryExpression node) throws Exception {
        visit(node.getLhs());
        writer.write(" ");
        switch (node.getOperator()) {
            case ADD:
                writer.write("+");
                break;
            case SUBTRACT:
                writer.write("-");
                break;
            case DIVIDE:
                writer.write("/");
                break;
            case MULTIPLY:
                writer.write("*");
                break;
            case REMAINDER:
                writer.write("%");
                break;
            case LEFT_SHIFT:
                writer.write("<<");
                break;
            case RIGHT_SHIFT:
                writer.write(">>");
                break;
            case RIGHT_SHIFT_UNSIGNED:
                writer.write(">>>");
                break;
            case BITWISE_AND:
                writer.write("&");
                break;
            case BITWISE_OR:
                writer.write("|");
                break;
            case BITWISE_XOR:
                writer.write("^");
                break;
            case AND:
                writer.write("&&");
                break;
            case OR:
                writer.write("||");
                break;
            case EQUAL_TO:
                writer.write("==");
                break;
            case LESS_THAN:
                writer.write("<");
                break;
            case GREATER_THAN:
                writer.write(">");
                break;
            case LESS_THAN_OR_EQUAL_TO:
                writer.write("<=");
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                writer.write(">=");
                break;
            case NOT_EQUAL_TO:
                writer.write("!=");
                break;
            case INSTANCEOF:
                writer.write("instanceof");
                break;

        }
        writer.write(" ");
        visit(node.getRhs());

    }

    @Override
    public void visitBooleanLiteral(final Literal.BooleanLiteral node) throws Exception {
        writer.write(node.getValue() ? "true" : "false");

    }

    @Override
    public void visitCast(final Cast node) throws Exception {
        writer.write("(");
        visit(node.getType());
        writer.write(")");
        visit(node.getExpression());

    }

    @Override
    public void visitEnumConstant(final EnumConstant node) throws Exception {
        writer.write(getIndent() + node.getName());
        formatList(node.getArguments(), "( ", ", ", " )");
        if (node.getTypeBody() != null) {
            writer.write("\n");
            ++indent;
            visit(node.getTypeBody());
            writer.write("\n");
            --indent;
        }

    }

    @Override
    public void visitEnumConstantAccess(final EnumConstantAccess node) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getConstant().getName());

    }

    @Override
    public void visitNewArray(final NewArray node) throws Exception {
        writer.write("new ");
        Type type = node.getType();
        while (type instanceof ArrayType) {
            type = ((ArrayType) type).getElementType();
        }
        visit(type);
        formatList(node.getDimensionSizes(), "[", "][", "]");
        for (int i = node.getDimensionSizes().size(); i < node.getNoDimensions(); ++i) {
            writer.write("[]");
        }
        if (node.getInitialValue() != null) {
            visit(node.getInitialValue());
        }

    }

    private void appendCharLiteral(final char literal) throws Exception {
        switch (literal) {
            case '\b':
                writer.write("\\b");
                break;
            case '\t':
                writer.write("\\t");
                break;
            case '\n':
                writer.write("\\n");
                break;
            case '\f':
                writer.write("\\f");
                break;
            case '\r':
                writer.write("\\r");
                break;
            case '\"':
                writer.write("\\\"");
                break;
            case '\'':
                writer.write("\\'");
                break;
            case '\\':
                writer.write("\\\\");
                break;
            default:
                if (literal < '\u001F' || literal > '\u007F') {
                    writer.write("\\u");
                    final String txtRep = Integer.toHexString(literal);
                    writer.write("0000".substring(txtRep.length()));
                    writer.write(txtRep);
                } else {

                    writer.write(literal);
                }
                break;
        }
    }

    @Override
    public void visitClassLiteral(final ClassLiteral node) throws Exception {
        visit(node.getType());
        writer.write(".class");

    }

    private void writeQualifiedName(final TypeDeclaration declaration) throws Exception {
        if (declaration.getDeclaringCompilationUnit() != null) {
            writer.write(declaration.getEnclosingPackage().getName());
        } else {
            writeQualifiedName(declaration.getDeclaringType());
        }
        writer.write("." + declaration.getName());
    }

    @Override
    public void visitImport(final Import node) throws Exception {
        writer.write(getIndent() + "import ");
        if (node.isStatic()) {
            writer.write("static ");
        }
        if (node.getParentPackage() != null) {
            writer.write(node.getParentPackage().getName());
        } else {
            writeQualifiedName(node.getTypeDeclaration());
        }
        if (node.isOnDemand()) {
            writer.write(".*");
        } else if (node.getImportedName() != null) {
            writer.write("." + node.getImportedName());
        }
        writer.write(";\n");

    }

    public void dump(final File outputDirectory, final Package pkg) throws Exception {
        for (final CompilationUnit cu : pkg.getCompilationUnits()) {
            dump(outputDirectory, cu);
        }
    }

    public void dump(final File outputDirectory, final CompilationUnit cu) throws Exception {
        final File outputFile = new File(outputDirectory, cu.getFileName());
        outputFile.getParentFile().mkdirs();
        clear();
        writer = new BufferedWriter(new FileWriter(outputFile));
        visit(cu);
        writer.flush();
        writer.close();
    }

    public void writeCode(final Writer writer, final CompilationUnit cu) throws Exception {
        this.writer = writer;
        visit(cu);
        writer.flush();
        writer.close();
    }

}
