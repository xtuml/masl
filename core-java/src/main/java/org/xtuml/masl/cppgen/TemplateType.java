//
// File: TemplateType
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//

package org.xtuml.masl.cppgen;


/**
 * Encapulates a template placeholder type. For instance, when creating a
 * templated function, <code>{@literal template<class T> f(T p);}</code>, the
 * template parameter <code>T</code> would be a TemplateType.
 */
public final class TemplateType extends Type
{

  /**
   * Constructs a TemplateType with the given name
   *

   *          The name of the type
   */
  public TemplateType ( final String name )
  {
    super(name);
  }

  @Override
  public boolean isTemplateType ()
  {
    return true;
  }

  @Override
  /**
   * {@inheritDoc}
   *
   * It is up to the using template to decide whether to pass by reference, so
   * don't attempt to give it any hints.
   *
   * @return false
   */
  boolean preferPassByReference ()
  {
    return false;
  }

  @Override
  public String toString ()
  {
    return getName();
  }

  public Class asClass ()
  {
    final Class clazz = new Class(getName(), getParentNamespace(), getDirectUsageIncludes());
    clazz.setForceTemplate(true);
    return clazz;
  }

}
