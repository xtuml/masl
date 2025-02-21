/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate;

import java.lang.annotation.*;

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
public @interface Alias {

    /**
     * Defines an alternative name for the translator. An emmpty string indicates
     * that the strip prefix and suffix should be used to invent a name instead
     *
     * @return the alternative name
     */
    String value();

    /**
     * Defines the prefix to strip from the class name if value is the empty string
     *
     * @return the prefix
     */
    String stripPrefix() default "";

    /**
     * Defines the suffix to strip from the class name if value is the empty string
     *
     * @return the suffix
     */
    String stripSuffix() default "";

    class Util {

        static final public String getAlias(final Class<?> transClass) {
            final Alias alias = transClass.getAnnotation(Alias.class);
            if (alias.value().equals("")) {
                String name = transClass.getName();
                if (name.startsWith(alias.stripPrefix())) {
                    name = name.substring(alias.stripPrefix().length());
                }
                if (name.endsWith(alias.stripSuffix())) {
                    name = name.substring(0, name.length() - alias.stripSuffix().length());
                }
                return name;
            } else {
                return alias.value();
            }
        }
    }

}
