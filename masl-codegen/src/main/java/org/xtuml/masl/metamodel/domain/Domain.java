/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.domain;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.type.TypeDeclaration;

import java.util.List;
import java.util.Set;

/**
 * Provides an interface to query information about a domain defined in MASL.
 */
public interface Domain extends ASTNode {

    /**
     * The name of the domain
     *
     * @return the domain name
     */
    String getName();

    /**
     * Returns a list of exceptions defined in the domain
     *
     * @return a list of exceptions
     */
    List<? extends ExceptionDeclaration> getExceptions();

    /**
     * Returns a list of objects defined in the domain
     *
     * @return a list of objects
     */
    List<? extends ObjectDeclaration> getObjects();

    /**
     * Returns a list of terminators defined in the domain
     *
     * @return a list of terminators
     */
    List<? extends DomainTerminator> getTerminators();

    /**
     * Returns the list of pragmas defined for the domain
     *
     * @return the list of pragmas
     */
    PragmaList getPragmas();

    /**
     * Returns a list of relationships linking objects defined in the domain
     *
     * @return a list of relationships
     */
    List<? extends RelationshipDeclaration> getRelationships();

    /**
     * Returns a list of domain services, scenarios and externals defined in the
     * domain
     *
     * @return a list of services
     */
    List<? extends DomainService> getServices();

    /**
     * Returns a list of types forward declared in the domain
     *
     * @return a list of types
     */
    List<? extends TypeDeclaration> getTypeForwardDeclarations();

    /**
     * Returns a list of types defined in the domain
     *
     * @return a list of types
     */
    List<? extends TypeDeclaration> getTypes();

    /**
     * Returns the list of all interfaces referenced directly by this domain or
     * interface. If this is an interface then only those interfaces referenced in
     * the interface file will be returned. If this is a domain, then all interfaces
     * references in the the domain definition and masl code will be returned.
     *
     * @return the list of interfaces
     */
    Set<? extends Domain> getReferencedInterfaces();

}
