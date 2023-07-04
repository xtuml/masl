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

public class CodeWriter extends AbstractASTNodeVisitor<Void, Void> {

    @Override
    public final Void visitNull(final Void p) throws Exception {
        writer.write("###NULL###");
        return null;
    }

    public void clear() {
        writer = new StringWriter();
        indent = 0;
    }

    public String getCode() {
        return writer.toString();
    }

    @Override
    public Void visitArrayType(final ArrayType node, final Void param) throws Exception {
        visit(node.getElementType());
        writer.write("[]");
        return null;
    }

    @Override
    public Void visitAssert(final Assert node, final Void param) throws Exception {
        writer.write(getIndent());
        visit(node.getCondition());
        if (node.getMessage() != null) {
            writer.write(" : ");
            visit(node.getMessage());
        }
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitAST(final AST node, final Void param) throws Exception {
        visit(node.getChildNodes());
        return null;
    }

    @Override
    public Void visitBreak(final Break node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("break");
        if (node.getReferencedLabel() != null) {
            writer.write(" " + node.getReferencedLabel().getName());
        }
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitCatch(final Catch node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("catch ( ");
        visit(node.getException());
        writer.write(" )\n");
        ++indent;
        visit(node.getCodeBlock());
        --indent;
        return null;
    }

    @Override
    public Void visitCharacterLiteral(final Literal.CharacterLiteral node, final Void param) throws Exception {
        writer.write("'");
        appendCharLiteral(node.getValue());
        writer.write("'");
        return null;
    }

    @Override
    public Void visitCodeBlock(final CodeBlock node, final Void param) throws Exception {
        --indent;
        writer.write(getIndent() + "{\n");
        ++indent;
        visit(node.getStatements());
        --indent;
        writer.write(getIndent() + "}\n");
        ++indent;
        return null;
    }

    @Override
    public Void visitComment(final Comment node, final Void param) throws Exception {
        TextUtils.textBlock(writer, getIndent(), "", "//", node.getText(), "", true);
        return null;
    }

    @Override
    public Void visitCompilationUnit(final CompilationUnit node, final Void param) throws Exception {
        if (node.getPackage() != null) {
            writer.write("package " + node.getPackage().getName() + ";\n\n");
        }
        visit(node.getImportDeclarations());
        writer.write("\n");
        visit(node.getTypeDeclarations());
        writer.write("\n");
        return null;
    }

    @Override
    public Void visitConditional(final Conditional node, final Void param) throws Exception {
        visit(node.getCondition());
        writer.write(" ? ");
        visit(node.getTrueValue());
        writer.write(" : ");
        visit(node.getFalseValue());
        return null;
    }

    @Override
    public Void visitConstructor(final Constructor node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitContinue(final Continue node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("continue");
        if (node.getReferencedLabel() != null) {
            writer.write(" " + node.getReferencedLabel().getName());
        }
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitDeclaredType(final DeclaredType node, final Void param) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getTypeDeclaration().getName());
        formatList(node.getTypeArguments(), "<", ",", ">");
        return null;
    }

    @Override
    public Void visitDoubleLiteral(final Literal.DoubleLiteral node, final Void param) throws Exception {
        writer.write(String.valueOf(node.getValue()));
        return null;
    }

    @Override
    public Void visitDoWhile(final DoWhile node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("do\n");
        ++indent;
        visit(node.getStatement());
        --indent;
        writer.write(getIndent());
        writer.write("while (");
        visit(node.getCondition());
        writer.write(" )\n");
        return null;
    }

    @Override
    public Void visitEmptyStatement(final EmptyStatement node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitExpressionStatement(final ExpressionStatement node, final Void param) throws Exception {
        writer.write(getIndent());
        visit(node.getExpression());
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitField(final Field node, final Void param) throws Exception {
        writer.write(getIndent());
        visit(node.getModifiers());
        visit(node.getType());
        writer.write(" " + node.getName());
        if (node.getInitialValue() != null) {
            writer.write(" = ");
            visit(node.getInitialValue());
        }
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitFieldAccess(final FieldAccess node, final Void param) throws Exception {
        if (node.getInstance() != null) {
            visit(node.getInstance());
            writer.write(".");
        } else if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getField().getName());
        return null;
    }

    @Override
    public Void visitArrayLengthAccess(final ArrayLengthAccess node, final Void param) throws Exception {
        visit(node.getInstance());
        writer.write(".length");
        return null;
    }

    @Override
    public Void visitFloatLiteral(final Literal.FloatLiteral node, final Void param) throws Exception {
        writer.write(node.getValue() + "f");
        return null;
    }

    @Override
    public Void visitFor(final For node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitIf(final If node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitInitializerBlock(final InitializerBlock node, final Void param) throws Exception {
        if (node.isStatic()) {
            writer.write(getIndent() + "static\n");
        }
        ++indent;
        visit(node.getCodeBlock());
        --indent;
        return null;
    }

    @Override
    public Void visitIntegerLiteral(final Literal.IntegerLiteral node, final Void param) throws Exception {
        writer.write(String.valueOf(node.getValue()));
        return null;
    }

    @Override
    public Void visitLabeledStatement(final LabeledStatement node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write(node.getName() + " :\n");
        ++indent;
        visit(node.getStatement());
        --indent;
        return null;
    }

    @Override
    public Void visitLocalVariable(final LocalVariable node, final Void param) throws Exception {
        visit(node.getModifiers());
        visit(node.getType());
        writer.write(" " + node.getName());
        if (node.getInitialValue() != null) {
            writer.write(" = ");
            visit(node.getInitialValue());
        }
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(final LocalVariableDeclaration node, final Void param) throws Exception {
        writer.write(getIndent());
        visit(node.getLocalVariable());
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitLongLiteral(final Literal.LongLiteral node, final Void param) throws Exception {
        writer.write(node.getValue() + "L");
        return null;
    }

    @Override
    public Void visitMethod(final Method node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitMethodInvocation(final MethodInvocation node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitConstructorInvocation(final ConstructorInvocation node, final Void param) throws Exception {
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

        return null;
    }

    @Override
    public Void visitModifiers(final Modifiers node, final Void param) throws Exception {
        for (final Modifier modifier : node.getModifiers()) {
            writer.write(modifier.toString().toLowerCase() + " ");
        }
        return null;
    }

    @Override
    public Void visitNewInstance(final NewInstance node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitNullLiteral(final Literal.NullLiteral node, final Void param) throws Exception {
        writer.write("null");
        return null;
    }

    @Override
    public Void visitPackage(final Package node, final Void param) throws Exception {
        formatList(node.getCompilationUnits(),
                   "",
                   "\n==================================================================================\n",
                   "");
        return null;
    }

    @Override
    public Void visitPackageQualifier(final PackageQualifier node, final Void param) throws Exception {
        writer.write(node.getPackage().getName());
        return null;
    }

    @Override
    public Void visitParameter(final Parameter node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitParenthesizedExpression(final ParenthesizedExpression node, final Void param) throws Exception {
        writer.write("(");
        visit(node.getExpression());
        writer.write(")");
        return null;
    }

    @Override
    public Void visitPostfixExpression(final PostfixExpression node, final Void param) throws Exception {
        visit(node.getExpression());
        switch (node.getOperator()) {
            case INCREMENT:
                writer.write("++");
                break;
            case DECREMENT:
                writer.write("--");
                break;
        }
        return null;
    }

    @Override
    public Void visitPrefixExpression(final PrefixExpression node, final Void param) throws Exception {
        switch (node.getOperator()) {
            case INCREMENT:
                writer.write("++");
                break;
            case DECREMENT:
                writer.write("--");
                break;
        }
        visit(node.getExpression());
        return null;
    }

    @Override
    public Void visitPrimitiveType(final PrimitiveType node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitReturn(final Return node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("return");
        if (node.getReturnValue() != null) {
            writer.write(" ");
            visit(node.getReturnValue());
        }
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitStringLiteral(final Literal.StringLiteral node, final Void param) throws Exception {
        writer.write("\"");
        for (final char a : node.getValue().toCharArray()) {
            appendCharLiteral(a);
        }
        writer.write("\"");

        return null;
    }

    @Override
    public Void visitSuperQualifier(final SuperQualifier node, final Void param) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write("super");
        return null;
    }

    @Override
    public Void visitSwitch(final Switch node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitSwitchBlock(final Switch.SwitchBlock node, final Void param) throws Exception {
        formatList(node.getCaseLabels(), getIndent() + "case ", ":\n" + getIndent() + "case ", ":\n");
        if (node.isDefault()) {
            writer.write(getIndent() + "default:\n");
        }
        ++indent;
        visit(node.getStatements());
        --indent;
        return null;
    }

    @Override
    public Void visitSynchronizedBlock(final SynchronizedBlock node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("synchronised (");
        visit(node.getLockExpression());
        writer.write(" )\n");
        ++indent;
        visit(node.getCodeBlock());
        --indent;
        return null;
    }

    @Override
    public Void visitThis(final This node, final Void param) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write("this");
        return null;
    }

    @Override
    public Void visitThrow(final Throw node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("throw ");
        visit(node.getThrownExpression());
        writer.write(";\n");
        return null;
    }

    @Override
    public Void visitTry(final Try node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitTypeBody(final TypeBody node, final Void param) throws Exception {
        writer.write(getIndent() + "{\n");
        ++indent;
        formatList(node.getEnumConstants(), "", ",\n", ";\n");
        visit(node.getMembers());
        --indent;
        writer.write(getIndent() + "}");
        return null;
    }

    @Override
    public Void visitTypeDeclaration(final TypeDeclaration node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitTypeDeclarationStatement(final TypeDeclarationStatement node, final Void param) throws Exception {
        visit(node.getTypeDeclaration());
        return null;
    }

    @Override
    public Void visitTypeParameter(final TypeParameter node, final Void param) throws Exception {
        writer.write(node.getName());
        formatList(node.getExtendsBounds(), " extends ", " & ", "");
        return null;
    }

    @Override
    public Void visitTypeQualifier(final TypeQualifier node, final Void param) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getTypeDeclaration().getName());
        return null;
    }

    @Override
    public Void visitTypeVariable(final TypeVariable node, final Void param) throws Exception {
        writer.write(node.getName());
        return null;
    }

    @Override
    public Void visitUnaryExpression(final UnaryExpression node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitvariableAccess(final VariableAccess node, final Void param) throws Exception {
        writer.write(node.getVariable().getName());
        return null;
    }

    @Override
    public Void visitWhile(final While node, final Void param) throws Exception {
        writer.write(getIndent());
        writer.write("while (");
        visit(node.getCondition());
        writer.write(" )\n");
        ++indent;
        visit(node.getStatement());
        --indent;
        return null;
    }

    @Override
    public Void visitWildcardType(final WildcardType node, final Void param) throws Exception {
        writer.write("?");
        if (node.getSuperBound() != null) {
            writer.write(" super ");
            visit(node.getSuperBound());
        }
        if (node.getExtendsBound() != null && !isJavaLangObject(node.getExtendsBound())) {
            writer.write(" extends ");
            visit(node.getExtendsBound());
        }
        return null;
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
    public Void visitArrayAccess(final ArrayAccess node, final Void param) throws Exception {
        visit(node.getArrayExpression());
        writer.write("[");
        visit(node.getIndexExpression());
        writer.write("]");
        return null;
    }

    @Override
    public Void visitArrayInitializer(final ArrayInitializer node, final Void param) throws Exception {
        writer.write("{");
        formatList(node.getElements(), " ", ", ", " ");
        writer.write("}");
        return null;
    }

    @Override
    public Void visitAssignmen(final Assignment node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitBinaryExpression(final BinaryExpression node, final Void param) throws Exception {
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
        return null;
    }

    @Override
    public Void visitBooleanLiteral(final Literal.BooleanLiteral node, final Void param) throws Exception {
        writer.write(node.getValue() ? "true" : "false");
        return null;
    }

    @Override
    public Void visitCast(final Cast node, final Void param) throws Exception {
        writer.write("(");
        visit(node.getType());
        writer.write(")");
        visit(node.getExpression());
        return null;
    }

    @Override
    public Void visitEnumConstant(final EnumConstant node, final Void param) throws Exception {
        writer.write(getIndent() + node.getName());
        formatList(node.getArguments(), "( ", ", ", " )");
        if (node.getTypeBody() != null) {
            writer.write("\n");
            ++indent;
            visit(node.getTypeBody());
            writer.write("\n");
            --indent;
        }
        return null;
    }

    @Override
    public Void visitEnumConstantAccess(final EnumConstantAccess node, final Void param) throws Exception {
        if (node.getQualifier() != null) {
            visit(node.getQualifier());
            writer.write(".");
        }
        writer.write(node.getConstant().getName());
        return null;
    }

    @Override
    public Void visitNewArray(final NewArray node, final Void param) throws Exception {
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
        return null;
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
    public Void visitClassLiteral(final ClassLiteral node, final Void param) throws Exception {
        visit(node.getType());
        writer.write(".class");
        return null;
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
    public Void visitImport(final Import node, final Void param) throws Exception {
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
        return null;
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
