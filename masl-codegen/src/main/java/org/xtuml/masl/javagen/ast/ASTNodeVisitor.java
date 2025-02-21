/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast;

import org.xtuml.masl.javagen.ast.code.*;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.*;
import org.xtuml.masl.javagen.ast.types.*;

public interface ASTNodeVisitor {

    void visitNull() throws Exception;

    void visit(ASTNode node) throws Exception;

    void visitArrayAccess(ArrayAccess node) throws Exception;

    void visitImport(Import node) throws Exception;

    void visitClassLiteral(ClassLiteral node) throws Exception;

    void visitNewArray(NewArray node) throws Exception;

    void visitArrayInitializer(ArrayInitializer node) throws Exception;

    void visitArrayType(ArrayType node) throws Exception;

    void visitAssert(Assert node) throws Exception;

    void visitAssignmen(Assignment node) throws Exception;

    void visitAST(AST node) throws Exception;

    void visitBinaryExpression(BinaryExpression node) throws Exception;

    void visitBooleanLiteral(Literal.BooleanLiteral node) throws Exception;

    void visitBreak(Break node) throws Exception;

    void visitCast(Cast node) throws Exception;

    void visitCatch(Catch node) throws Exception;

    void visitCharacterLiteral(Literal.CharacterLiteral node) throws Exception;

    void visitCodeBlock(CodeBlock node) throws Exception;

    void visitComment(Comment node) throws Exception;

    void visitCompilationUnit(CompilationUnit node) throws Exception;

    void visitConditional(Conditional node) throws Exception;

    void visitConstructor(Constructor node) throws Exception;

    void visitContinue(Continue node) throws Exception;

    void visitDeclaredType(DeclaredType node) throws Exception;

    void visitDoubleLiteral(Literal.DoubleLiteral node) throws Exception;

    void visitDoWhile(DoWhile node) throws Exception;

    void visitEmptyStatement(EmptyStatement node) throws Exception;

    void visitEnumConstant(EnumConstant node) throws Exception;

    void visitEnumConstantAccess(EnumConstantAccess node) throws Exception;

    void visitExpressionStatement(ExpressionStatement node) throws Exception;

    void visitFieldAccess(FieldAccess node) throws Exception;

    void visitField(Field node) throws Exception;

    void visitFloatLiteral(Literal.FloatLiteral node) throws Exception;

    void visitFor(For node) throws Exception;

    void visitIf(If node) throws Exception;

    void visitInitializerBlock(InitializerBlock node) throws Exception;

    void visitNewInstance(NewInstance node) throws Exception;

    void visitIntegerLiteral(Literal.IntegerLiteral node) throws Exception;

    void visitLabeledStatement(LabeledStatement node) throws Exception;

    void visitLocalVariable(LocalVariable node) throws Exception;

    void visitLongLiteral(Literal.LongLiteral node) throws Exception;

    void visitMethod(Method node) throws Exception;

    void visitMethodInvocation(MethodInvocation node) throws Exception;

    void visitModifiers(Modifiers node) throws Exception;

    void visitNullLiteral(Literal.NullLiteral node) throws Exception;

    void visitPackage(Package node) throws Exception;

    void visitPackageQualifier(PackageQualifier node) throws Exception;

    void visitParameter(Parameter node) throws Exception;

    void visitParenthesizedExpression(ParenthesizedExpression node) throws Exception;

    void visitPostfixExpression(PostfixExpression node) throws Exception;

    void visitPrefixExpression(PrefixExpression node) throws Exception;

    void visitPrimitiveType(PrimitiveType node) throws Exception;

    void visitReturn(Return node) throws Exception;

    void visitStringLiteral(Literal.StringLiteral node) throws Exception;

    void visitSuperQualifier(SuperQualifier node) throws Exception;

    void visitSwitchBlock(Switch.SwitchBlock node) throws Exception;

    void visitSwitch(Switch node) throws Exception;

    void visitSynchronizedBlock(SynchronizedBlock node) throws Exception;

    void visitThis(This node) throws Exception;

    void visitThrow(Throw node) throws Exception;

    void visitTry(Try node) throws Exception;

    void visitTypeBody(TypeBody node) throws Exception;

    void visitTypeDeclaration(TypeDeclaration node) throws Exception;

    void visitTypeDeclarationStatement(TypeDeclarationStatement node) throws Exception;

    void visitTypeParameter(TypeParameter node) throws Exception;

    void visitTypeQualifier(TypeQualifier node) throws Exception;

    void visitTypeVariable(TypeVariable node) throws Exception;

    void visitUnaryExpression(UnaryExpression node) throws Exception;

    void visitvariableAccess(VariableAccess node) throws Exception;

    void visitLocalVariableDeclaration(LocalVariableDeclaration node) throws Exception;

    void visitWhile(While node) throws Exception;

    void visitWildcardType(WildcardType node) throws Exception;

    void visitArrayLengthAccess(ArrayLengthAccess node) throws Exception;

    void visitConstructorInvocation(ConstructorInvocation node) throws Exception;
}
