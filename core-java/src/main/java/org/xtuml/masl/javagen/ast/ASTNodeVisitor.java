//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast;

import org.xtuml.masl.javagen.ast.code.Assert;
import org.xtuml.masl.javagen.ast.code.Break;
import org.xtuml.masl.javagen.ast.code.Catch;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.ConstructorInvocation;
import org.xtuml.masl.javagen.ast.code.Continue;
import org.xtuml.masl.javagen.ast.code.DoWhile;
import org.xtuml.masl.javagen.ast.code.EmptyStatement;
import org.xtuml.masl.javagen.ast.code.ExpressionStatement;
import org.xtuml.masl.javagen.ast.code.For;
import org.xtuml.masl.javagen.ast.code.If;
import org.xtuml.masl.javagen.ast.code.LabeledStatement;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.LocalVariableDeclaration;
import org.xtuml.masl.javagen.ast.code.Return;
import org.xtuml.masl.javagen.ast.code.Switch;
import org.xtuml.masl.javagen.ast.code.SynchronizedBlock;
import org.xtuml.masl.javagen.ast.code.Throw;
import org.xtuml.masl.javagen.ast.code.Try;
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.code.While;
import org.xtuml.masl.javagen.ast.def.Comment;
import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.javagen.ast.def.Constructor;
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Import;
import org.xtuml.masl.javagen.ast.def.InitializerBlock;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.def.Modifiers;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.Parameter;
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.def.TypeParameter;
import org.xtuml.masl.javagen.ast.expr.ArrayAccess;
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.ArrayLengthAccess;
import org.xtuml.masl.javagen.ast.expr.Assignment;
import org.xtuml.masl.javagen.ast.expr.BinaryExpression;
import org.xtuml.masl.javagen.ast.expr.Cast;
import org.xtuml.masl.javagen.ast.expr.ClassLiteral;
import org.xtuml.masl.javagen.ast.expr.Conditional;
import org.xtuml.masl.javagen.ast.expr.EnumConstantAccess;
import org.xtuml.masl.javagen.ast.expr.FieldAccess;
import org.xtuml.masl.javagen.ast.expr.Literal;
import org.xtuml.masl.javagen.ast.expr.MethodInvocation;
import org.xtuml.masl.javagen.ast.expr.NewArray;
import org.xtuml.masl.javagen.ast.expr.NewInstance;
import org.xtuml.masl.javagen.ast.expr.PackageQualifier;
import org.xtuml.masl.javagen.ast.expr.ParenthesizedExpression;
import org.xtuml.masl.javagen.ast.expr.PostfixExpression;
import org.xtuml.masl.javagen.ast.expr.PrefixExpression;
import org.xtuml.masl.javagen.ast.expr.SuperQualifier;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;
import org.xtuml.masl.javagen.ast.expr.UnaryExpression;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;
import org.xtuml.masl.javagen.ast.types.ArrayType;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.PrimitiveType;
import org.xtuml.masl.javagen.ast.types.TypeVariable;
import org.xtuml.masl.javagen.ast.types.WildcardType;


public interface ASTNodeVisitor<R, P>
{

  R visitNull ( P param ) throws Exception;

  R visit ( ASTNode node, P param ) throws Exception;

  R visitArrayAccess ( ArrayAccess node, P param ) throws Exception;

  R visitImport ( Import node, P param ) throws Exception;

  R visitClassLiteral ( ClassLiteral node, P param ) throws Exception;

  R visitNewArray ( NewArray node, P param ) throws Exception;

  R visitArrayInitializer ( ArrayInitializer node, P param ) throws Exception;

  R visitArrayType ( ArrayType node, P param ) throws Exception;

  R visitAssert ( Assert node, P param ) throws Exception;

  R visitAssignmen ( Assignment node, P param ) throws Exception;

  R visitAST ( AST node, P param ) throws Exception;

  R visitBinaryExpression ( BinaryExpression node, P param ) throws Exception;

  R visitBooleanLiteral ( Literal.BooleanLiteral node, P param ) throws Exception;

  R visitBreak ( Break node, P param ) throws Exception;

  R visitCast ( Cast node, P param ) throws Exception;

  R visitCatch ( Catch node, P param ) throws Exception;

  R visitCharacterLiteral ( Literal.CharacterLiteral node, P param ) throws Exception;

  R visitCodeBlock ( CodeBlock node, P param ) throws Exception;

  R visitComment ( Comment node, P param ) throws Exception;

  R visitCompilationUnit ( CompilationUnit node, P param ) throws Exception;

  R visitConditional ( Conditional node, P param ) throws Exception;

  R visitConstructor ( Constructor node, P param ) throws Exception;

  R visitContinue ( Continue node, P param ) throws Exception;

  R visitDeclaredType ( DeclaredType node, P param ) throws Exception;

  R visitDoubleLiteral ( Literal.DoubleLiteral node, P param ) throws Exception;

  R visitDoWhile ( DoWhile node, P param ) throws Exception;

  R visitEmptyStatement ( EmptyStatement node, P param ) throws Exception;

  R visitEnumConstant ( EnumConstant node, P param ) throws Exception;

  R visitEnumConstantAccess ( EnumConstantAccess node, P param ) throws Exception;

  R visitExpressionStatement ( ExpressionStatement node, P param ) throws Exception;

  R visitFieldAccess ( FieldAccess node, P param ) throws Exception;

  R visitField ( Field node, P param ) throws Exception;

  R visitFloatLiteral ( Literal.FloatLiteral node, P param ) throws Exception;

  R visitFor ( For node, P param ) throws Exception;

  R visitIf ( If node, P param ) throws Exception;

  R visitInitializerBlock ( InitializerBlock node, P param ) throws Exception;

  R visitNewInstance ( NewInstance node, P param ) throws Exception;

  R visitIntegerLiteral ( Literal.IntegerLiteral node, P param ) throws Exception;

  R visitLabeledStatement ( LabeledStatement node, P param ) throws Exception;

  R visitLocalVariable ( LocalVariable node, P param ) throws Exception;

  R visitLongLiteral ( Literal.LongLiteral node, P param ) throws Exception;

  R visitMethod ( Method node, P param ) throws Exception;

  R visitMethodInvocation ( MethodInvocation node, P param ) throws Exception;

  R visitModifiers ( Modifiers node, P param ) throws Exception;

  R visitNullLiteral ( Literal.NullLiteral node, P param ) throws Exception;

  R visitPackage ( Package node, P param ) throws Exception;

  R visitPackageQualifier ( PackageQualifier node, P param ) throws Exception;

  R visitParameter ( Parameter node, P param ) throws Exception;

  R visitParenthesizedExpression ( ParenthesizedExpression node, P param ) throws Exception;

  R visitPostfixExpression ( PostfixExpression node, P param ) throws Exception;

  R visitPrefixExpression ( PrefixExpression node, P param ) throws Exception;

  R visitPrimitiveType ( PrimitiveType node, P param ) throws Exception;

  R visitReturn ( Return node, P param ) throws Exception;

  R visitStringLiteral ( Literal.StringLiteral node, P param ) throws Exception;

  R visitSuperQualifier ( SuperQualifier node, P param ) throws Exception;

  R visitSwitchBlock ( Switch.SwitchBlock node, P param ) throws Exception;

  R visitSwitch ( Switch node, P param ) throws Exception;

  R visitSynchronizedBlock ( SynchronizedBlock node, P param ) throws Exception;

  R visitThis ( This node, P param ) throws Exception;

  R visitThrow ( Throw node, P param ) throws Exception;

  R visitTry ( Try node, P param ) throws Exception;

  R visitTypeBody ( TypeBody node, P param ) throws Exception;

  R visitTypeDeclaration ( TypeDeclaration node, P param ) throws Exception;

  R visitTypeDeclarationStatement ( TypeDeclarationStatement node, P param ) throws Exception;

  R visitTypeParameter ( TypeParameter node, P param ) throws Exception;

  R visitTypeQualifier ( TypeQualifier node, P param ) throws Exception;

  R visitTypeVariable ( TypeVariable node, P param ) throws Exception;

  R visitUnaryExpression ( UnaryExpression node, P param ) throws Exception;

  R visitvariableAccess ( VariableAccess node, P param ) throws Exception;

  R visitLocalVariableDeclaration ( LocalVariableDeclaration node, P param ) throws Exception;

  R visitWhile ( While node, P param ) throws Exception;

  R visitWildcardType ( WildcardType node, P param ) throws Exception;

  R visitArrayLengthAccess ( ArrayLengthAccess node, P param ) throws Exception;

  R visitConstructorInvocation ( ConstructorInvocation node, P param ) throws Exception;
}
