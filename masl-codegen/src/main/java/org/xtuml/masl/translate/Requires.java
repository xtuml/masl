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
 * Defines an list of translators which must be present for this translator to
 * run.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Requires {

    /**
     * Defines a list of translators that are required for this translator to run.
     * If they are not present, the translator will not run (as opposed to
     * getPrerequisites, which forces the dependencies to run)
     *
     * @return list of translator aliases or classnames
     */
    String[] value();

}
