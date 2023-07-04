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
package org.xtuml.masl.javagen.ast;

import org.xtuml.masl.javagen.ast.code.*;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.*;
import org.xtuml.masl.javagen.ast.types.*;

public interface ASTNodeVisitor<R, P> {

    R visitNull(P param) throws Exception;

    R visit(ASTNode node, P param) throws Exception;

    R visitArrayAccess(ArrayAccess node, P param) throws Exception;

    R visitImport(Import node, P param) throws Exception;

    R visitClassLiteral(ClassLiteral node, P param) throws Exception;

    R visitNewArray(NewArray node, P param) throws Exception;

    R visitArrayInitializer(ArrayInitializer node, P param) throws Exception;

    R visitArrayType(ArrayType node, P param) throws Exception;

    R visitAssert(Assert node, P param) throws Exception;

    R visitAssignmen(Assignment node, P param) throws Exception;

    R visitAST(AST node, P param) throws Exception;

    R visitBinaryExpression(BinaryExpression node, P param) throws Exception;

    R visitBooleanLiteral(Literal.BooleanLiteral node, P param) throws Exception;

    R visitBreak(Break node, P param) throws Exception;

    R visitCast(Cast node, P param) throws Exception;

    R visitCatch(Catch node, P param) throws Exception;

    R visitCharacterLiteral(Literal.CharacterLiteral node, P param) throws Exception;

    R visitCodeBlock(CodeBlock node, P param) throws Exception;

    R visitComment(Comment node, P param) throws Exception;

    R visitCompilationUnit(CompilationUnit node, P param) throws Exception;

    R visitConditional(Conditional node, P param) throws Exception;

    R visitConstructor(Constructor node, P param) throws Exception;

    R visitContinue(Continue node, P param) throws Exception;

    R visitDeclaredType(DeclaredType node, P param) throws Exception;

    R visitDoubleLiteral(Literal.DoubleLiteral node, P param) throws Exception;

    R visitDoWhile(DoWhile node, P param) throws Exception;

    R visitEmptyStatement(EmptyStatement node, P param) throws Exception;

    R visitEnumConstant(EnumConstant node, P param) throws Exception;

    R visitEnumConstantAccess(EnumConstantAccess node, P param) throws Exception;

    R visitExpressionStatement(ExpressionStatement node, P param) throws Exception;

    R visitFieldAccess(FieldAccess node, P param) throws Exception;

    R visitField(Field node, P param) throws Exception;

    R visitFloatLiteral(Literal.FloatLiteral node, P param) throws Exception;

    R visitFor(For node, P param) throws Exception;

    R visitIf(If node, P param) throws Exception;

    R visitInitializerBlock(InitializerBlock node, P param) throws Exception;

    R visitNewInstance(NewInstance node, P param) throws Exception;

    R visitIntegerLiteral(Literal.IntegerLiteral node, P param) throws Exception;

    R visitLabeledStatement(LabeledStatement node, P param) throws Exception;

    R visitLocalVariable(LocalVariable node, P param) throws Exception;

    R visitLongLiteral(Literal.LongLiteral node, P param) throws Exception;

    R visitMethod(Method node, P param) throws Exception;

    R visitMethodInvocation(MethodInvocation node, P param) throws Exception;

    R visitModifiers(Modifiers node, P param) throws Exception;

    R visitNullLiteral(Literal.NullLiteral node, P param) throws Exception;

    R visitPackage(Package node, P param) throws Exception;

    R visitPackageQualifier(PackageQualifier node, P param) throws Exception;

    R visitParameter(Parameter node, P param) throws Exception;

    R visitParenthesizedExpression(ParenthesizedExpression node, P param) throws Exception;

    R visitPostfixExpression(PostfixExpression node, P param) throws Exception;

    R visitPrefixExpression(PrefixExpression node, P param) throws Exception;

    R visitPrimitiveType(PrimitiveType node, P param) throws Exception;

    R visitReturn(Return node, P param) throws Exception;

    R visitStringLiteral(Literal.StringLiteral node, P param) throws Exception;

    R visitSuperQualifier(SuperQualifier node, P param) throws Exception;

    R visitSwitchBlock(Switch.SwitchBlock node, P param) throws Exception;

    R visitSwitch(Switch node, P param) throws Exception;

    R visitSynchronizedBlock(SynchronizedBlock node, P param) throws Exception;

    R visitThis(This node, P param) throws Exception;

    R visitThrow(Throw node, P param) throws Exception;

    R visitTry(Try node, P param) throws Exception;

    R visitTypeBody(TypeBody node, P param) throws Exception;

    R visitTypeDeclaration(TypeDeclaration node, P param) throws Exception;

    R visitTypeDeclarationStatement(TypeDeclarationStatement node, P param) throws Exception;

    R visitTypeParameter(TypeParameter node, P param) throws Exception;

    R visitTypeQualifier(TypeQualifier node, P param) throws Exception;

    R visitTypeVariable(TypeVariable node, P param) throws Exception;

    R visitUnaryExpression(UnaryExpression node, P param) throws Exception;

    R visitvariableAccess(VariableAccess node, P param) throws Exception;

    R visitLocalVariableDeclaration(LocalVariableDeclaration node, P param) throws Exception;

    R visitWhile(While node, P param) throws Exception;

    R visitWildcardType(WildcardType node, P param) throws Exception;

    R visitArrayLengthAccess(ArrayLengthAccess node, P param) throws Exception;

    R visitConstructorInvocation(ConstructorInvocation node, P param) throws Exception;
}
