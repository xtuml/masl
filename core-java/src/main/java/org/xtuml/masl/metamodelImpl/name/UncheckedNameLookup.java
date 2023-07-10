//
// File: UncheckedNameLookup.java
//
// In some contexts, it is necessary to reference an element by name that is
// defined in the scope (e.g. pragma values). In these cases, an unchecked name
// lookup can be used which is guaranteed to return a name. If a real name is
// not found in the scope, an unknown name is returned whose references resolve
// to a literal string containing the name itself.
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
