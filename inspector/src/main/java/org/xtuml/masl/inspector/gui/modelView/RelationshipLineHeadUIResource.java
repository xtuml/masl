// 
// Filename : RelationshipLineHeadUIResource.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import javax.swing.UIManager;


public class RelationshipLineHeadUIResource
    implements javax.swing.plaf.UIResource
{

  private final java.awt.Shape arrowHead;
  private final String         cardinality;

  public RelationshipLineHeadUIResource ( final java.awt.Shape arrowHead, final String cardinality )
  {
    this.arrowHead = arrowHead;
    this.cardinality = cardinality;
  }

  public java.awt.Shape getArrowHead ()
  {
    return arrowHead;
  }

  public String getCardinality ()
  {
    return cardinality;
  }

  public static RelationshipLineHeadUIResource getLineHead ( final boolean many, final boolean conditional )
  {
    if ( many )
    {
      if ( conditional )
      {
        return (RelationshipLineHeadUIResource)UIManager.get("Relationship.Mc");
      }
      else
      {
        return (RelationshipLineHeadUIResource)UIManager.get("Relationship.M");
      }
    }
    else if ( conditional )
    {
      return (RelationshipLineHeadUIResource)UIManager.get("Relationship.1c");
    }
    else
    {
      return (RelationshipLineHeadUIResource)UIManager.get("Relationship.1");
    }
  }
}
