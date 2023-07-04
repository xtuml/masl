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
package org.xtuml.masl.cppgen;

import java.util.Set;

public abstract class TemplateSpecialisation {

    abstract String getValue(Namespace currentNamespace);

    abstract Set<Declaration> getDirectUsageForwardDeclarations();

    abstract Set<CodeFile> getDirectUsageIncludes();

    abstract Set<Declaration> getIndirectUsageForwardDeclarations();

    abstract Set<CodeFile> getIndirectUsageIncludes();

    abstract Set<CodeFile> getNoRefDirectUsageIncludes();

    abstract boolean isTemplateType();

    public static TemplateSpecialisation create(final Expression expression) {
        return new TemplateSpecialisation() {

            @Override
            String getValue(final Namespace currentNamespace) {
                return expression.getCode(currentNamespace);
            }

            @Override
            Set<Declaration> getDirectUsageForwardDeclarations() {
                return expression.getForwardDeclarations();
            }

            @Override
            Set<Declaration> getIndirectUsageForwardDeclarations() {
                return expression.getForwardDeclarations();
            }

            @Override
            Set<CodeFile> getDirectUsageIncludes() {
                return expression.getIncludes();
            }

            @Override
            Set<CodeFile> getIndirectUsageIncludes() {
                return expression.getIncludes();
            }

            @Override
            Set<CodeFile> getNoRefDirectUsageIncludes() {
                return expression.getIncludes();
            }

            @Override
            boolean isTemplateType() {
                return false;
            }

        };
    }

    public static TemplateSpecialisation create(final TypeUsage type) {
        return new TemplateSpecialisation() {

            @Override
            String getValue(final Namespace currentNamespace) {
                return type.getQualifiedName(currentNamespace);
            }

            @Override
            Set<Declaration> getDirectUsageForwardDeclarations() {
                return type.getDirectUsageForwardDeclarations();
            }

            @Override
            Set<CodeFile> getDirectUsageIncludes() {
                return type.getDirectUsageIncludes();
            }

            @Override
            Set<CodeFile> getNoRefDirectUsageIncludes() {
                return type.getNoRefDirectUsageIncludes();
            }

            @Override
            Set<Declaration> getIndirectUsageForwardDeclarations() {
                return type.getIndirectUsageForwardDeclarations();
            }

            @Override
            Set<CodeFile> getIndirectUsageIncludes() {
                return type.getIndirectUsageIncludes();
            }

            @Override
            boolean isTemplateType() {
                return type.isTemplateType();
            }

        };
    }

}
