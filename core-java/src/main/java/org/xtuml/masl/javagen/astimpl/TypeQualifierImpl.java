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

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;

public class TypeQualifierImpl extends QualifierImpl implements TypeQualifier {

    public TypeQualifierImpl(final ASTImpl ast, final TypeDeclaration type) {
        super(ast);
        if (type == null) {
            throw new IllegalArgumentException("Cannot qualify anonymous type");
        }
        this.type = (TypeDeclarationImpl) type;
    }

    @Override
    public TypeDeclarationImpl getTypeDeclaration() {
        return type;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitTypeQualifier(this);
    }

    private final TypeDeclarationImpl type;

    @Override
    public void forceQualifier() {
        if (type.getDeclaringCompilationUnit() != null) {
            qualifier.set(new PackageQualifierImpl(getAST(), type.getEnclosingPackage()));
        } else {
            qualifier.set(new TypeQualifierImpl(getAST(), type.getDeclaringType()));
        }
    }

    @Override
    public QualifierImpl getQualifier() {
        if (qualifier.get() == null && getEnclosingScope().requiresQualifier(type)) {
            forceQualifier();
        }
        return qualifier.get();
    }

    private final ChildNode<QualifierImpl> qualifier = new ChildNode<>(this);
}
