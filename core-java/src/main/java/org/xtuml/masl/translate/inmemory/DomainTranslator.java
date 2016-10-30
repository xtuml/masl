//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.inmemory;

import java.util.Arrays;
import java.util.Collection;

import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.ConcreteDomainTranslator;

import com.google.common.collect.Iterables;



@Alias("InMemory")
@Default
public class DomainTranslator extends ConcreteDomainTranslator
{

  @Override
  protected ObjectTranslator createTranslator ( final ObjectDeclaration object )
  {
    return new ObjectTranslator(object);
  }

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }


  private DomainTranslator ( final Domain domain )
  {
    super(domain);
    library = new SharedLibrary(mainDomainTranslator.getLibrary().getName() + "_" + getLibNameSuffix()).withDefaultHeaderPath(domain.getName() + "_OOA")
                                                                                                       .withCCDefaultExtensions().inBuildSet(mainDomainTranslator.getBuildSet());

    library.addDependency(Transient.popLibrary);
    library.addDependency(mainDomainTranslator.getLibrary());

    if ( getProperties().getProperty("standalone", "true") == "true" &&
         mainDomainTranslator.getProperties().getProperty("standalone", "true") == "true" )
    {
      standaloneDeps = new InterfaceLibrary(mainDomainTranslator.getLibrary().getName() + "_"
                                                                         + getLibNameSuffix()
                                  + "_standalone_deps").inBuildSet(getMainDomainTranslator().getBuildSet());
      standaloneExecutable = new Executable(mainDomainTranslator.getLibrary().getName() + "_"
                                                                         + getLibNameSuffix()
                                  + "_standalone").asPrivate().inBuildSet(mainDomainTranslator.getBuildSet()).withCCDefaultExtensions();
      standaloneExecutable.addDependency(standaloneDeps);
      standaloneDeps.addDependency(library);
    }
    else
    {
      standaloneDeps = null;
      standaloneExecutable = null;
    }
  }

  @Override
  public void translate ()
  {
    super.translate();
    if ( Iterables.isEmpty(library.getBodyFiles()) )
    {
      // Some build systems complain if there's not at least one file in a library
      library.createBodyFile("transient_dummy");
    }
    if ( standaloneExecutable != null )
    {
      standaloneExecutable.includeGroup(mainDomainTranslator.getStandaloneExecutableSkeleton());

      for ( final Domain refDomain : domain.getReferencedInterfaces() )
      {
        standaloneDeps.addDependency(DomainTranslator.getInstance(refDomain).standaloneDeps);
      }
    }
  }

  @Override
  protected void translateAuxiliaryFiles ( final Domain domain )
  {
  }

  @Override
  public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites ()
  {
    return Arrays.<org.xtuml.masl.translate.DomainTranslator>asList(mainDomainTranslator);
  }


  @Override
  public String getLibNameSuffix ()
  {
    return "transient";
  }

  @Override
  public Library getLibrary ()
  {
    return library;
  }

  private final Library    library;
  private final Library    standaloneDeps;
  private final Library standaloneExecutable;


}
