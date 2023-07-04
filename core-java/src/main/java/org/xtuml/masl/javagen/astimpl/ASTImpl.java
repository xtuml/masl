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
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.AST;
import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.ConstructorInvocation;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.code.Variable;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.*;
import org.xtuml.masl.javagen.ast.expr.*;
import org.xtuml.masl.javagen.ast.expr.Assignment.Operator;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.PrimitiveType;
import org.xtuml.masl.javagen.ast.types.PrimitiveType.Tag;
import org.xtuml.masl.javagen.ast.types.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTImpl extends ASTNodeImpl implements AST {

    public ASTImpl() {
        super(null);
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitAST(this, p);
    }

    @Override
    public void addPackage(final Package pkg) {
        packages.add((PackageImpl) pkg);
    }

    @Override
    public ArrayAccessImpl createArrayAccess(final Expression arrayExpression, final Expression indexExpression) {
        return new ArrayAccessImpl(this, arrayExpression, indexExpression);
    }

    @Override
    public ArrayInitializerImpl createArrayInitializer(final Expression... elements) {
        return new ArrayInitializerImpl(this, elements);
    }

    @Override
    public ArrayLengthAccessImpl createArrayLengthAccess(final Expression instance) {
        return new ArrayLengthAccessImpl(this, instance);
    }

    @Override
    public ArrayTypeImpl createArrayType(final Type containedType) {
        return new ArrayTypeImpl(this, containedType);
    }

    @Override
    public AssertImpl createAssert(final Expression condition) {
        return new AssertImpl(this, (ExpressionImpl) condition);
    }

    @Override
    public AssignmentImpl createAssignment(final Expression target, final Expression source) {
        return createAssignment(target, Assignment.Operator.ASSIGN, source);
    }

    @Override
    public AssignmentImpl createAssignment(final Expression target, final Operator operator, final Expression source) {
        return new AssignmentImpl(this, target, operator, source);
    }

    @Override
    public BinaryExpressionImpl createBinaryExpression(final Expression lhs,
                                                       final org.xtuml.masl.javagen.ast.expr.BinaryExpression.Operator operator,
                                                       final Expression rhs) {
        return new BinaryExpressionImpl(this, lhs, operator, rhs);
    }

    @Override
    public PrimitiveTypeImpl createBoolean() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.BOOLEAN);
    }

    @Override
    public BreakImpl createBreak() {
        return new BreakImpl(this);
    }

    @Override
    public PrimitiveTypeImpl createByte() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.BYTE);
    }

    @Override
    public CastImpl createCast(final Type type, final Expression expression) {
        return new CastImpl(this, type, expression);
    }

    @Override
    public CatchImpl createCatch(final Parameter exceptionParameter) {
        return new CatchImpl(this, exceptionParameter);
    }

    @Override
    public PrimitiveTypeImpl createChar() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.CHAR);
    }

    @Override
    public ClassLiteralImpl createClassLiteral(final Type type) {
        return new ClassLiteralImpl(this, type);
    }

    @Override
    public CodeBlockImpl createCodeBlock() {
        return new CodeBlockImpl(this);
    }

    @Override
    public CommentImpl createComment(final String text) {
        return new CommentImpl(this, text);
    }

    @Override
    public CompilationUnitImpl createCompilationUnit(final String name) {
        return new CompilationUnitImpl(this, name);
    }

    @Override
    public ConditionalImpl createConditional(final Expression condition,
                                             final Expression trueValue,
                                             final Expression falseValue) {
        return new ConditionalImpl(this, condition, trueValue, falseValue);
    }

    @Override
    public MethodImpl createConstructor(final Parameter... params) {
        return new MethodImpl(this, params);
    }

    @Override
    public ContinueImpl createContinue() {
        return new ContinueImpl(this);
    }

    @Override
    public DeclaredTypeImpl createDeclaredType(final TypeDeclaration typeDeclaration, final Type... args) {
        return new DeclaredTypeImpl(this, (TypeDeclarationImpl) typeDeclaration, args);
    }

    @Override
    public PrimitiveTypeImpl createDouble() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.DOUBLE);
    }

    @Override
    public DoWhileImpl createDoWhile(final Expression condition) {
        return new DoWhileImpl(this, (ExpressionImpl) condition);
    }

    @Override
    public EmptyStatementImpl createEmptyStatement() {
        return new EmptyStatementImpl(this);
    }

    @Override
    public EnumConstantImpl createEnumConstant(final String name, final Expression... args) {
        return new EnumConstantImpl(this, name, args);
    }

    @Override
    public EnumConstantAccessImpl createEnumConstantAccess(final EnumConstant enumConstant) {
        return new EnumConstantAccessImpl(this, enumConstant);
    }

    @Override
    public ExpressionStatementImpl createExpressionStatement(final StatementExpression expression) {
        return new ExpressionStatementImpl(this, expression);
    }

    @Override
    public FieldImpl createField(final Type type, final String name) {
        return new FieldImpl(this, type, name);
    }

    @Override
    public FieldImpl createField(final Type type, final String name, final Expression initialValue) {
        return new FieldImpl(this, type, name, initialValue);
    }

    @Override
    public FieldAccessImpl createFieldAccess(final Expression instance, final Field field) {
        return new FieldAccessImpl(this, instance, field);
    }

    @Override
    public FieldAccessImpl createFieldAccess(final Field field) {
        return new FieldAccessImpl(this, field);
    }

    @Override
    public PrimitiveTypeImpl createFloat() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.FLOAT);
    }

    @Override
    public ForImpl createFor(final LocalVariable variable, final Expression collection) {
        return new ForImpl(this, variable, collection);
    }

    @Override
    public ForImpl createFor(final LocalVariable variable,
                             final Expression condition,
                             final StatementExpression update) {
        return new ForImpl(this, variable, condition, update);
    }

    @Override
    public ForImpl createFor(final StatementExpression start,
                             final Expression condition,
                             final StatementExpression update) {
        return new ForImpl(this, start, condition, update);
    }

    @Override
    public IfImpl createIf(final Expression condition) {
        return new IfImpl(this, (ExpressionImpl) condition);
    }

    @Override
    public IfImpl createIfElseChain(final Expression condition,
                                    final boolean finalElse,
                                    final Expression... elseConditions) {
        final IfImpl result = new IfImpl(this, (ExpressionImpl) condition);
        IfImpl curIf = result;
        for (final Expression elseCond : elseConditions) {
            curIf.setElse(createIf(elseCond));
            curIf = (IfImpl) curIf.getElse();
        }
        if (finalElse) {
            curIf.setElse(createCodeBlock());
        }
        return result;
    }

    @Override
    public InitializerBlockImpl createInitializer(final boolean isStatic) {
        return new InitializerBlockImpl(this, isStatic);
    }

    @Override
    public PrimitiveTypeImpl createInt() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.INT);
    }

    @Override
    public LabeledStatementImpl createLabeledStatement(final String name, final Statement statement) {
        return new LabeledStatementImpl(this, name, (StatementImpl) statement);
    }

    @Override
    public LiteralImpl.BooleanLiteralImpl createLiteral(final boolean value) {
        return new LiteralImpl.BooleanLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.CharacterLiteralImpl createLiteral(final char value) {
        return new LiteralImpl.CharacterLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.DoubleLiteralImpl createLiteral(final double value) {
        return new LiteralImpl.DoubleLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.FloatLiteralImpl createLiteral(final float value) {
        return new LiteralImpl.FloatLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.IntegerLiteralImpl createLiteral(final int value) {
        return new LiteralImpl.IntegerLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.LongLiteralImpl createLiteral(final long value) {
        return new LiteralImpl.LongLiteralImpl(this, value);
    }

    @Override
    public LiteralImpl.StringLiteralImpl createLiteral(final String value) {
        return new LiteralImpl.StringLiteralImpl(this, value);
    }

    @Override
    public LocalVariableImpl createLocalVariable(final Type type, final String name) {
        return new LocalVariableImpl(this, type, name);
    }

    @Override
    public LocalVariableImpl createLocalVariable(final Type type, final String name, final Expression initialValue) {
        return new LocalVariableImpl(this, type, name, initialValue);
    }

    @Override
    public PrimitiveTypeImpl createLong() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.LONG);
    }

    @Override
    public MethodInvocationImpl createMethodInvocation(final Expression instance,
                                                       final Method method,
                                                       final Expression... args) {
        return new MethodInvocationImpl(this, instance, method, args);
    }

    @Override
    public MethodInvocationImpl createMethodInvocation(final Method method, final Expression... args) {
        return new MethodInvocationImpl(this, method, args);
    }

    @Override
    public NewArrayImpl createNewArray(final Type type, final int noDimensions, final ArrayInitializer initialValue) {
        return new NewArrayImpl(this, type, noDimensions, initialValue);
    }

    @Override
    public NewArrayImpl createNewArray(final Type type, final int noDimensions, final Expression... dimensionSizes) {
        return new NewArrayImpl(this, type, noDimensions, dimensionSizes);
    }

    @Override
    public NewInstanceImpl createNewInstance(final DeclaredType instanceType, final Expression... args) {
        return new NewInstanceImpl(this, instanceType, args);
    }

    @Override
    public LiteralImpl.NullLiteralImpl createNullLiteral() {
        return new LiteralImpl.NullLiteralImpl(this);
    }

    @Override
    public PackageImpl createPackage(final String name) {
        if (java.lang.Package.getPackage(name) == null) {
            final PackageImpl result = new PackageImpl(this, name);
            addPackage(result);
            return result;
        } else {
            return getPackage(java.lang.Package.getPackage(name), true);
        }
    }

    @Override
    public ParameterImpl createParameter(final Type type, final String name) {
        return new ParameterImpl(this, type, name);
    }

    @Override
    public ParenthesizedExpressionImpl createParenthesizedExpression(final Expression expression) {
        return new ParenthesizedExpressionImpl(this, expression);
    }

    @Override
    public PostfixExpressionImpl createPostDecrement(final Expression expression) {
        return new PostfixExpressionImpl(this, expression, PostfixExpressionImpl.Operator.DECREMENT);
    }

    @Override
    public PostfixExpressionImpl createPostIncrement(final Expression expression) {
        return new PostfixExpressionImpl(this, expression, PostfixExpressionImpl.Operator.INCREMENT);
    }

    @Override
    public PrefixExpressionImpl createPreDecrement(final Expression expression) {
        return new PrefixExpressionImpl(this, PrefixExpressionImpl.Operator.DECREMENT, expression);
    }

    @Override
    public PrefixExpressionImpl createPreIncrement(final Expression expression) {
        return new PrefixExpressionImpl(this, PrefixExpressionImpl.Operator.INCREMENT, expression);
    }

    @Override
    public PrimitiveTypeImpl createPrimitiveType(final PrimitiveType.Tag tag) {
        return new PrimitiveTypeImpl(this, tag);
    }

    @Override
    public ReturnImpl createReturn() {
        return new ReturnImpl(this);
    }

    @Override
    public ReturnImpl createReturn(final Expression returnValue) {
        return new ReturnImpl(this, (ExpressionImpl) returnValue);
    }

    @Override
    public PrimitiveTypeImpl createShort() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.SHORT);
    }

    @Override
    public ImportImpl createSingleStaticImport(final TypeDeclaration typeDeclaration, final String name) {
        return new ImportImpl(this, typeDeclaration, name, ImportImpl.Scope.StaticImport, ImportImpl.Mode.SingleImport);
    }

    @Override
    public ImportImpl createSingleTypeImport(final TypeDeclaration typeDeclaration) {
        return new ImportImpl(this, typeDeclaration, null, ImportImpl.Scope.TypeImport, ImportImpl.Mode.SingleImport);
    }

    @Override
    public ImportImpl createStaticImportOnDemand(final TypeDeclaration typeDeclaration) {
        return new ImportImpl(this,
                              typeDeclaration,
                              null,
                              ImportImpl.Scope.StaticImport,
                              ImportImpl.Mode.OnDemandImport);
    }

    @Override
    public SwitchImpl createSwitch(final Expression discriminator) {
        return new SwitchImpl(this, (ExpressionImpl) discriminator);
    }

    @Override
    public SwitchBlockImpl createSwitchBlock() {
        return new SwitchBlockImpl(this);
    }

    @Override
    public SynchronizedBlockImpl createSynchronizedBlock(final Expression lockExpression) {
        return new SynchronizedBlockImpl(this, lockExpression);
    }

    @Override
    public ThisImpl createThis() {
        return new ThisImpl(this);
    }

    @Override
    public ThisImpl createThis(final TypeBody parentType) {
        return new ThisImpl(this, parentType);
    }

    @Override
    public ThisImpl createThis(final TypeDeclaration parentType) {
        return new ThisImpl(this, parentType.getTypeBody());
    }

    @Override
    public ThrowImpl createThrow(final Expression thrownExpression) {
        return new ThrowImpl(this, (ExpressionImpl) thrownExpression);
    }

    @Override
    public TryImpl createTry() {
        return new TryImpl(this);
    }

    @Override
    public TypeImpl createType(final java.lang.reflect.Type type) {
        if (type instanceof java.lang.reflect.ParameterizedType pt) {
            final DeclaredTypeImpl
                    result =
                    createDeclaredType(getTypeDeclaration((java.lang.Class<?>) pt.getRawType()));
            for (final java.lang.reflect.Type arg : pt.getActualTypeArguments()) {
                result.addTypeArgument(createType(arg));
            }
            return result;
        } else if (type instanceof java.lang.Class<?>) {
            if (((java.lang.Class<?>) type).isArray()) {
                return createArrayType(createType(((java.lang.Class<?>) type).getComponentType()));
            } else if (((java.lang.Class<?>) type).isPrimitive()) {
                return createPrimitiveType(primitiveTypeLookup.get(type));
            } else {
                return createDeclaredType(getTypeDeclaration((java.lang.Class<?>) type));
            }
        } else if (type instanceof java.lang.reflect.WildcardType wct) {
            final WildcardTypeImpl result = createWildcardType();
            if (wct.getLowerBounds().length > 0) {
                result.setSuperBound((ReferenceTypeImpl) createType(wct.getLowerBounds()[0]));
            }
            if (wct.getUpperBounds().length > 0) {
                result.setExtendsBound((ReferenceTypeImpl) createType(wct.getUpperBounds()[0]));
            }
            return result;
        } else if (type instanceof java.lang.reflect.TypeVariable<?> tv) {

            final Object obj = tv.getGenericDeclaration();
            if (obj instanceof java.lang.reflect.Method) {
                final MirroredMethodImpl parent = getMethod(((java.lang.reflect.Method) obj));
                return createTypeVariable(parent.getTypeParameter(tv));
            } else if (obj instanceof java.lang.reflect.Constructor<?>) {
                final MirroredMethodImpl parent = getConstructor(((java.lang.reflect.Constructor<?>) obj));
                return createTypeVariable(parent.getTypeParameter(tv));
            } else {
                final MirroredTypeDeclarationImpl parent = getTypeDeclaration((java.lang.Class<?>) obj);
                return createTypeVariable(parent.getTypeParameter(tv));
            }
        } else if (type instanceof java.lang.reflect.GenericArrayType) {
            return createArrayType(createType(((java.lang.reflect.GenericArrayType) type).getGenericComponentType()));
        } else {
            return null;
        }
    }

    @Override
    public DeclaredTypeImpl createType(final TypeDeclaration typeDeclaration) {
        return new DeclaredTypeImpl(this, (TypeDeclarationImpl) typeDeclaration);
    }

    @Override
    public TypeBodyImpl createTypeBody() {
        return new TypeBodyImpl(this);
    }

    @Override
    public TypeDeclarationImpl createTypeDeclaration(final String name) {
        return new TypeDeclarationImpl(this, name);
    }

    @Override
    public TypeDeclarationStatementImpl createTypeDeclarationStatement(final TypeDeclaration declaration) {
        return new TypeDeclarationStatementImpl(this, (TypeDeclarationImpl) declaration);
    }

    @Override
    public ImportImpl createTypeImportOnDemand(final Package parentPackage) {
        return new ImportImpl(this, parentPackage);
    }

    @Override
    public ImportImpl createTypeImportOnDemand(final TypeDeclaration typeDeclaration) {
        return new ImportImpl(this, typeDeclaration, null, ImportImpl.Scope.TypeImport, ImportImpl.Mode.OnDemandImport);
    }

    @Override
    public TypeParameterImpl createTypeParameter(final String name) {
        return new TypeParameterImpl(this, name);
    }

    @Override
    public TypeVariableImpl createTypeVariable(final TypeParameter parameter) {
        return new TypeVariableImpl(this, parameter);
    }

    @Override
    public UnaryExpressionImpl createUnaryExpression(final UnaryExpression.Operator operator,
                                                     final Expression expression) {
        return new UnaryExpressionImpl(this, operator, expression);
    }

    @Override
    public VariableAccessImpl createVariableAccess(final Variable p2) {
        return new VariableAccessImpl(this, p2);
    }

    @Override
    public VariableDeclarationStatementImpl createVariableDeclaration(final LocalVariable declaration) {
        return new VariableDeclarationStatementImpl(this, (LocalVariableImpl) declaration);
    }

    @Override
    public PrimitiveTypeImpl createVoid() {
        return createPrimitiveType(PrimitiveTypeImpl.Tag.VOID);
    }

    @Override
    public WhileImpl createWhile(final Expression condition) {
        return new WhileImpl(this, (ExpressionImpl) condition);
    }

    @Override
    public WildcardTypeImpl createWildcardType() {
        return new WildcardTypeImpl(this);
    }

    @Override
    public ASTImpl getAST() {
        return this;
    }

    @Override
    public MirroredCompilationUnitImpl getCompilationUnit(final java.lang.Class<?> clazz) {
        return getPackage(clazz.getPackage()).getCompilationUnit(clazz);
    }

    @Override
    public Constructor getConstructor(final Class<?> clazz, final Class<?>... paramTypes) {
        try {
            return getConstructor(clazz.getConstructor(paramTypes));
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MirroredMethodImpl getConstructor(final java.lang.reflect.Constructor<?> constructor) {
        return getTypeDeclaration(constructor.getDeclaringClass()).getTypeBody().getConstructorDeclaration(constructor);
    }

    @Override
    public Field getField(final Class<?> clazz, final String name) {
        try {
            return getField(clazz.getDeclaredField(name));
        } catch (final NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MirroredFieldImpl getField(final java.lang.reflect.Field field) {
        return getTypeDeclaration(field.getDeclaringClass()).getTypeBody().getFieldDeclaration(field);
    }

    @Override
    public Method getMethod(final Class<?> clazz, final String name, final Class<?>... paramTypes) {
        try {
            return getMethod(clazz.getDeclaredMethod(name, paramTypes));
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MirroredMethodImpl getMethod(final java.lang.reflect.Method method) {
        return getTypeDeclaration(method.getDeclaringClass()).getTypeBody().getMethodDeclaration(method);
    }

    @Override
    public MirroredPackageImpl getPackage(final java.lang.Package javaPkg) {
        return getPackage(javaPkg, false);
    }

    @Override
    public MirroredPackageImpl getPackage(final java.lang.Package javaPkg, final boolean isExtensible) {
        if (packageLookup.containsKey(javaPkg)) {
            return packageLookup.get(javaPkg);
        } else {
            final MirroredPackageImpl pkg = new MirroredPackageImpl(this, javaPkg, isExtensible);
            addPackage(pkg);
            packageLookup.put(javaPkg, pkg);
            return pkg;
        }
    }

    @Override
    public MirroredTypeDeclarationImpl getTypeDeclaration(final java.lang.Class<?> clazz) {
        if (clazz.getEnclosingClass() == null) {
            return getCompilationUnit(clazz).getTypeDeclaration();
        } else {
            return getTypeDeclaration(clazz.getEnclosingClass()).getTypeBody().getTypeDeclaration(clazz);
        }

    }

    final private List<PackageImpl> packages = new ChildNodeList<PackageImpl>(this);

    final private Map<java.lang.Package, MirroredPackageImpl>
            packageLookup =
            new HashMap<java.lang.Package, MirroredPackageImpl>();

    final private static Map<java.lang.Class<?>, Tag> primitiveTypeLookup = new HashMap<java.lang.Class<?>, Tag>();

    static {
        primitiveTypeLookup.put(Boolean.TYPE, Tag.BOOLEAN);
        primitiveTypeLookup.put(Byte.TYPE, Tag.BYTE);
        primitiveTypeLookup.put(Character.TYPE, Tag.CHAR);
        primitiveTypeLookup.put(Short.TYPE, Tag.SHORT);
        primitiveTypeLookup.put(Integer.TYPE, Tag.INT);
        primitiveTypeLookup.put(Long.TYPE, Tag.LONG);
        primitiveTypeLookup.put(Float.TYPE, Tag.FLOAT);
        primitiveTypeLookup.put(Double.TYPE, Tag.DOUBLE);
        primitiveTypeLookup.put(Void.TYPE, Tag.VOID);
    }

    @Override
    public MethodImpl createMethod(final String name, final Parameter... params) {
        return new MethodImpl(this, name, params);
    }

    @Override
    public MethodImpl createMethod(final String name, final Type returnType, final Parameter... params) {
        return new MethodImpl(this, name, returnType, params);
    }

    @Override
    public EnumConstant getEnumConstant(final Enum<?> enumConstant) {
        return getTypeDeclaration(enumConstant.getClass()).getEnumConstant(enumConstant);
    }

    @Override
    public ConstructorInvocation createSuperInvocation(final Expression... args) {
        return new ConstructorInvocationImpl(this, true, args);
    }

    @Override
    public ConstructorInvocation createThisInvocation(final Expression... args) {
        return new ConstructorInvocationImpl(this, false, args);
    }

}
