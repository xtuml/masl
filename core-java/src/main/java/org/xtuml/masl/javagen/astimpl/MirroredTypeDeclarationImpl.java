//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.javagen.ast.types.DeclaredType;


class MirroredTypeDeclarationImpl extends TypeDeclarationImpl
{

  MirroredTypeDeclarationImpl ( final ASTImpl ast, final Class<?> clazz )
  {
    super(ast, clazz.getSimpleName());
    this.mirroredClass = clazz;
    if ( clazz.isInterface() )
    {
      super.setInterface();
    }
    if ( clazz.isAnnotation() )
    {
      super.setAnnotation();
    }
    if ( clazz.isEnum() )
    {
      super.setEnum();
    }
    getModifiers().setModifiers(clazz.getModifiers());
    setTypeBody(new MirroredTypeBodyImpl(ast, clazz));
  }

  @Override
  public DeclaredTypeImpl addInterface ( final DeclaredType iface )
  {
    throw new UnsupportedOperationException("Mirrored Class");
  }

  @Override
  public List<DeclaredTypeImpl> getInterfaces ()
  {
    populateMirror();
    return super.getInterfaces();
  }

  @Override
  public DeclaredTypeImpl getSupertype ()
  {
    populateMirror();
    return super.getSupertype();
  }

  @Override
  public List<TypeParameterImpl> getTypeParameters ()
  {
    populateMirror();
    return super.getTypeParameters();
  }

  @Override
  public MirroredTypeBodyImpl getTypeBody ()
  {
    return (MirroredTypeBodyImpl)super.getTypeBody();
  }

  @Override
  public void setAnnotation ()
  {
    throw new UnsupportedOperationException("Mirrored Class");
  }

  @Override
  public void setInterface ()
  {
    throw new UnsupportedOperationException("Mirrored Class");
  }

  @Override
  public DeclaredTypeImpl setSupertype ( final DeclaredType supertype )
  {
    throw new UnsupportedOperationException("Mirrored Class");
  }


  @Override
  public String toString ()
  {
    return "declaration of " + getName();
  }


  private void populateMirror ()
  {
    if ( !mirrorPopulated )
    {
      mirrorPopulated = true;

      for ( final java.lang.reflect.TypeVariable<?> tp : mirroredClass.getTypeParameters() )
      {
        final MirroredTypeParameterImpl parameter = new MirroredTypeParameterImpl(getAST(), tp, getScope());
        super.addTypeParameter(parameter);
        typeParameterLookup.put(tp, parameter);
      }

      for ( final java.lang.reflect.Type iface : mirroredClass.getGenericInterfaces() )
      {
        super.addInterface((DeclaredType)getAST().createType(iface));
      }
      if ( mirroredClass.getGenericSuperclass() != null )
      {
        super.setSupertype((DeclaredType)getAST().createType(mirroredClass.getGenericSuperclass()));
      }

      if ( mirroredClass.isEnum() )
      {
        for ( final Object ec : mirroredClass.getEnumConstants() )
        {
          final MirroredEnumConstantImpl enumConstant = new MirroredEnumConstantImpl(getAST(), (Enum<?>)ec);
          super.addEnumConstant(enumConstant);
          enumConstantLookup.put((Enum<?>)ec, enumConstant);
        }
      }

    }
  }

  MirroredTypeParameterImpl getTypeParameter ( final java.lang.reflect.TypeVariable<?> variable )
  {
    populateMirror();
    return typeParameterLookup.get(variable);
  }

  MirroredEnumConstantImpl getEnumConstant ( final Enum<?> ec )
  {
    populateMirror();
    return enumConstantLookup.get(ec);
  }


  private Class<?>                                                                mirroredClass       = null;
  private boolean                                                                 mirrorPopulated     = false;
  private final Map<java.lang.reflect.TypeVariable<?>, MirroredTypeParameterImpl> typeParameterLookup = new HashMap<java.lang.reflect.TypeVariable<?>, MirroredTypeParameterImpl>();
  private final Map<Enum<?>, MirroredEnumConstantImpl>                            enumConstantLookup  = new HashMap<Enum<?>, MirroredEnumConstantImpl>();


}
