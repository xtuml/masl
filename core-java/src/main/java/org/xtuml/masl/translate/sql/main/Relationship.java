/*
 * Filename : Relationship.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;


public class Relationship
{

  static public TypedefType assocOneToOneRelationshipMapper ( final int relationshipNumber,
                                                              final Class leftClass,
                                                              final Class rightClass,
                                                              final Class assocClass,
                                                              final boolean leftCondition,
                                                              final boolean rightCondition,
                                                              final String className,
                                                              final Namespace namespace )
  {
    final Class assocOneToOneMapperClass = new Class("AssociativeOneToOneRelationship", Database.namespace, Database.assocOneToOneMapperInc);
    assocOneToOneMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    assocOneToOneMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    assocOneToOneMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    assocOneToOneMapperClass.addTemplateSpecialisation(new TypeUsage(assocClass));
    assocOneToOneMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    assocOneToOneMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = assocOneToOneMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType assocOneToManyRelationshipMapper ( final int relationshipNumber,
                                                               final Class leftClass,
                                                               final Class rightClass,
                                                               final Class assocClass,
                                                               final boolean leftCondition,
                                                               final boolean rightCondition,
                                                               final String className,
                                                               final Namespace namespace )
  {
    final Class assocOneToManyMapperClass = new Class("AssociativeOneToManyRelationship",
                                                      Database.namespace,
                                                      Database.assocOneToManyMapperInc);
    assocOneToManyMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    assocOneToManyMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    assocOneToManyMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    assocOneToManyMapperClass.addTemplateSpecialisation(new TypeUsage(assocClass));
    assocOneToManyMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    assocOneToManyMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = assocOneToManyMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType assocManyToOneRelationshipMapper ( final int relationshipNumber,
                                                               final Class leftClass,
                                                               final Class rightClass,
                                                               final Class assocClass,
                                                               final boolean leftCondition,
                                                               final boolean rightCondition,
                                                               final String className,
                                                               final Namespace namespace )
  {
    final Class assocManyToOneMapperClass = new Class("AssociativeManyToOneRelationship",
                                                      Database.namespace,
                                                      Database.assocManyToOneMapperInc);
    assocManyToOneMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    assocManyToOneMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    assocManyToOneMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    assocManyToOneMapperClass.addTemplateSpecialisation(new TypeUsage(assocClass));
    assocManyToOneMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    assocManyToOneMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = assocManyToOneMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType assocManyToManyRelationshipMapper ( final int relationshipNumber,
                                                                final Class leftClass,
                                                                final Class rightClass,
                                                                final Class assocClass,
                                                                final boolean leftCondition,
                                                                final boolean rightCondition,
                                                                final String className,
                                                                final Namespace namespace )
  {
    final Class assocManyToManyMapperClass = new Class("AssociativeManyToManyRelationship",
                                                       Database.namespace,
                                                       Database.assocManyToManyMapperInc);
    assocManyToManyMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    assocManyToManyMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    assocManyToManyMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    assocManyToManyMapperClass.addTemplateSpecialisation(new TypeUsage(assocClass));
    assocManyToManyMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    assocManyToManyMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = assocManyToManyMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType oneToOneRelationshipMapper ( final int relationshipNumber,
                                                         final Class leftClass,
                                                         final Class rightClass,
                                                         final boolean leftCondition,
                                                         final boolean rightCondition,
                                                         final String className,
                                                         final Namespace namespace )
  {
    final Class oneToOneMapperClass = new Class("OneToOneRelationship", Database.namespace, Database.oneToOneMapperInc);
    oneToOneMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    oneToOneMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    oneToOneMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    oneToOneMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    oneToOneMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = oneToOneMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType oneToManyRelationshipMapper ( final int relationshipNumber,
                                                          final Class leftClass,
                                                          final Class rightClass,
                                                          final boolean leftCondition,
                                                          final boolean rightCondition,
                                                          final String className,
                                                          final Namespace namespace )
  {
    final Class oneToManyMapperClass = new Class("OneToManyRelationship", Database.namespace, Database.oneToManyMapperInc);
    oneToManyMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    oneToManyMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    oneToManyMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    oneToManyMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    oneToManyMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = oneToManyMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

  static public TypedefType ManyToOneRelationshipMapper ( final int relationshipNumber,
                                                          final Class leftClass,
                                                          final Class rightClass,
                                                          final boolean leftCondition,
                                                          final boolean rightCondition,
                                                          final String className,
                                                          final Namespace namespace )
  {
    final Class manyToOneMapperClass = new Class("ManyToOneRelationship", Database.namespace, Database.manyToOneMapperInc);
    manyToOneMapperClass.addTemplateSpecialisation(new Literal(relationshipNumber));
    manyToOneMapperClass.addTemplateSpecialisation(new TypeUsage(leftClass));
    manyToOneMapperClass.addTemplateSpecialisation(new TypeUsage(rightClass));
    manyToOneMapperClass.addTemplateSpecialisation(leftCondition == true ? new Literal("true") : new Literal("false"));
    manyToOneMapperClass.addTemplateSpecialisation(rightCondition == true ? new Literal("true") : new Literal("false"));

    final Class mapperTypedef = manyToOneMapperClass.referenceNestedType("mapper_type");
    final TypedefType requiredTypedef = new TypedefType(className, namespace, new TypeUsage(mapperTypedef));
    return requiredTypedef;
  }

}
