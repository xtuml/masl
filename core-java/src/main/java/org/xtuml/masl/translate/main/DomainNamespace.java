//
// File: DomainNamespace.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.metamodel.domain.Domain;



public class DomainNamespace
{

  static Map<Domain, Namespace> namespaces = new HashMap<Domain, Namespace>();

  public static Namespace get ( final Domain domain )
  {
    Namespace result = namespaces.get(domain);
    if ( result == null )
    {
      result = new Namespace(Mangler.mangleName(domain));
      namespaces.put(domain, result);
    }
    return result;
  }


}
