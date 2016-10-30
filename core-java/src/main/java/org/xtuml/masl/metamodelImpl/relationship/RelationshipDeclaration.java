//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.relationship;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;


public abstract class RelationshipDeclaration extends Positioned
    implements org.xtuml.masl.metamodel.relationship.RelationshipDeclaration
{

  public static Reference createReference ( final Domain.Reference domainRef, final String relName )
  {
    if ( domainRef == null || relName == null )
    {
      return null;
    }

    try
    {
      final Position position = domainRef.getPosition() == null ? Position.getPosition(relName) : domainRef.getPosition();
      return domainRef.getDomain().getRelationship(relName).getReference(position);

    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  public class Reference extends Positioned
  {

    private Reference ( final Position position )
    {
      super(position);
    }

    public RelationshipDeclaration getRelationship ()
    {
      return RelationshipDeclaration.this;
    }

  }

  public Reference getReference ( final Position position )
  {
    return new Reference(position);
  }

  private final String     name;
  private final PragmaList pragmas;

  RelationshipDeclaration ( final Position position, final Domain domain, final String name, final PragmaList pragmas )
  {
    super(position);
    this.domain = domain;
    this.pragmas = pragmas;
    this.name = name;
  }

  @Override
  public PragmaList getPragmas ()
  {
    return pragmas;
  }

  @Override
  public String getName ()
  {
    return name;
  }

  private final Domain domain;

  @Override
  public Domain getDomain ()
  {
    return domain;
  }

  @Override
  public String toString ()
  {
    return "relationship " + getName() + "\tis\t";
  }

}
