//
// File: Type.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * The superclass of all types. Each type may be defined in either a parent
 * {@link Namespace}or {@link Class}.
 * 
 */
public abstract class Type
{

  Declaration getDeclaration ()
  {
    return null;
  }

  /**
   * Gets the code file that this type is declared in. If the type is declared
   * in more than one file, then an arbitrary one is chosen.
   * 
   * @return the code file containing a declaration for this type.
   */
  public CodeFile getDeclaredIn ()
  {
    final Declaration dec = getDeclaration();
    if ( dec == null )
    {
      return null;
    }
    else
    {
      final Set<CodeFile> includes = dec.getUsageIncludes();
      if ( includes.iterator().hasNext() )
      {
        return includes.iterator().next();
      }
      else
      {
        return null;
      }
    }
  }

  /**
   * The name of the type
   */
  private final String name;

  /**
   * The parent namespace of the type.
   */
  private Namespace    parentNamespace;

  /**
   * Creates a type with the given name
   * 

   *          - the name of the type to create
   */
  protected Type ( final String name )
  {
    this(name, null);
  }

  /**
   * Creates a type with the given name and parent scope
   * 

   *          - the name of the type to create
   */
  protected Type ( final String name, final Namespace parentNamespace )
  {
    this.name = name;
    this.parentNamespace = parentNamespace;
  }

  /**
   * Calculates the set of forward declarations needed to make direct (ie not
   * via a reference or pointer) use of this type. Typically this will return an
   * empty set, as direct use would require a full declaration, and hence need
   * the include file.
   * 
   * @return the required forward declarations
   */
  Set<Declaration> getDirectUsageForwardDeclarations ()
  {
    return new LinkedHashSet<Declaration>();
  }

  /**
   * Calculates the set of include files needed to make direct (ie not via a
   * reference or pointer) use of this type.
   * 
   * @return the required include files
   */
  Set<CodeFile> getDirectUsageIncludes ()
  {
    return new LinkedHashSet<CodeFile>();
  }

  /**
   * Calculates the set of include files needed to make direct (ie not via a
   * reference or pointer) use of this type.
   * 
   * @return the required include files
   */
  Set<CodeFile> getNoRefDirectUsageIncludes ()
  {
    return new LinkedHashSet<CodeFile>();
  }

  /**
   * Calculates the set of forward declarations needed to make use of this type
   * via a pointer or reference.
   * 
   * @return the required forward declarations
   */
  Set<Declaration> getIndirectUsageForwardDeclarations ()
  {
    return new LinkedHashSet<Declaration>();
  }

  /**
   * Calculates the set of include files needed to make use of this type via a
   * pointer or reference.
   * 
   * @return the required include files
   */
  Set<CodeFile> getIndirectUsageIncludes ()
  {
    return new LinkedHashSet<CodeFile>();
  }

  /**
   * @return the name of the type
   */
  public String getName ()
  {
    return name;
  }

  /**
   * Gets the fully qualified name of the type.
   * 
   * @return the qualified name
   */
  String getQualifiedName ()
  {
    return getQualifiedName(null);
  }

  /**
   * Gets the name of the type suitably qualified to be used in the supplied
   * namespace
   * 

   *          The namespace the name is to be used in
   * @return the qualified name
   */
  String getQualifiedName ( final Namespace currentNamespace )
  {
    if ( parentNamespace == null )
    {
      return name;
    }
    else if ( parentNamespace.contains(currentNamespace) )
    {
      return name;
    }
    else
    {
      return parentNamespace.getQualifiedName(currentNamespace) + "::" + name;
    }

  }

  /**
   * Sets the parent namespace of this type
   * 

   */
  void setParentNamespace ( final Namespace parentNamespace )
  {
    this.parentNamespace = parentNamespace;
  }

  Namespace getParentNamespace ()
  {
    return parentNamespace;
  }

  /**
   * Calculates whether the type should be passed by reference or not. Some C++
   * types are more efficiently passed by reference, eg Classes, and some are
   * more efficiently passed by value, eg fundamental types. This method enables
   * us to discern between the two.
   * 
   * @return whether this type should be passed by reference if possible
   */
  abstract boolean preferPassByReference ();

  @Override
  public String toString ()
  {
    return getQualifiedName();
  }

  @Override
  public boolean equals ( final Object rhs )
  {
    if ( this == rhs )
    {
      return true;
    }
    if ( rhs instanceof Type )
    {
      final Type rhsType = (Type)rhs;
      return name.equals(rhsType.name)
             && ((getParentNamespace() == null && rhsType.parentNamespace == null) || parentNamespace
                                                                                                     .equals(rhsType.parentNamespace));
    }
    return false;
  }

  @Override
  public int hashCode ()
  {
    return name.hashCode() ^ (parentNamespace == null ? 0 : parentNamespace.hashCode());
  }

  /**
   * Creates a function call to a constructor on the class. This is normally
   * used to call constructors on externally defined classes, so that there is
   * no need to create all the constructors individually.
   * 

   *          A list of paramters to pass to the constructor
   * @return a call to a constructor on the class
   */
  public FunctionCall callConstructor ( final Expression... params )
  {
    return callConstructor(Arrays.asList(params));
  }

  /**
   * Creates a function call to a constructor on the class. This is normally
   * used to call constructors on externally defined classes, so that there is
   * no need to create all the constructors individually.
   * 

   *          A list of paramters to pass to the constructor
   * @return a call to a constructor on the class
   */
  public FunctionCall callConstructor ( final List<Expression> params )
  {
    final Function function = new Function(getName(), parentNamespace);
    return function.asFunctionCall(params);
  }

  /**
   * Decides whether this type is a template. For the purposes of this method,
   * this type is a template if it has any template parameters which have not
   * been specialised.
   * <p>
   * For example, given the following class definition, <code>
   {@literal template<class T,class U>}
   class A
   {
   };
   </code> the type {@code A<int,int>} would not be a template as it has been
   * fully specialised, but {@code A<Z,int>}, where {@code Z} is a template
   * parameter in the current scope, would be.
   * <p>
   * This information is useful when deciding whether to use the {@code
   * obj.template f<T>()} form of member reference, which is required when both
   * {@code obj} and {@code f} are templates.
   * 
   * @return true if this is a template class, false otherwise
   */
  boolean isTemplateType ()
  {
    return false;
  }
}
