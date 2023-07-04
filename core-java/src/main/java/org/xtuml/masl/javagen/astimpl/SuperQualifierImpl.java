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
import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.expr.SuperQualifier;
import org.xtuml.masl.javagen.ast.expr.TypeQualifier;

public class SuperQualifierImpl extends QualifierImpl implements SuperQualifier {

    public SuperQualifierImpl(final ASTImpl ast, final TypeBody typeBody) {
        super(ast);
        setTypeBody(typeBody);
    }

    private void setTypeBody(final TypeBody typeBody) {
        this.typeBody = (TypeBodyImpl) typeBody;
    }

    @Override
    public TypeBodyImpl getTypeBody() {
        if (typeBody == null) {
            typeBody = getEnclosingTypeBody();
        }
        return typeBody;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitSuperQualifier(this);
    }

    private TypeBodyImpl typeBody;

    @Override
    public TypeQualifierImpl getQualifier() {
        if (getEnclosingScope().requiresQualifier(this)) {
            forceQualifier();
        }
        return qualifier.get();
    }

    private void setQualifier(final TypeQualifier qualifier) {
        this.qualifier.set((TypeQualifierImpl) qualifier);
    }

    private final ChildNode<TypeQualifierImpl> qualifier = new ChildNode<>(this);

    @Override
    public void forceQualifier() {
        setQualifier(new TypeQualifierImpl(getAST(), getTypeBody().getParentTypeDeclaration()));
    }

}
