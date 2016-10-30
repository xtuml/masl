//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Comment;
import org.xtuml.masl.javagen.ast.def.Constructor;
import org.xtuml.masl.javagen.ast.def.EnumConstant;
import org.xtuml.masl.javagen.ast.def.Field;
import org.xtuml.masl.javagen.ast.def.InitializerBlock;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.def.Modifier;
import org.xtuml.masl.javagen.ast.def.Parameter;
import org.xtuml.masl.javagen.ast.def.Property;
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.def.TypeMemberGroup;
import org.xtuml.masl.javagen.ast.def.Visibility;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.This;
import org.xtuml.masl.javagen.ast.types.Type;
import org.xtuml.masl.utils.TextUtils;


class TypeBodyImpl extends ASTNodeImpl
    implements TypeBody, Scoped
{

  protected class MemberGroupImpl
      implements TypeMemberGroup, TypeMemberGroupMember
  {

    @Override
    public CommentImpl addComment ( final Comment comment )
    {
      return (CommentImpl)addMember((CommentImpl)comment);
    }

    @Override
    public CommentImpl addComment ( final String text )
    {
      return addComment(getAST().createComment(text));
    }

    @Override
    public MethodImpl addConstructor ( final Parameter... params )
    {
      return addConstructor(getAST().createConstructor(params));
    }

    @Override
    public MethodImpl addConstructor ( final Constructor constructor )
    {
      return (MethodImpl)addMember((MethodImpl)constructor);
    }

    @Override
    public FieldImpl addField ( final Field field )
    {
      final FieldImpl result = (FieldImpl)addMember((FieldImpl)field);
      fieldDeclarations.add(result);
      if ( getParentTypeDeclaration() != null && getParentTypeDeclaration().isInterface() )
      {
        result.getModifiers().setModifier(Modifier.STATIC);
        result.getModifiers().setModifier(Modifier.PUBLIC);
        result.getModifiers().setModifier(Modifier.FINAL);
      }

      return result;
    }

    @Override
    public FieldImpl addField ( final Type type, final String name )
    {
      return addField(getAST().createField(type, name));
    }

    @Override
    public FieldImpl addField ( final Type type, final String name, final Expression initialValue )
    {
      return addField(getAST().createField(type, name, initialValue));
    }

    @Override
    public MemberGroupImpl addGroup ()
    {
      return addGroup(new MemberGroupImpl());
    }

    @Override
    public InitializerBlockImpl addInitializerBlock ( final InitializerBlock declaration )
    {
      return (InitializerBlockImpl)addMember((InitializerBlockImpl)declaration);
    }

    @Override
    public InitializerBlockImpl addInitializerBlock ( final boolean isStatic )
    {
      return addInitializerBlock(getAST().createInitializer(isStatic));
    }

    @Override
    public MethodImpl overrideMethod ( final Method superMethod )
    {
      return addMethod(((MethodImpl)superMethod).copyForOverride());
    }

    @Override
    public MethodImpl addMethod ( final Method method )
    {
      final MethodImpl result = (MethodImpl)addMember((MethodImpl)method);
      methodDeclarations.add(result);
      return result;
    }

    @Override
    public MethodImpl addMethod ( final String name, final Type returnType, final Parameter... params )
    {
      return addMethod(getAST().createMethod(name, returnType, params));
    }

    @Override
    public MethodImpl addMethod ( final String name, final Parameter... params )
    {
      return addMethod(getAST().createMethod(name, params));
    }

    private void addPropertyGroups ()
    {
      if ( propertyGetters == null )
      {
        propertyGetters = addGroup();
        propertySetters = addGroup();
        propertyFields = addGroup();
      }
    }

    @Override
    public Property addProperty ( final Type type, final String name )
    {
      return addProperty(type, name, null);
    }

    @Override
    public Property addProperty ( final Type type, final String name, final Constructor initBy )
    {
      addPropertyGroups();
      final FieldImpl field = propertyFields.addField(type, name);
      field.setVisibility(Visibility.PRIVATE);

      final MethodImpl getter = propertyGetters.addMethod("get" + TextUtils.upperFirst(name), ((TypeImpl)type).deepCopy());
      getter.setCodeBlock().addStatement(getAST().createReturn(field.asExpression()));
      getter.setVisibility(Visibility.PUBLIC);

      final MethodImpl setter = propertySetters.addMethod("set" + TextUtils.upperFirst(name), getAST().createType(void.class));
      final ParameterImpl setterParam = setter.addParameter(((TypeImpl)type).deepCopy(), name);
      setter.setCodeBlock().addStatement(field.asExpression().assign(setterParam.asExpression()));
      setter.setVisibility(Visibility.PUBLIC);
      setterParam.setFinal();

      if ( initBy != null )
      {
        final ParameterImpl constructorParam = (ParameterImpl)initBy.addParameter(((TypeImpl)type).deepCopy(), name);
        constructorParam.setFinal();
        initBy.getCodeBlock().addStatement(field.asExpression().assign(constructorParam.asExpression()));
      }

      return new PropertyImpl(getter, setter, field);
    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration ( final TypeDeclaration typeDeclaration )
    {
      final TypeDeclarationImpl result = (TypeDeclarationImpl)addMember((TypeDeclarationImpl)typeDeclaration);
      typeDeclarations.add(result);
      return result;
    }

    @Override
    public TypeDeclarationImpl addTypeDeclaration ( final String name )
    {
      return addTypeDeclaration(getAST().createTypeDeclaration(name));
    }

    private TypeMemberImpl addMember ( final TypeMemberImpl member )
    {
      allMembers.add(member);
      groupMembers.add(member);
      return member;
    }

    private MemberGroupImpl addGroup ( final MemberGroupImpl group )
    {
      groupMembers.add(group);
      return group;
    }

    private List<TypeMemberImpl> getMembers ()
    {
      final List<TypeMemberImpl> result = new ArrayList<TypeMemberImpl>();
      for ( final TypeMemberGroupMember member : groupMembers )
      {
        if ( member instanceof MemberGroupImpl )
        {
          result.addAll(((MemberGroupImpl)member).getMembers());
        }
        else
        {
          result.add((TypeMemberImpl)member);
        }
      }
      return Collections.unmodifiableList(result);
    }

    private final List<TypeMemberGroupMember> groupMembers    = new ArrayList<TypeMemberGroupMember>();

    private MemberGroupImpl                   propertyGetters = null;
    private MemberGroupImpl                   propertySetters = null;
    private MemberGroupImpl                   propertyFields  = null;
  }

  private final MemberGroupImpl mainGroup = new MemberGroupImpl();


  @Override
  public CommentImpl addComment ( final Comment comment )
  {
    return mainGroup.addComment(comment);
  }

  @Override
  public CommentImpl addComment ( final String text )
  {
    return mainGroup.addComment(text);
  }

  @Override
  public MethodImpl addConstructor ( final Parameter... params )
  {
    return mainGroup.addConstructor(params);
  }

  @Override
  public MethodImpl addConstructor ( final Constructor constructor )
  {
    return mainGroup.addConstructor(constructor);
  }

  @Override
  public FieldImpl addField ( final Field field )
  {
    return mainGroup.addField(field);
  }

  @Override
  public FieldImpl addField ( final Type type, final String name )
  {
    return mainGroup.addField(type, name);
  }

  @Override
  public FieldImpl addField ( final Type type, final String name, final Expression initialValue )
  {
    return mainGroup.addField(type, name, initialValue);
  }

  @Override
  public MemberGroupImpl addGroup ()
  {
    return mainGroup.addGroup();
  }

  @Override
  public InitializerBlockImpl addInitializerBlock ( final boolean isStatic )
  {
    return mainGroup.addInitializerBlock(isStatic);
  }

  @Override
  public InitializerBlockImpl addInitializerBlock ( final InitializerBlock declaration )
  {
    return mainGroup.addInitializerBlock(declaration);
  }

  @Override
  public MethodImpl overrideMethod ( final Method superMethod )
  {
    return mainGroup.overrideMethod(superMethod);
  }

  @Override
  public MethodImpl addMethod ( final Method method )
  {
    return mainGroup.addMethod(method);
  }

  @Override
  public MethodImpl addMethod ( final String name, final Type returnType, final Parameter... params )
  {
    return mainGroup.addMethod(name, returnType, params);
  }

  @Override
  public MethodImpl addMethod ( final String name, final Parameter... params )
  {
    return mainGroup.addMethod(name, params);
  }


  @Override
  public Property addProperty ( final Type type, final String name )
  {
    return mainGroup.addProperty(type, name);
  }

  @Override
  public Property addProperty ( final Type type, final String name, final Constructor initBy )
  {
    return mainGroup.addProperty(type, name, initBy);
  }

  @Override
  public TypeDeclarationImpl addTypeDeclaration ( final TypeDeclaration typeDeclaration )
  {
    return mainGroup.addTypeDeclaration(typeDeclaration);
  }

  @Override
  public TypeDeclarationImpl addTypeDeclaration ( final String name )
  {
    return mainGroup.addTypeDeclaration(name);
  }

  private final class TBScope
      extends Scope
  {

    TBScope ()
    {
      super(TypeBodyImpl.this);
    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final TypeDeclarationImpl typeDeclaration,
                                          boolean visible,
                                          boolean shadowed )
    {
      if ( typeDeclaration.getParentTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }
      else if ( containsTypeNamed(typeDeclaration.getName()) )
      {
        shadowed = true;
      }

      return super.requiresQualifier(baseScope, typeDeclaration, visible, shadowed);
    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final FieldAccessImpl fieldAccess,
                                          boolean visible,
                                          boolean shadowed )
    {
      if ( fieldAccess.getField().getParentTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }
      else if ( containsFieldNamed(fieldAccess.getField().getName()) )
      {
        shadowed = true;
      }

      return super.requiresQualifier(baseScope, fieldAccess, visible, shadowed);
    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final EnumConstantAccessImpl enumAccess,
                                          boolean visible,
                                          boolean shadowed )
    {
      if ( enumAccess.getConstant().getParentTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }
      else if ( containsFieldNamed(enumAccess.getConstant().getName()) )
      {
        shadowed = true;
      }

      return super.requiresQualifier(baseScope, enumAccess, visible, shadowed);
    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final MethodInvocationImpl methodCall,
                                          boolean visible,
                                          final boolean shadowed )
    {
      if ( methodCall.getMethod().getParentTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }

      return super.requiresQualifier(baseScope, methodCall, visible, shadowed);
    }


    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final ThisImpl thisExpression,
                                          boolean visible,
                                          boolean shadowed )
    {
      if ( thisExpression.getTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }
      else
      {
        shadowed = true;
      }
      return super.requiresQualifier(baseScope, thisExpression, visible, shadowed);

    }

    @Override
    protected boolean requiresQualifier ( final Scope baseScope,
                                          final SuperQualifierImpl superQualifier,
                                          boolean visible,
                                          boolean shadowed )
    {
      if ( superQualifier.getTypeBody() == TypeBodyImpl.this )
      {
        visible = true;
      }
      else
      {
        shadowed = true;
      }
      return super.requiresQualifier(baseScope, superQualifier, visible, shadowed);

    }


  }

  TypeDeclarationImpl getParentTypeDeclaration ()
  {
    if ( getParentNode() instanceof TypeDeclarationImpl )
    {
      return (TypeDeclarationImpl)getParentNode();
    }
    else
    {
      return null;
    }
  }

  NewInstanceImpl getParentInstanceCreationExpression ()
  {
    if ( getParentNode() instanceof NewInstanceImpl )
    {
      return (NewInstanceImpl)getParentNode();
    }
    else
    {
      return null;
    }
  }

  TypeBodyImpl ( final ASTImpl ast )
  {
    super(ast);
    scope = new TBScope();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitTypeBody(this, p);
  }


  @Override
  public List<TypeMemberImpl> getMembers ()
  {
    return mainGroup.getMembers();
  }

  @Override
  public Scope getScope ()
  {
    return scope;
  }

  boolean containsNonPrivateTypeNamed ( final String name )
  {
    for ( final TypeDeclaration type : typeDeclarations )
    {
      if ( type.getName().equals(name) &&
           !type.getModifiers().isPrivate() )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsPublicTypeNamed ( final String name )
  {
    for ( final TypeDeclaration type : typeDeclarations )
    {
      if ( type.getName().equals(name) &&
           type.getModifiers().isPublic() )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsFieldNamed ( final String name )
  {
    for ( final Field field : fieldDeclarations )
    {
      if ( field.getName().equals(name) )
      {
        return true;
      }
    }
    for ( final EnumConstant enumConstant : enumConstants )
    {
      if ( enumConstant.getName().equals(name) )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsMethodNamed ( final String name )
  {
    for ( final Method method : methodDeclarations )
    {
      if ( method.getName().equals(name) )
      {
        return true;
      }
    }
    return false;
  }


  boolean containsStaticPublicFieldNamed ( final String name )
  {
    for ( final Field field : fieldDeclarations )
    {
      if ( field.getName().equals(name) &&
           field.getModifiers().isStatic() &&
           field.getModifiers().isPublic() )
      {
        return true;
      }
    }

    for ( final EnumConstant enumConstant : enumConstants )
    {
      if ( enumConstant.getName().equals(name) )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsStaticPublicMethodNamed ( final String name )
  {
    for ( final Method method : methodDeclarations )
    {
      if ( method.getName().equals(name) &&
           method.getModifiers().isStatic() &&
           method.getModifiers().isPublic() )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsStaticPublicTypeNamed ( final String name )
  {
    for ( final TypeDeclaration type : typeDeclarations )
    {
      if ( type.getName().equals(name) &&
           type.getModifiers().isStatic() &&
           type.getModifiers().isPublic() )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsTypeNamed ( final String name )
  {
    for ( final TypeDeclaration type : typeDeclarations )
    {
      if ( type.getName().equals(name) )
      {
        return true;
      }
    }
    return false;
  }

  Scope getDeclaringScope ()
  {
    return getScope().getParentScope();
  }


  TypeDeclarationImpl getSupertype ()
  {
    if ( getParentTypeDeclaration() != null && getParentTypeDeclaration().getSupertype() != null )
    {
      return getParentTypeDeclaration().getSupertype().getTypeDeclaration();
    }
    else if ( getParentInstanceCreationExpression() != null )
    {
      return getParentInstanceCreationExpression().getInstanceType().getTypeDeclaration();
    }
    else
    {
      return null;
    }

  }


  private final ArrayList<TypeDeclarationImpl> typeDeclarations   = new ArrayList<TypeDeclarationImpl>();
  private final ArrayList<FieldImpl>           fieldDeclarations  = new ArrayList<FieldImpl>();
  private final ArrayList<MethodImpl>          methodDeclarations = new ArrayList<MethodImpl>();
  private final ChildNodeList<TypeMemberImpl>  allMembers         = new ChildNodeList<TypeMemberImpl>(this);

  private final TBScope                        scope;


  @Override
  public This asThis ()
  {
    return getAST().createThis(this);
  }

  private final ChildNodeList<EnumConstantImpl> enumConstants = new ChildNodeList<EnumConstantImpl>(this);

  @Override
  public EnumConstantImpl addEnumConstant ( final EnumConstant enumConstant )
  {
    enumConstants.add((EnumConstantImpl)enumConstant);
    return (EnumConstantImpl)enumConstant;
  }

  @Override
  public EnumConstantImpl addEnumConstant ( final String name, final Expression... args )
  {
    return addEnumConstant(getAST().createEnumConstant(name, args));
  }

  @Override
  public List<? extends EnumConstant> getEnumConstants ()
  {
    return Collections.unmodifiableList(enumConstants);
  }


}
