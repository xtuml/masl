// 
// Filename : ProcessStatusListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public interface BacklogListener
    extends java.util.EventListener
{

  public void backlogChanged ( BacklogEvent e );
}
