//
// File: UncheckedNameLookup.java
//
package org.xtuml.masl.metamodelImpl.name;

import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;


public class UncheckedNameLookup extends NameLookup
{

  public UncheckedNameLookup ()
  {
    super(SemanticErrorCode.NameRedefinition, SemanticErrorCode.NameNotFoundInScope);
  }

  public Name find ( final String name )
  {
    final Name result = super.find(name);
    if ( result != null )
    {
      return result;
    }
    else
    {
      // If the name is not found in scope, return a new unknown name reference
      System.out.println("Creating unknown reference: '" + name + "'");
      return UnknownName.create(name);
    }
  }

}
