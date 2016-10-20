/*
 * Filename : ObjectTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;


public class ObjectTranslator
{

  private final ObjectDeclaration                        objectDecl;
  private final SqlFrameworkTranslator                   framework;

  private final List<GeneratedClass>                     classList                   = new ArrayList<GeneratedClass>();
  private final List<NormalRelationshipDeclaration>      normalRelationshipDeclList  = new ArrayList<NormalRelationshipDeclaration>();
  private final List<SubtypeRelationshipDeclaration>     subtypeRelationshipDeclList = new ArrayList<SubtypeRelationshipDeclaration>();
  private final List<AssociativeRelationshipDeclaration> assocRelationshipDeclList   = new ArrayList<AssociativeRelationshipDeclaration>();

  private final Namespace                                namespace;
  private final ImplementationClass                      implClass;

  ObjectTranslator ( final SqlFrameworkTranslator parent, final ObjectDeclaration objDec )
  {
    objectDecl = objDec;
    framework = parent;
    namespace = framework.getDatabase().getDatabaseTraits().getNameSpace();

    implClass = new ImplementationClass(this, objectDecl, namespace);
    classList.add(implClass);
    classList.add(new PopulationClass(this, objectDecl, namespace));
    classList.add(new MapperClass(this, objectDecl, namespace));
    classList.add(new MapperSqlClass(this, objectDecl, namespace));
  }

  Database getDatabase ()
  {
    return framework.getDatabase();
  }

  SqlFrameworkTranslator getFrameworkTranslator ()
  {
    return framework;
  }

  void addRelationship ( final RelationshipDeclaration relationship )
  {

    if ( relationship instanceof NormalRelationshipDeclaration )
    {
      final NormalRelationshipDeclaration normalRelDecl = (NormalRelationshipDeclaration)relationship;
      if ( normalRelDecl.getLeftObject() == objectDecl || normalRelDecl.getRightObject() == objectDecl )
      {
        normalRelationshipDeclList.add(normalRelDecl);
      }
    }
    else if ( relationship instanceof SubtypeRelationshipDeclaration )
    {
      final SubtypeRelationshipDeclaration subtypeRelDecl = (SubtypeRelationshipDeclaration)relationship;
      if ( subtypeRelDecl.getSupertype() == objectDecl || subtypeRelDecl.getSubtypes().contains(objectDecl) )
      {
        subtypeRelationshipDeclList.add(subtypeRelDecl);
      }
    }
    else if ( relationship instanceof AssociativeRelationshipDeclaration )
    {
      final AssociativeRelationshipDeclaration assocRelDecl = (AssociativeRelationshipDeclaration)relationship;
      if ( assocRelDecl.getLeftObject() == objectDecl ||
                assocRelDecl.getRightObject() == objectDecl ||
                assocRelDecl.getAssocObject() == objectDecl )
      {
        assocRelationshipDeclList.add(assocRelDecl);
      }
    }
  }


  void translateModel ()
  {
    doAttributes();
    doRelationships();
    doEvents();
  }

  void translateActions ()
  {
    doFinds();
    doNavigates();
  }

  org.xtuml.masl.translate.main.object.ObjectTranslator getMainObjectTranslator ( final ObjectDeclaration declaration )
  {
    return org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(declaration);
  }

  public Class getClass ( final String name )
  {
    Class requiredClass = null;
    for ( final GeneratedClass currentClass : classList )
    {
      if ( currentClass.getClassName().equals(name) )
      {
        requiredClass = currentClass.getCppClass();
        break;
      }
    }
    return requiredClass;
  }

  List<NormalRelationshipDeclaration> getNormalRelationships ()
  {
    return normalRelationshipDeclList;
  }

  List<SubtypeRelationshipDeclaration> getSubTypeRelationships ()
  {
    return subtypeRelationshipDeclList;
  }

  List<AssociativeRelationshipDeclaration> getAssociativeRelationships ()
  {
    return assocRelationshipDeclList;
  }

  public Function getSetterMethod ( final AttributeDeclaration attribute )
  {
    return implClass.getSetterMethod(attribute);
  }

  TypedefType getKeyType ( final IdentifierDeclaration identifier )
  {
    return implClass.getKeyType(identifier);
  }

  Function getKeyGetterFn ( final IdentifierDeclaration identifier )
  {
    return implClass.getKeyGetterFn(identifier);
  }

  private void doAttributes ()
  {
    for ( final GeneratedClass currentClass : classList )
    {
      currentClass.translateAttributes();
    }
  }

  private void doRelationships ()
  {
    for ( final GeneratedClass currentClass : classList )
    {
      currentClass.translateRelationships();
    }
  }

  private void doEvents ()
  {
    for ( final GeneratedClass currentClass : classList )
    {
      currentClass.translateEvents();
    }
  }

  private void doFinds ()
  {
    for ( final GeneratedClass currentClass : classList )
    {
      currentClass.translateFind();
    }
  }

  private void doNavigates ()
  {
    for ( final GeneratedClass currentClass : classList )
    {
      currentClass.translateNavigations();
    }
  }

}
