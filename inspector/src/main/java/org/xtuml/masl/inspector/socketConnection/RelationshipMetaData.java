// 
// Filename : RelationshipMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;


public class RelationshipMetaData extends org.xtuml.masl.inspector.processInterface.RelationshipMetaData
    implements ReadableObject
{

  private int            archId;
  private String         number;
  private int            leftObject;
  private int            rightObject;
  private String         leftRole;
  private String         rightRole;
  private boolean        leftMany;
  private boolean        rightMany;
  private boolean        leftConditional;
  private boolean        rightConditional;
  private int            assocObject;
  private boolean        isAssoc;

  private DomainMetaData domain = null;

  @Override
  public DomainMetaData getDomain ()
  {
    return domain;
  }

  public void setDomain ( final DomainMetaData domain )
  {
    this.domain = domain;
  }


  public int getArchId ()
  {
    return archId;
  }

  @Override
  public String getNumber ()
  {
    return number;
  }

  @Override
  public ObjectMetaData getLeftObject ()
  {
    return getDomain().getObject(leftObject);
  }

  @Override
  public ObjectMetaData getRightObject ()
  {
    return getDomain().getObject(rightObject);
  }

  @Override
  public String getLeftRole ()
  {
    return leftRole;
  }

  @Override
  public String getRightRole ()
  {
    return rightRole;
  }

  @Override
  public boolean getLeftMany ()
  {
    return leftMany;
  }

  @Override
  public boolean getRightMany ()
  {
    return rightMany;
  }

  @Override
  public boolean getLeftConditional ()
  {
    return leftConditional;
  }

  @Override
  public boolean getRightConditional ()
  {
    return rightConditional;
  }

  @Override
  public ObjectMetaData getAssocObject ()
  {
    return isAssoc ? getDomain().getObject(assocObject) : null;
  }

  @Override
  public boolean getAssocMany ()
  {
    return false;
  }


  @Override
  public org.xtuml.masl.inspector.processInterface.RelationshipData getRelationshipData ()
  {
    return new RelationshipData(this);
  }

  public void read ( final CommunicationChannel channel ) throws IOException
  {
    archId = channel.readInt();
    number = channel.readString();
    leftObject = channel.readInt();
    rightRole = channel.readString();
    rightMany = channel.readBoolean();
    rightConditional = channel.readBoolean();
    rightObject = channel.readInt();
    leftRole = channel.readString();
    leftMany = channel.readBoolean();
    leftConditional = channel.readBoolean();
    isAssoc = channel.readBoolean();
    if ( isAssoc )
    {
      assocObject = channel.readInt();
    }
  }
}
