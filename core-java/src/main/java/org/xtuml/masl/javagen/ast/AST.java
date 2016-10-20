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
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.code.Switch;
import org.xtuml.masl.javagen.ast.code.SynchronizedBlock;
import org.xtuml.masl.javagen.ast.code.Throw;
import org.xtuml.masl.javagen.ast.code.Try;
import org.xtuml.masl.javagen.ast.code.TypeDeclarationStatement;
import org.xtuml.masl.javagen.ast.code.Variable;
import org.xtuml.masl.javagen.ast.code.While;
import org.xtuml.masl.javagen.ast.code.Switch.SwitchBlock;
import org.xtuml.masl.javagen.ast.def.Comment;
import org.xtuml.masl.javagen.ast.def.CompilationUnit;
import org.xtuml.masl.javagen.ast.def.Constructor;
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.Import;
import org.xtuml.masl.javagen.ast.def.InitializerBlock;
import org.xtuml.masl.javagen.ast.def.Method;
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
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.FieldAccess;
import org.xtuml.masl.javagen.ast.expr.Literal;
import org.xtuml.masl.javagen.ast.expr.MethodInvocation;
import org.xtuml.masl.javagen.ast.expr.NewArray;
import org.xtuml.masl.javagen.ast.expr.NewInstance;
import org.xtuml.masl.javagen.ast.expr.ParenthesizedExpression;
import org.xtuml.masl.javagen.ast.expr.PostfixExpression;
import org.xtuml.masl.javagen.ast.expr.PrefixExpression;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.expr.UnaryExpression;
import org.xtuml.masl.javagen.ast.expr.VariableAccess;
import org.xtuml.masl.javagen.ast.types.ArrayType;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.PrimitiveType;
import org.xtuml.masl.javagen.ast.types.Type;
import org.xtuml.masl.javagen.ast.types.TypeVariable;
import org.xtuml.masl.javagen.ast.types.WildcardType;
import org.xtuml.masl.javagen.astimpl.EnumConstantAccessImpl;


public interface AST
    extends ASTNode
{

  void addPackage ( Package pkg );

  ArrayAccess createArrayAccess ( Expression arrayExpression, Expression indexExpression );

  ArrayInitializer createArrayInitializer ( Expression... elements );

  ArrayType createArrayType ( Type containedType );

  Assert createAssert ( Expression condition );

  Assignment createAssignment ( Expression target, Assignment.Operator operator, Expression source );

  Assignment createAssignment ( Expression target, Expression source );

  BinaryExpression createBinaryExpression ( Expression lhs, BinaryExpression.Operator operator, Expression rhs );

  PrimitiveType createBoolean ();

  Break createBreak ();

  PrimitiveType createByte ();

  Cast createCast ( Type type, Expression expression );

  Catch createCatch ( Parameter exceptionParameter );

  PrimitiveType createChar ();

  CodeBlock createCodeBlock ();

  Comment createComment ( String text );

  CompilationUnit createCompilationUnit ( String name );

  Conditional createConditional ( Expression condition, Expression trueValue, Expression falseValue );

  Constructor createConstructor ( Parameter... params );

  Continue createContinue ();

  DeclaredType createDeclaredType ( TypeDeclaration typeDeclaration, Type... args );

  PrimitiveType createDouble ();

  DoWhile createDoWhile ( Expression condition );

  EmptyStatement createEmptyStatement ();

  EnumConstant createEnumConstant ( String name, Expression... args );

  EnumConstantAccessImpl createEnumConstantAccess ( EnumConstant enumConstant );

  ExpressionStatement createExpressionStatement ( StatementExpression expression );

  Field createField ( Type type, String name );

  Field createField ( Type type, String name, Expression initialValue );

  FieldAccess createFieldAccess ( Expression instance, Field field );

  ArrayLengthAccess createArrayLengthAccess ( Expression instance );

  FieldAccess createFieldAccess ( Field field );

  PrimitiveType createFloat ();

  For createFor ( LocalVariable variable, Expression collection );

  For createFor ( LocalVariable variable, Expression condition, StatementExpression update );

  For createFor ( StatementExpression start, Expression condition, StatementExpression update );

  If createIf ( Expression condition );

  If createIfElseChain ( Expression condition, boolean finalElse, Expression... elseConditions );

  InitializerBlock createInitializer ( boolean isStatic );

  PrimitiveType createInt ();

  LabeledStatement createLabeledStatement ( String name, Statement statement );

  Literal.BooleanLiteral createLiteral ( boolean value );

  Literal.CharacterLiteral createLiteral ( char value );

  Literal.DoubleLiteral createLiteral ( double value );

  Literal.FloatLiteral createLiteral ( float value );

  Literal.IntegerLiteral createLiteral ( int value );

  Literal.LongLiteral createLiteral ( long value );

  Literal.StringLiteral createLiteral ( String value );

  LocalVariable createLocalVariable ( Type type, String name );

  LocalVariable createLocalVariable ( Type type, String name, Expression initialValue );

  PrimitiveType createLong ();

  Method createMethod ( String name, Parameter... params );

  Method createMethod ( String name, Type returnType, Parameter... params );

  MethodInvocation createMethodInvocation ( Expression instance, Method method, Expression... args );

  MethodInvocation createMethodInvocation ( Method method, Expression... args );

  NewArray createNewArray ( Type type, int noDimensions, ArrayInitializer initialValue );

  NewArray createNewArray ( Type type, int noDimensions, Expression... dimensionSizes );

  NewInstance createNewInstance ( DeclaredType instanceType, Expression... args );

  Literal.NullLiteral createNullLiteral ();

  Package createPackage ( String name );

  Parameter createParameter ( Type exceptionType, String paramName );

  ParenthesizedExpression createParenthesizedExpression ( Expression expression );

  PostfixExpression createPostDecrement ( Expression expression );

  PostfixExpression createPostIncrement ( Expression expression );

  PrefixExpression createPreDecrement ( Expression expression );

  PrefixExpression createPreIncrement ( Expression expression );

  PrimitiveType createPrimitiveType ( PrimitiveType.Tag tag );

  Return createReturn ();

  Return createReturn ( Expression returnValue );

  PrimitiveType createShort ();

  Import createSingleStaticImport ( TypeDeclaration typeDeclaration, String name );

  Import createSingleTypeImport ( TypeDeclaration typeDeclaration );

  Import createStaticImportOnDemand ( TypeDeclaration typeDeclaration );

  Switch createSwitch ( Expression discriminator );

  SwitchBlock createSwitchBlock ();

  SynchronizedBlock createSynchronizedBlock ( Expression lockExpression );

  This createThis ();

  This createThis ( TypeBody parentType );

  This createThis ( TypeDeclaration parentType );

  Throw createThrow ( Expression thrownExpression );

  Try createTry ();

  Type createType ( java.lang.reflect.Type type );

  DeclaredType createType ( TypeDeclaration typeDeclaration );

  TypeBody createTypeBody ();

  TypeDeclaration createTypeDeclaration ( String name );

  TypeDeclarationStatement createTypeDeclarationStatement ( TypeDeclaration declaration );

  Import createTypeImportOnDemand ( Package parentPackage );

  Import createTypeImportOnDemand ( TypeDeclaration typeDeclaration );

  TypeParameter createTypeParameter ( String name );

  TypeVariable createTypeVariable ( TypeParameter parameter );

  UnaryExpression createUnaryExpression ( UnaryExpression.Operator operator, Expression expression );

  VariableAccess createVariableAccess ( Variable p2 );

  LocalVariableDeclaration createVariableDeclaration ( LocalVariable declaration );

  PrimitiveType createVoid ();

  While createWhile ( Expression condition );

  WildcardType createWildcardType ();

  CompilationUnit getCompilationUnit ( Class<?> clazz );


  Constructor getConstructor ( java.lang.Class<?> clazz, Class<?>... paramTypes );

  Method getMethod ( java.lang.Class<?> clazz, String name, Class<?>... paramTypes );

  Field getField ( java.lang.Class<?> clazz, String name );

  Constructor getConstructor ( java.lang.reflect.Constructor<?> constructor );

  EnumConstant getEnumConstant ( Enum<?> enumConstant );

  Method getMethod ( java.lang.reflect.Method method );

  Field getField ( java.lang.reflect.Field field );

  Package getPackage ( java.lang.Package pkg );

  Package getPackage ( java.lang.Package javaPkg, boolean forExtension );

  TypeDeclaration getTypeDeclaration ( Class<?> clazz );

  ClassLiteral createClassLiteral ( Type type );

  ConstructorInvocation createSuperInvocation ( Expression... args );

  ConstructorInvocation createThisInvocation ( Expression... args );
}
