/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

/**
 * Abstract class to represent different types of C++ template parameter
 */
public abstract class TemplateParameter {

    /**
     * Gets the nae of this template parameter
     *
     * @return the name
     */
    public abstract String getName();

}
