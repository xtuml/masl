/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.def.Constructor;
import org.xtuml.masl.javagen.ast.def.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class ASTNodeImpl implements ASTNode {

    ASTNodeImpl(final ASTImpl ast) {
        this.ast = ast;
    }

    @Override
    public ASTImpl getAST() {
        return ast;
    }

    @Override
    public Collection<ASTNodeImpl> getChildNodes() {
        return children;
    }

    @Override
    public ASTNodeImpl getParentNode() {
        return parentNode;
    }

    void removeChildNode(final ASTNodeImpl childNode) {
        children.remove(childNode);
        childNode.parentNode = null;
    }

    void addChildNode(final ASTNodeImpl childNode) {
        if (childNode.getAST() != getAST()) {
            throw new IllegalStateException("AST mismatch");
        }
        children.add(childNode);
        childNode.parentNode = this;
    }

    Scope getEnclosingScope() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof Scoped) {
            return ((Scoped) parentNode).getScope();
        } else {
            return parentNode.getEnclosingScope();
        }
    }

    public TypeDeclarationImpl getEnclosingTypeDeclaration() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof TypeDeclarationImpl) {
            return (TypeDeclarationImpl) parentNode;
        } else {
            return parentNode.getEnclosingTypeDeclaration();
        }
    }

    public TypeBodyImpl getEnclosingTypeBody() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof TypeBodyImpl) {
            return (TypeBodyImpl) parentNode;
        } else {
            return parentNode.getEnclosingTypeBody();
        }
    }

    public StatementImpl getEnclosingStatement() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof StatementImpl) {
            return (StatementImpl) parentNode;
        } else {
            return parentNode.getEnclosingStatement();
        }
    }

    public CompilationUnitImpl getEnclosingCompilationUnit() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof CompilationUnitImpl) {
            return (CompilationUnitImpl) parentNode;
        } else {
            return parentNode.getEnclosingCompilationUnit();
        }
    }

    public PackageImpl getEnclosingPackage() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof PackageImpl) {
            return (PackageImpl) parentNode;
        } else {
            return parentNode.getEnclosingPackage();
        }
    }

    public MethodImpl getEnclosingMethod() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof Method) {
            return (MethodImpl) parentNode;
        } else {
            return parentNode.getEnclosingMethod();
        }
    }

    public MethodImpl getEnclosingConstructor() {
        if (parentNode == null) {
            return null;
        }
        if (parentNode instanceof Constructor) {
            return (MethodImpl) parentNode;
        } else {
            return parentNode.getEnclosingMethod();
        }
    }

    private final ASTImpl ast;
    private ASTNodeImpl parentNode;
    private final List<ASTNodeImpl> children = new ArrayList<>();

}
