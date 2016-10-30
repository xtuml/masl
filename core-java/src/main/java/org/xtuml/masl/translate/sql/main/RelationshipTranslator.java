/*
 * Filename : RelationshipTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;


/**
 * For each relationship specification defined in the MASL model file an
 * instance of this translator class is created to enable the generation of the
 * required C++ to handle the link/unlink and navigation operations.
 * 
 * Each relationship has an associated relationship Mapper class to handle the
 * behavioural requirements of the relationship and a matching Mapper SQL class
 * that provides the SQL implementation to support this behaviour.
 */
public class RelationshipTranslator
{

  private final SqlFrameworkTranslator  framework;
  private final RelationshipMapperClass mapperClass;
  private final Namespace               namespace;

  public RelationshipTranslator ( final SqlFrameworkTranslator framework, final RelationshipDeclaration relationship )
  {
    this.framework = framework;
    namespace = new Namespace(framework.getDatabase().getDatabaseTraits().getName().toUpperCase());
    mapperClass = new RelationshipMapperClass(this, relationship, namespace);
  }

  /**
   * 
   * @return
   */
  SqlFrameworkTranslator getFramework ()
  {
    return framework;
  }

  /**
   * 
   * @return
   */
  DatabaseTraits getDatabaseTraits ()
  {
    return framework.getDatabase().getDatabaseTraits();
  }

  /**
   * The actual C++ class that is generated to implement the relationship Mapper
   * Class needs to be accessed so that the MASL Object code generation
   * component can utilise it during its operation.
   * 
   * @return the actual C++ relationship Mapper class
   */
  Class getRelationshipMapperClass ()
  {
    return mapperClass.getMapperClass();
  }

  /**
   * The actual C++ class that is generated to implement the relationship
   * MapperSQL Class needs to be accessed so that the MASL Object code
   * generation component can utilise it during its operation.
   * 
   * @return the actual C++ relationship MapperSQL class
   */
  Class getRelationshipMapperSqlClass ()
  {
    return mapperClass.getMapperSqlClass();
  }

  /**
   * For subtypeRelationship declarations multiple C++ Mapper classes are
   * generated, one for each supertype/subtype pair (@see
   * SubTypeRelationshipMapperSqlClass). The actual C++ class generated needs to
   * be accessed so that the MASL Object code generation component can utilise
   * it during its operation. Therefore return this class for the parameter
   * values specified.
   * 

   *          the supertype object for the required relationship

   *          the derived object for the required relationship
   * @return the C++ Mapper Class corresponding to the pair of supplied objects
   */
  Class getSuperToSubtypeRelationshipMapperClass ( final ObjectDeclaration supertypeObjDecl, final ObjectDeclaration subtypeObjDecl )
  {
    return mapperClass.getSuperToSubMapperClass(supertypeObjDecl, subtypeObjDecl);
  }

  /**
   * For subtypeRelationship declarations multiple C++ MapperSQL classes are
   * generated, one for each supertype/subtype pair (@see
   * SubTypeRelationshipMapperSqlClass). The actual C++ class generated needs to
   * be accessed so that the MASL Object code generation component can utilise
   * it during its operation. Therefore return this class for the parameter
   * values specified.
   * 

   *          the supertype object for the required relationship

   *          the derived object for the required relationship
   * @return the C++ MapperSQL Class corresponding to the pair of supplied
   *         objects
   */
  Class getSuperToSubtypeRelationshipMapperSqlClass ( final ObjectDeclaration supertypeObjDecl,
                                                      final ObjectDeclaration subtypeObjDecl )
  {
    return mapperClass.getSuperToSubMapperSqlClass(supertypeObjDecl, subtypeObjDecl);
  }

  /**
   * Decompose the specified relationship to determine the multiplicity of the
   * association and return an enum representation that can be used withinin
   * select constructs.
   * 

   *          the relationship to interrorgate.
   * @return an enum value to represent the multiplicity of relationship
   *         declaration.
   */
  static RelationshipType getRelationshipType ( final RelationshipDeclaration relationshipDecl )
  {
    RelationshipType relType = null;
    if ( relationshipDecl instanceof NormalRelationshipDeclaration )
    {
      final NormalRelationshipDeclaration normRelationshipDecl = (NormalRelationshipDeclaration)relationshipDecl;
      if ( normRelationshipDecl.getLeftMult() == MultiplicityType.ONE &&
                  normRelationshipDecl.getRightMult() == MultiplicityType.ONE )
      {
        relType = RelationshipType.OneToOne;
      }
      else if ( normRelationshipDecl.getLeftMult() == MultiplicityType.ONE &&
                   normRelationshipDecl.getRightMult() == MultiplicityType.MANY )
      {
        relType = RelationshipType.OneToMany;
      }
      else if ( normRelationshipDecl.getLeftMult() == MultiplicityType.MANY &&
                   normRelationshipDecl.getRightMult() == MultiplicityType.ONE )
      {
        relType = RelationshipType.ManyToOne;
      }
    }
    else if ( relationshipDecl instanceof AssociativeRelationshipDeclaration )
    {
      final AssociativeRelationshipDeclaration assoRelationshipDecl = (AssociativeRelationshipDeclaration)relationshipDecl;
      if ( assoRelationshipDecl.getLeftMult() == MultiplicityType.ONE &&
                  assoRelationshipDecl.getRightMult() == MultiplicityType.ONE )
      {
        relType = RelationshipType.AssocOneToOne;
      }
      else if ( assoRelationshipDecl.getLeftMult() == MultiplicityType.ONE &&
                      assoRelationshipDecl.getRightMult() == MultiplicityType.MANY )
      {
        relType = RelationshipType.AssocOneToMany;
      }
      else if ( assoRelationshipDecl.getLeftMult() == MultiplicityType.MANY &&
                       assoRelationshipDecl.getRightMult() == MultiplicityType.ONE )
      {
        relType = RelationshipType.AssocManyToOne;
      }
      else if ( assoRelationshipDecl.getLeftMult() == MultiplicityType.MANY &&
                       assoRelationshipDecl.getRightMult() == MultiplicityType.MANY )
      {
        relType = RelationshipType.AssocManyToMany;
      }
    }
    else if ( relationshipDecl instanceof SubtypeRelationshipDeclaration )
    {
      relType = RelationshipType.SubToSuper;
    }
    return relType;
  }

  /**
   * 

   *          the relationship declaration
   * @return The actual relationship number for the specified relationship.
   */
  static int getRelationshipNumber ( final RelationshipDeclaration relationshipDecl )
  {
    return Integer.parseInt(relationshipDecl.getName().substring(1)); // move
                                                                      // past
                                                                      // the 'R'
                                                                      // character.
  }
}
