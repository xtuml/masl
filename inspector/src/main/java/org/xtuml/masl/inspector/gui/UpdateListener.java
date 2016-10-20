// 
// Filename : UpdateListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

interface UpdateListener
    extends java.util.EventListener
{

  void updateStarted ();

  void updateComplete ();
}
