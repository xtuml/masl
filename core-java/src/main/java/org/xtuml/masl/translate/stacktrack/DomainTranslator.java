//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.stacktrack;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;



@Alias("StackTrack")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator
{

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
  }

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

  @Override
  public void translate ()
  {
    for ( final ObjectDeclaration object : domain.getObjects() )
    {
      objectTranslators.put(object, new ObjectTranslator(object));
    }

    for ( final DomainTerminator object : domain.getTerminators() )
    {
      termTranslators.put(object, new TerminatorTranslator(object));
    }

    for ( final ObjectTranslator objectTranslator : objectTranslators.values() )
    {
      objectTranslator.translate();
    }

    for ( final TerminatorTranslator termTranslator : termTranslators.values() )
    {
      termTranslator.translate();
    }

    for ( final DomainService service : domain.getServices() )
    {
      final ActionTranslator serviceTranslator = new ActionTranslator(mainDomainTranslator.getServiceTranslator(service));
      serviceTranslator.translate();
    }

  }

  ObjectTranslator getObjectTranslator ( final ObjectDeclaration object )
  {
    return objectTranslators.get(object);
  }

  Map<ObjectDeclaration, ObjectTranslator>                    objectTranslators = new HashMap<ObjectDeclaration, ObjectTranslator>();
  Map<DomainTerminator, TerminatorTranslator>                 termTranslators   = new HashMap<DomainTerminator, TerminatorTranslator>();

  private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

}
