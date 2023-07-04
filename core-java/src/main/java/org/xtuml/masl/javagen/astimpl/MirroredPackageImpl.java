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
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.def.CompilationUnit;

import java.util.HashMap;
import java.util.Map;

class MirroredPackageImpl extends PackageImpl {

    MirroredPackageImpl(final ASTImpl ast, final java.lang.Package pkg, final boolean isExtensible) {
        super(ast, pkg.getName());
        this.isExtensible = isExtensible;
    }

    MirroredCompilationUnitImpl getCompilationUnit(Class<?> clazz) {
        while (clazz.getEnclosingClass() != null) {
            clazz = clazz.getEnclosingClass();
        }
        if (compilationUnitLookup.containsKey(clazz)) {
            return compilationUnitLookup.get(clazz);
        } else {
            final MirroredCompilationUnitImpl result = new MirroredCompilationUnitImpl(getAST(), clazz);
            super.addCompilationUnit(result);
            compilationUnitLookup.put(clazz, result);
            return result;
        }
    }

    private final Map<Class<?>, MirroredCompilationUnitImpl>
            compilationUnitLookup =
            new HashMap<Class<?>, MirroredCompilationUnitImpl>();

    @Override
    public CompilationUnitImpl addCompilationUnit(final CompilationUnit compilationUnit) {
        if (!isExtensible && !(compilationUnit instanceof MirroredCompilationUnitImpl)) {
            throw new IllegalStateException("Package is not extensible");
        }

        return super.addCompilationUnit(compilationUnit);
    }

    @Override
    boolean containsPublicTypeNamed(final String name) {
        try {
            final Class<?> clazz = Class.forName(getName() + "." + name);
            return java.lang.reflect.Modifier.isPublic(clazz.getModifiers());
        } catch (final ClassNotFoundException e) {
            return super.containsPublicTypeNamed(name);
        }
    }

    @Override
    boolean containsTypeNamed(final String name) {
        try {
            Class.forName(getName() + "." + name);
            return true;
        } catch (final ClassNotFoundException e) {
            return super.containsTypeNamed(name);
        }
    }

    private final boolean isExtensible;

    boolean isExtensible() {
        return isExtensible;
    }

}
