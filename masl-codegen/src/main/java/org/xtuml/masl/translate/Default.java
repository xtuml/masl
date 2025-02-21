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
public @interface Default {

    class Util {

        static final public boolean isDefault(final Class<?> transClass) {
            return transClass.getAnnotation(Default.class) != null;
        }
    }

}
