//
// File: Alias.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Defines an alias for the class which it attached to. Used by the Translators
 * to give an alternative name for preferences to refer to the class by. If the
 * value is the empty string, then it is intended that the name is invented by
 * using the class name of the translator with the prefix and suffix stripped as
 * specified.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Default
{

  public class Util
  {

    static final public boolean isDefault ( final Class<?> transClass )
    {
      return transClass.getAnnotation(Default.class) != null;
    }
  }

}
