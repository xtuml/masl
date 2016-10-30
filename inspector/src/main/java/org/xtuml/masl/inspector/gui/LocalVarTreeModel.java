// 
// Filename : LocalVarTreeModel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.xtuml.masl.inspector.processInterface.LocalVarData;


public class LocalVarTreeModel
    implements TreeModel
{

  private LocalVarNode        rootNode;

  protected EventListenerList listenerList = new EventListenerList();

  public LocalVarTreeModel ()
  {
    rootNode = new LocalVarNode(0, "root", new LocalVarData[0]);
  }

  public void setLocalVars ( final LocalVarData[] vars )
  {
    rootNode = new LocalVarNode(0, "root", vars);
    fireTreeChanged(new TreeModelEvent(this, new Object[]
      { rootNode }));
  }

  public Object getRoot ()
  {
    return rootNode;
  }

  public Object getChild ( final Object parent, final int index )
  {
    return ((LocalVarNode)parent).getChild(index);
  }

  public int getChildCount ( final Object parent )
  {
    return ((LocalVarNode)parent).getChildCount();
  }

  public boolean isLeaf ( final Object node )
  {
    return ((LocalVarNode)node).isLeaf();
  }

  public int getIndexOfChild ( final Object parent, final Object node )
  {
    return ((LocalVarNode)node).getIndex();
  }

  public TreePath getPathForNames ( final String[] names )
  {
    final List<LocalVarNode> objects = new ArrayList<LocalVarNode>(names.length);

    LocalVarNode currentNode = rootNode;

    int i = 1;
    while ( currentNode != null )
    {
      objects.add(currentNode);
      if ( i < names.length )
      {
        currentNode = currentNode.getChild(names[i++]);
      }
      else
      {
        currentNode = null;
      }
    }

    return new TreePath(objects.toArray());
  }

  public void valueForPathChanged ( final TreePath path, final Object newValue )
  {
  }

  public void addTreeModelListener ( final TreeModelListener l )
  {
    listenerList.add(TreeModelListener.class, l);
  }

  public void removeTreeModelListener ( final TreeModelListener l )
  {
    listenerList.remove(TreeModelListener.class, l);
  }

  public TreeModelListener[] getTreeModelListeners ()
  {
    return listenerList.getListeners(
                       TreeModelListener.class);
  }

  public void fireTreeChanged ( final TreeModelEvent e )
  {
    // Guaranteed to return a non-null array
    final Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for ( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if ( listeners[i] == TreeModelListener.class )
      {
        ((TreeModelListener)listeners[i + 1]).treeStructureChanged(e);
      }
    }
  }
}
