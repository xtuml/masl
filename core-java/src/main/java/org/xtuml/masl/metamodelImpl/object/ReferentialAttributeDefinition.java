//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.object;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;


public class ReferentialAttributeDefinition extends Positioned
    implements org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition
{

  private final RelationshipSpecification relationship;
  private final String                    attributeName;
  private AttributeDeclaration            destAttribute;

  public static ReferentialAttributeDefinition create ( final ObjectDeclaration sourceObject,
                                                        final RelationshipSpecification.Reference relRef,
                                                        final String attributeName )
  {
    if ( sourceObject == null || relRef == null )
    {
      return null;
    }

    return new ReferentialAttributeDefinition(relRef.getPosition(), relRef.getRelationshipSpec(), attributeName);
  }


  private ReferentialAttributeDefinition ( final Position position,
                                           final RelationshipSpecification relationship,
                                           final String attributeName )
  {
    super(position);
    this.relationship = relationship;
    this.attributeName = attributeName;
  }

  @Override
  public RelationshipSpecification getRelationship ()
  {
    return relationship;
  }

  private AttributeDeclaration parentAttribute;

  public AttributeDeclaration getParentAttribute ()
  {
    return parentAttribute;
  }

  @Override
  public AttributeDeclaration getDestinationAttribute ()
  {
    return destAttribute;
  }

  void linkDestination () throws SemanticError
  {
    // Can't do this on construction, as related objects may not have been fully
    // defined.

    final AttributeDeclaration formalisingAtt = relationship.getDestinationObject().getAttribute(attributeName);
    if ( !formalisingAtt.isIdentifier() )
    {
      throw new SemanticError(SemanticErrorCode.RefAttNotIdentifier,
                              Position.getPosition(attributeName),
                              attributeName,
                              relationship
                                          .getDestinationObject().getName());
    }
    else if ( !formalisingAtt.getType().equals(parentAttribute.getType()) )
    {
      throw new SemanticError(SemanticErrorCode.RefAttNotSameType,
                              Position.getPosition(attributeName),
                              attributeName,
                              relationship
                                          .getDestinationObject().getName());
    }
    else if ( relationship.getReverseSpec().requiresFormalising() )
    {
      throw new SemanticError(SemanticErrorCode.RefAttWrongEnd,
                              getPosition(),
                              relationship.getRelationship().getName(),
                              relationship
                                          .getDestinationObject().getName());
    }
    else if ( relationship.getReverseSpec().isFormalisingEnd() )
    {
      throw new SemanticError(SemanticErrorCode.RefAttBothEnds,
                              getPosition(),
                              relationship.getRelationship().getName(),
                              relationship.getFromObject().getName(),
                              relationship
                                          .getDestinationObject().getName());
    }
    destAttribute = formalisingAtt;
    relationship.setFormalisingEnd();
  }

  void setParentAttribute ( final AttributeDeclaration parentAttribute )
  {
    this.parentAttribute = parentAttribute;
  }

  @Override
  public String toString ()
  {
    return relationship + "." + attributeName;
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitReferentialAttributeDefinition(this, p);
  }

}
