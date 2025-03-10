/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

/**
 * Define a set of enumeration values that can be used to represent the
 * multiplicity of a relationship.
 */
enum RelationshipType {
    OneToOne, OneToMany, ManyToOne, SubToSuper, SuperToSub, AssocOneToOne, AssocOneToMany, AssocManyToOne, AssocManyToMany
}
