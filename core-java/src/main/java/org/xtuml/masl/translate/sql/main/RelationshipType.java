/*
 * Filename : RelationshipType.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

/**
 * Define a set of enumeration values that can be used to represent the
 * multiplicity of a relationship.
 */
enum RelationshipType
{
  OneToOne,
                          OneToMany,
                          ManyToOne,
                          SubToSuper,
                          SuperToSub,
                          AssocOneToOne,
                          AssocOneToMany,
                          AssocManyToOne,
                          AssocManyToMany
}
