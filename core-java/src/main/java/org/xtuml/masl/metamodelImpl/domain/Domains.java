//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.domain;

import java.util.Collection;

import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;


public class Domains
{

  private static CheckedLookup<Domain> domains = new CheckedLookup<Domain>(SemanticErrorCode.DomainAlreadyDefined,
                                                                           SemanticErrorCode.DomainNotFound);

  public static void addDomain ( final Domain domain )
  {
    if ( domain == null )
    {
      return;
    }

    try
    {
      domains.put(domain.getName(), domain);
    }
    catch ( final AlreadyDefined e )
    {
      e.report();
    }

  }

  public static Domain findDomain ( final String name )
  {
    return domains.find(name);
  }

  public static Domain getDomain ( final String name ) throws NotFound
  {
    return domains.get(name);
  }

  public static Collection<Domain> getDomains ()
  {
    return domains.asList();
  }
}
