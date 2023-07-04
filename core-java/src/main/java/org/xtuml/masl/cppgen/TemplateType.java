/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

/**
 * Encapulates a template placeholder type. For instance, when creating a
 * templated function, <code>{@literal template<class T> f(T p);}</code>, the
 * template parameter <code>T</code> would be a TemplateType.
 */
public final class TemplateType extends Type {

    /**
     * Constructs a TemplateType with the given name
     * <p>
     * <p>
     * The name of the type
     */
    public TemplateType(final String name) {
        super(name);
    }

    @Override
    public boolean isTemplateType() {
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
    boolean preferPassByReference() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Class asClass() {
        final Class clazz = new Class(getName(), getParentNamespace(), getDirectUsageIncludes());
        clazz.setForceTemplate(true);
        return clazz;
    }

}
