//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.relationship;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public class AssociativeRelationshipDeclaration extends RelationshipDeclaration
    implements org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration
{

  private final ObjectDeclaration assocObject;
  private final HalfRelationship  leftToRight;
  private final HalfRelationship  rightToLeft;


  public static void create ( final Position position,
                                              final Domain domain,
                                              final String name,
                                              final HalfRelationship leftToRight,
                                              final HalfRelationship rightToLeft,
                                              final ObjectNameExpression assocObject,
                                              final PragmaList pragmas )
  {
    if ( domain == null || leftToRight == null || rightToLeft == null || assocObject == null )
    {
      return;
    }

    try
    {
      domain.addRelationship(new AssociativeRelationshipDeclaration(position,
                                                                    domain,
                                                                    name,
                                                                    leftToRight,
                                                                    rightToLeft,
                                                                    assocObject.getObject(),
                                                                    pragmas));
    }
    catch ( final SemanticError e )
    {
      e.report();
    }
  }


  private AssociativeRelationshipDeclaration ( final Position position,
                                               final Domain domain,
                                               final String name,
                                               final HalfRelationship leftToRight,
                                               final HalfRelationship rightToLeft,
                                               final ObjectDeclaration assocObject,
                                               final PragmaList pragmas )
  {
    super(position, domain, name, pragmas);
    this.leftToRight = leftToRight;
    this.rightToLeft = rightToLeft;
    this.assocObject = assocObject;
    this.leftToRightSpec = new RelationshipSpecification(this, leftToRight);
    this.rightToLeftSpec = new RelationshipSpecification(this, rightToLeft);
    this.leftToAssocSpec = new RelationshipSpecification(this,
                                                         leftToRight.getFromObject().getObject(),
                                                         leftToRight.isConditional(),
                                                         leftToRight.getRole(),
                                                         leftToRight.getMult(),
                                                         assocObject);
    this.assocToLeftSpec = new RelationshipSpecification(this,
                                                         assocObject,
                                                         false,
                                                         rightToLeft.getRole(),
                                                         MultiplicityType.ONE,
                                                         rightToLeft.getToObject().getObject());
    this.rightToAssocSpec = new RelationshipSpecification(this,
                                                          rightToLeft.getFromObject().getObject(),
                                                          rightToLeft.isConditional(),
                                                          rightToLeft.getRole(),
                                                          rightToLeft.getMult(),
                                                          assocObject);
    this.assocToRightSpec = new RelationshipSpecification(this,
                                                          assocObject,
                                                          false,
                                                          leftToRight.getRole(),
                                                          MultiplicityType.ONE,
                                                          leftToRight.getToObject().getObject());


    leftToRightSpec.setReverseSpec(rightToLeftSpec);
    rightToLeftSpec.setReverseSpec(leftToRightSpec);

    leftToRightSpec.setAssocSpec(leftToAssocSpec);
    rightToLeftSpec.setAssocSpec(rightToAssocSpec);

    leftToAssocSpec.setNonAssocSpec(leftToRightSpec);
    rightToAssocSpec.setNonAssocSpec(rightToLeftSpec);

    leftToAssocSpec.setReverseSpec(assocToLeftSpec);
    assocToLeftSpec.setReverseSpec(leftToAssocSpec);

    rightToAssocSpec.setReverseSpec(assocToRightSpec);
    assocToRightSpec.setReverseSpec(rightToAssocSpec);

    assocToLeftSpec.setRequiresFormalising();
    assocToRightSpec.setRequiresFormalising();

    getLeftObject().addRelationship(getLeftToRightSpec());
    getLeftObject().addRelationship(getLeftToAssocSpec());
    getRightObject().addRelationship(getRightToLeftSpec());
    getRightObject().addRelationship(getRightToAssocSpec());
    getAssocObject().addRelationship(getAssocToLeftSpec());
    getAssocObject().addRelationship(getAssocToRightSpec());

  }

  private final RelationshipSpecification leftToRightSpec;
  private final RelationshipSpecification rightToLeftSpec;
  private final RelationshipSpecification leftToAssocSpec;
  private final RelationshipSpecification rightToAssocSpec;
  private final RelationshipSpecification assocToRightSpec;
  private final RelationshipSpecification assocToLeftSpec;

  @Override
  public RelationshipSpecification getLeftToRightSpec ()
  {
    return leftToRightSpec;
  }

  @Override
  public RelationshipSpecification getRightToLeftSpec ()
  {
    return rightToLeftSpec;
  }

  @Override
  public RelationshipSpecification getLeftToAssocSpec ()
  {
    return leftToAssocSpec;
  }

  @Override
  public RelationshipSpecification getRightToAssocSpec ()
  {
    return rightToAssocSpec;
  }

  @Override
  public RelationshipSpecification getAssocToRightSpec ()
  {
    return assocToRightSpec;
  }

  @Override
  public RelationshipSpecification getAssocToLeftSpec ()
  {
    return assocToLeftSpec;
  }

  @Override
  public ObjectDeclaration getLeftObject ()
  {
    return rightToLeft.getToObject().getObject();
  }

  @Override
  public String getRightRole ()
  {
    return leftToRight.getRole();
  }

  @Override
  public org.xtuml.masl.metamodel.relationship.MultiplicityType getRightMult ()
  {
    return leftToRight.getMult().getType();
  }

  @Override
  public boolean getRightConditional ()
  {
    return leftToRight.isConditional();
  }

  @Override
  public ObjectDeclaration getRightObject ()
  {
    return leftToRight.getToObject().getObject();
  }

  @Override
  public String getLeftRole ()
  {
    return rightToLeft.getRole();
  }

  @Override
  public org.xtuml.masl.metamodel.relationship.MultiplicityType getLeftMult ()
  {
    return rightToLeft.getMult().getType();
  }

  @Override
  public boolean getLeftConditional ()
  {
    return rightToLeft.isConditional();
  }

  @Override
  public ObjectDeclaration getAssocObject ()
  {
    return assocObject;
  }

  @Override
  public String toString ()
  {
    return super.toString() + leftToRight + "\n" + "\t\t" + rightToLeft + "\n" + "\t\tusing " + assocObject + ";\n" + getPragmas();
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.vistAssociativeRelationshipDeclaration(this, p);
  }

}
