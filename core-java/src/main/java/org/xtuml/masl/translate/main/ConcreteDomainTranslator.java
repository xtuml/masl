//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.main.object.ConcreteObjectTranslator;



public abstract class ConcreteDomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  private final Map<ObjectDeclaration, ConcreteObjectTranslator> objectTranslators = new HashMap<ObjectDeclaration, ConcreteObjectTranslator>();

  protected ConcreteDomainTranslator ( final Domain domain )
  {
    super(domain);
    mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
  }

  protected final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

  /**
   * 
   * @return
   * @see org.xtuml.masl.translate.Translator#getPrerequisites()
   */
  @Override
  public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.DomainTranslator>asList(mainDomainTranslator);
  }

  protected abstract ConcreteObjectTranslator createTranslator ( ObjectDeclaration object );

  protected abstract void translateAuxiliaryFiles ( Domain domain );

  protected abstract String getLibNameSuffix ();

  protected abstract FileGroup getLibrary ();

  @Override
  public void translate ()
  {
    for ( final ObjectDeclaration object : domain.getObjects() )
    {
      final ConcreteObjectTranslator objectTranslator = createTranslator(object);
      objectTranslator.translate();
      objectTranslators.put(object, objectTranslator);
    }


    for ( final RelationshipDeclaration relationship : domain.getRelationships() )
    {
      translateRelationship(relationship);
    }

    for ( final ObjectDeclaration object : domain.getObjects() )
    {
      getObjectTranslator(object).translateRelationships();
    }

    translateAuxiliaryFiles(domain);

  }

  public void translateRelationship ( final RelationshipDeclaration relationship )
  {
    if ( relationship instanceof NormalRelationshipDeclaration )
    {
      createRelationship((NormalRelationshipDeclaration)relationship);
    }
    else if ( relationship instanceof AssociativeRelationshipDeclaration )
    {
      createRelationship((AssociativeRelationshipDeclaration)relationship);
    }
    else if ( relationship instanceof SubtypeRelationshipDeclaration )
    {
      createRelationship((SubtypeRelationshipDeclaration)relationship);
    }
  }

  private void createRelationship ( final AssociativeRelationshipDeclaration rel )
  {
    final ConcreteObjectTranslator leftObj = getObjectTranslator(rel.getLeftObject());
    final ConcreteObjectTranslator rightObj = getObjectTranslator(rel.getRightObject());
    final ConcreteObjectTranslator assocObj = getObjectTranslator(rel.getAssocObject());

    leftObj.addRelationship(rel.getLeftToRightSpec(), rel.getLeftToAssocSpec());
    rightObj.addRelationship(rel.getRightToLeftSpec(), rel.getRightToAssocSpec());

    assocObj.addRelationship(rel.getAssocToLeftSpec(), null);
    assocObj.addRelationship(rel.getAssocToRightSpec(), null);

  }

  private void createRelationship ( final NormalRelationshipDeclaration rel )
  {
    final ConcreteObjectTranslator leftObj = getObjectTranslator(rel.getLeftObject());
    final ConcreteObjectTranslator rightObj = getObjectTranslator(rel.getRightObject());

    leftObj.addRelationship(rel.getLeftToRightSpec(), null);
    rightObj.addRelationship(rel.getRightToLeftSpec(), null);
  }


  private void createRelationship ( final SubtypeRelationshipDeclaration rel )
  {
    final ConcreteObjectTranslator supObj = getObjectTranslator(rel.getSupertype());

    for ( final ObjectDeclaration subType : rel.getSubtypes() )
    {
      final ConcreteObjectTranslator subObj = getObjectTranslator(subType);

      supObj.addRelationship(rel.getSuperToSubSpec(subType), null);
      subObj.addRelationship(rel.getSubToSuperSpec(subType), null);

    }
  }

  public org.xtuml.masl.translate.main.DomainTranslator getMainDomainTranslator ()
  {
    return mainDomainTranslator;
  }

  public ConcreteObjectTranslator getObjectTranslator ( final ObjectDeclaration dec )
  {
    return objectTranslators.get(dec);
  }
}
