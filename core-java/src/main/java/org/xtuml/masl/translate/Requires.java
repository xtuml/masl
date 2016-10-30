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
 * Defines an list of translators which must be present for this translator to
 * run.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Requires
{

  /**
   * Defines a list of translators that are required for this translator to run.
   * If they are not present, the translator will not run (as opposed to
   * getPrerequisites, which forces the dependencies to run)
   * 
   * @return list of translator aliases or classnames
   */
  String[] value();

}
